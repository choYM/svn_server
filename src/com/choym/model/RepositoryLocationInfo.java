package com.choym.model;

/**
 * SVN repository location information
 * 
 * @author choYM
 * @since 2016-10-05
 */
public class RepositoryLocationInfo {
	private String url;
	private String label;
	private String username;
	private String password;

	public RepositoryLocationInfo() {
		super();
	}

	public RepositoryLocationInfo(String url, String label, String username, String password) {
		super();
		this.url = url;
		this.label = label;
		this.username = username;
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
