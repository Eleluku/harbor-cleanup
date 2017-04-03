package model;

public class Version implements Comparable<Version> {

	/* format1: Mainversion.Subversion-Buildnumber-"SNAPSHOT" */
	/* format2: "Dev"-Buildnumber-"SNAPSHOT" */
	private String version;

	public Version(String version) {
		super();
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	private String[] getVersionAsStringArray() {
		String[] versionArray = new String[3];
		if (!this.version.contains("latest") && !this.version.contains("Dev")) {
			versionArray[0] = String.valueOf(getMainVersion());
			versionArray[1] = String.valueOf(getSubVersion());
			versionArray[2] = String.valueOf(getBuildNumber());
		} else if (this.version.contains("Dev")) {
			versionArray = new String[1];
			versionArray[0] = String.valueOf(getBuildNumber());
		}
		return versionArray;
	}

	public int getMainVersion() {
		if (!this.version.contains("latest")) {
			String[] versionSplit = this.version.split("\\.");
			int mainVersion = Integer.parseInt(versionSplit[0]);
			return mainVersion;
		} else {
			// TODO useful return value for latest
			return -1;
		}
	}

	public int getSubVersion() {
		if (!this.version.contains("latest")) {
			String[] versionSplit = this.version.split("\\.");
			int subVersion = Integer.parseInt(versionSplit[1].split("-")[0]);
			return subVersion;
		} else {
			// TODO useful return value for latest
			return -1;
		}
	}

	public int getBuildNumber() {
		if (!this.version.contains("latest")) {
			String[] versionSplit = this.version.split("-", 3);
			int mainVersion = Integer.parseInt(versionSplit[1]);
			return mainVersion;
		} else {
			// TODO useful return value for latest
			return -1;
		}
	}

	@Override
	public String toString() {
		return this.version;
	}

	@Override
	public int compareTo(Version that) {
		if (that == null || this.getVersion().contains("latest"))
			return 1;
		if (that.getVersion().contains("latest"))
			return -1;
		String[] thisParts = this.getVersionAsStringArray();
		String[] thatParts = that.getVersionAsStringArray();
		int length = Math.max(thisParts.length, thatParts.length);
		for (int i = 0; i < length; i++) {
			int thisPart = i < thisParts.length ? Integer.parseInt(thisParts[i]) : 0;
			int thatPart = i < thatParts.length ? Integer.parseInt(thatParts[i]) : 0;
			if (thisPart < thatPart)
				return -1;
			if (thisPart > thatPart)
				return 1;
		}
		return 0;
	}

	@Override
	public boolean equals(Object that) {
		if (this == that)
			return true;
		if (that == null)
			return false;
		if (this.getClass() != that.getClass())
			return false;
		return this.compareTo((Version) that) == 0;
	}

}
