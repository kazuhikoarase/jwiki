package jwiki.fs.impl;

import java.util.Map;

import jwiki.fs.IContent;

/**
 * Content
 * @author kazuhiko arase
 */
public class Content implements IContent {
	public static final Content EMPTY =
		new Content(null, new byte[0], null);
	private final String id;
	private final byte[] data;
	private final Map<String,String> properties;
	public Content(String id,
			byte[] data, Map<String,String> properties) {
		this.id = id;
		this.data = data;
		this.properties = properties;
	}
	public String getId() {
		return id;
	}
	public byte[] getData() {
		return data;
	}
	public Map<String,String> getProperties() {
		return properties;
	}
}