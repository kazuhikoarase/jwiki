package jwiki.core.impl;

import jwiki.core.ILink;

public class Link implements ILink {
	private final String path;
	private final String label;
	public Link(String path, String label) {
		this.path = path;
		this.label = label;
	}
	public String getPath() {
		return path;
	}
	public String getLabel() {
		return label;
	}
}