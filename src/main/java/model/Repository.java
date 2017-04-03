package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Repository {

	private String name;
	private Version[] versions;

	public Repository() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Version[] getVersions() {
		if (versions != null) {
			return versions;
		} else {
			return new Version[0];
		}

	}

	public void setVersions(Version[] versions) {
		this.versions = versions;
	}

	@Override
	public String toString() {
		String versionString = "";
		if (versions != null) {
			for (int i = 0; i < versions.length; i++) {
				versionString += versions[i].toString() + "\n";
			}
		}

		return "Repository name: " + name + "\n" + "Tags: \n" + versionString;
	}

}
