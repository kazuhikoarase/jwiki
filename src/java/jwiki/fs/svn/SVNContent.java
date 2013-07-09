package jwiki.fs.svn;

import java.util.Map;

import jwiki.fs.IContent;

/**
 * SVNContent
 * @author kazuhiko arase
 */
public class SVNContent implements IContent {
	public static final SVNContent EMPTY =
		new SVNContent(-1, new byte[0], null);
	private final long revision;
	private final byte[] data;
	private final Map<String,String> properties;
	public SVNContent(long revision,
			byte[] data, Map<String,String> properties) {
		this.revision = revision;
		this.data = data;
		this.properties = properties;
	}
	public long getRevision() {
		return revision;
	}
	public byte[] getData() {
		return data;
	}
	public Map<String,String> getProperties() {
		return properties;
	}
}