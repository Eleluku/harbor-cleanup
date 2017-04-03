package model;


import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Project {

	@JsonProperty("project_id")
	private int projectId;
	
	@JsonProperty("owner_id")
	private int ownerId;
	
	private String name;
	
	@JsonProperty("creation_time")
	private String creationTime;
	
	@JsonProperty("creation_time_str")
	private String creationTimeString;
	
	private int deleted;
	
	@JsonProperty("owner_name")
	private String ownerName;
	
	/** 1 := public, 0 := private */
	private int isPublic;
	
	@JsonProperty("Togglable")
	private boolean isToggleable;
	
	@JsonProperty("update_time")
	private String updateTime;
	
	@JsonProperty("current_user_role_id")
	private int currentUserRoleId;
	
	@JsonProperty("repo_count")
	private int repoCount;
	
	private ArrayList<Repository> repositories;
	
	public Project(){
		
	}

	public ArrayList<Repository> getRepositories() {
		return repositories;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public int getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}

	public String getCreationTimeString() {
		return creationTimeString;
	}

	public void setCreationTimeString(String creationTimeString) {
		this.creationTimeString = creationTimeString;
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public int getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(int isPublic) {
		this.isPublic = isPublic;
	}

	public boolean isToggleable() {
		return isToggleable;
	}

	public void setToggleable(boolean isToggleable) {
		this.isToggleable = isToggleable;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public int getCurrentUserRoleId() {
		return currentUserRoleId;
	}

	public void setCurrentUserRoleId(int currentUserRoleId) {
		this.currentUserRoleId = currentUserRoleId;
	}

	public int getRepoCount() {
		return repoCount;
	}

	public void setRepoCount(int repoCount) {
		this.repoCount = repoCount;
	}

	public void setRepositories(ArrayList<Repository> repositories) {
		this.repositories = repositories;
	}
	
	@Override
	public String toString(){
		String repositoryString = "";
		
		if(repositories.size() > 0){
			for(Repository repo: repositories){
				repositoryString += repo.toString();
			}
		} else {
			repositoryString = "No repositories in this project.\n";
		}
		
		return "Project ID: " + this.projectId + "\nProject name: " + this.name + "\n" 
				+ "Repositories:\n" + repositoryString + "-------------------------\n";
	}
	
	
	
}
