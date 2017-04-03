package cleanup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import model.Project;
import model.Repository;
import model.Version;

public class Cleaner {

	Project[] projects;

	RestTemplate restTemplate;
	HttpEntity<String> entity;

	/**
	 * Creates a new cleaner instance.
	 * 
	 * @param user
	 *            the username to authenticate with the harbor api
	 * @param password
	 *            the password to authenticate with the harbor api
	 */
	public Cleaner(String user, String password) {
		this.restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();

		String authString = user + ":" + password;
		byte[] authEncoded = Base64.encodeBase64(authString.getBytes());
		headers.set("Authorization", "Basic " + new String(authEncoded));

		this.entity = new HttpEntity<String>(headers);
	}

	/**
	 * Cleans the registry of old tags. The number of deleted tags is determined
	 * in Config.java.
	 */
	public void clean() {

		// getting all projects from server.
		this.getAllProjects();

		// printing current status
		System.out.println("Current Projects:");
		for (Project p : this.projects) {
			System.out.println(p.toString());
		}

		// deleting tags
		for (Project p : this.projects) {
			if (Config.deleteTagsFromDev) {
				this.deleteTags("dev", p);
			}
			if (Config.deleteTagsFromStable) {
				this.deleteTags("stable", p);
			}
		}
	}

	/**
	 * Calls the harbor api and sets the project instance variable.
	 */
	private void getAllProjects() {
		// sending the request to get all projects
		ResponseEntity<Project[]> reponseProjects = restTemplate.exchange(Config.harborApiUrl + "projects",
				HttpMethod.GET, entity, Project[].class);
		// parse all projects into array
		Project[] projects = reponseProjects.getBody();

		// getting repositories for projects
		for (Project p : projects) {
			ResponseEntity<String[]> responseRepositories = restTemplate.exchange(
					Config.harborApiUrl + "/repositories?project_id=" + p.getProjectId(), HttpMethod.GET, entity,
					String[].class);

			// check if the project has repositories, add them to array
			ArrayList<Repository> allRepos = new ArrayList<Repository>();
			for (String repositoryName : responseRepositories.getBody()) {

				Repository repo = new Repository();
				repo.setName(repositoryName);
				allRepos.add(repo);
			}

			// get tags for every repository
			for (Repository repo : allRepos) {
				ResponseEntity<String[]> responseTags = restTemplate.exchange(
						Config.harborApiUrl + "/repositories/tags?repo_name=" + repo.getName(), HttpMethod.GET, entity,
						String[].class);

				if (responseTags.getBody().length > 0) {

					Version[] allVersions = new Version[responseTags.getBody().length];

					for (int i = 0; i < responseTags.getBody().length; i++) {
						Version version = new Version(responseTags.getBody()[i]);
						allVersions[i] = version;
					}

					// sorting versions
					Arrays.sort(allVersions);
					repo.setVersions(allVersions);
				}
			}

			p.setRepositories(allRepos);
		}
		this.projects = projects;
	}

	/**
	 * Deletes tags from a given project repository Only works for repositories
	 * containing "dev" or "stable" in their name
	 * 
	 * @param repoName
	 *            the repository's name
	 * @param project
	 *            the project's name
	 */
	private void deleteTags(String repoName, Project project) {

		// find the repository in the project
		for (Repository repo : project.getRepositories()) {
			if (repo.getName().contains(repoName)) {

				Version[] versions = repo.getVersions();

				if (repoName.contains("stable")) {

					// split into arrays by MainVersion.SubVersion
					List<Version[]> versionsSplit = this.splitVersions(versions);

					// delete tags
					for (int i = 0; i < versionsSplit.size(); i++) {
						if (i < versionsSplit.size() - 1) {
							// old main.sub version
							deleteTagsFromProjectRepository(project, repo, versionsSplit.get(i),
									Config.tagsToKeepOldStable);
						} else {
							// current main.sub version
							deleteTagsFromProjectRepository(project, repo, versionsSplit.get(i),
									Config.tagsToKeepCurrentStable);
						}
					}
				} else if (repoName.contains("dev")) {
					// delete tags
					deleteTagsFromProjectRepository(project, repo, versions, Config.tagsToKeepDev);
				}
			}
		}
	}

	/**
	 * Splits a list of versions into multiple Arrays. The versions are split by
	 * mainversion.subversion.
	 * 
	 * @param versions
	 *            all versions to be split.
	 * @return List of Arrays with the split versions.
	 */
	private List<Version[]> splitVersions(Version[] versions) {
		// sort array
		Arrays.sort(versions);

		List<Version[]> versionsSplit = new ArrayList<Version[]>();

		// check nullPointer, empty array and only one element.
		if (versions == null || versions.length <= 1) {
			if (versions.length == 1) {
				versionsSplit.add(versions);
			}
			return versionsSplit;
		}

		// compare pair of mainversion and subversion
		int splitAtIndex = 0;
		for (int i = 0; i < versions.length; i++) {

			// check if end of array is reached and add the rest.
			if (i == versions.length - 1) {
				versionsSplit.add(Arrays.copyOfRange(versions, splitAtIndex, i + 1));
				break;
			}
			if (versions[i].getMainVersion() < versions[i + 1].getMainVersion()
					|| versions[i].getSubVersion() < versions[i + 1].getSubVersion()) {

				// jump in main or sub version detected, split
				versionsSplit.add(Arrays.copyOfRange(versions, splitAtIndex, i + 1));
				splitAtIndex = i + 1;
			}
		}

		return versionsSplit;
	}

	/**
	 * Deletes tags from a given project repository and subversion. A given
	 * number of tags is kept.
	 * 
	 * @param project
	 *            the project.
	 * @param repo
	 *            the repository.
	 * @param currentSubVersion
	 *            the current subversion.
	 * @param tagsToKeep
	 *            number of tags to keep.
	 */
	private void deleteTagsFromProjectRepository(Project project, Repository repo, Version[] currentSubVersion,
			int tagsToKeep) {

		// check nullPointer
		if (currentSubVersion == null) {
			System.out.println("No versions given.");
			return;
		}

		// sorting array again, just to be sure
		Arrays.sort(currentSubVersion);

		// loop through versions and deleting
		for (int i = 0; i < currentSubVersion.length - tagsToKeep; i++) {
			String requestUrl = Config.harborApiUrl + "repositories?repo_name=" + repo.getName() + "&tag="
					+ currentSubVersion[i].getVersion();
			System.out.println(requestUrl);
			// TODO send DELETE request to requestUrl

		}
	}

}
