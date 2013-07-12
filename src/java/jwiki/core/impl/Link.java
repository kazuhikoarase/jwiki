package jwiki.core.impl;

import jwiki.core.ILink;

public class Link implements ILink {
	private final String path;
	private final String query;
	private final String label;
	public Link(String path, String query, String label) {
		this.path = path;
		this.query = query;
		this.label = label;
	}
	public String getPath() {
		return path;
	}
	public String getQuery() {
		return query;
	}
	public String getLabel() {
		return label;
	}
}