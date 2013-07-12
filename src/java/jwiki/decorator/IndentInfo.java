package jwiki.decorator;

import java.util.HashMap;
import java.util.Map;

public class IndentInfo {
	private final int indent;
	private final String tag;
	private final Map<String, Object> attributes;
	protected IndentInfo(int indent, String tag) {
		this.indent = indent;
		this.tag = tag;
		this.attributes = new HashMap<String, Object>();
	}
	public int getIndent() {
		return indent;
	}
	public String getTag() {
		return tag;
	}
	public Object getAttribute(String name) {
		return attributes.get(name);
	}
	public void setAttribute(String name, Object value) {
		attributes.put(name, value);
	}
}
