package cleanup;

public interface Config {

	/** the url to the harbor api */
	static public String harborApiUrl = "http://192.168.20.130/api/";

	static public int tagsToKeepDev = 1;
	static public int tagsToKeepCurrentStable = 2;
	static public int tagsToKeepOldStable = 1;

	static public boolean deleteTagsFromStable = true;
	static public boolean deleteTagsFromDev = true;

}
