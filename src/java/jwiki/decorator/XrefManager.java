package jwiki.decorator;

import java.util.HashMap;
import java.util.Map;

public class XrefManager {

	private Map<String,Info> xref = new HashMap<String,Info>(); 
	
	public Object getAnchor(String curValue, String label, String newValue) {
		xref.put(curValue, new Info(label, newValue) );
		return new LazyAnchor(curValue);
	}
	
	public Object getRef(String curValue) {
		return new LazyRef(curValue);
	}
	
	private static class Info {
		private final String label;
		private final String value;
		public Info(String label, String value) {
			this.label = label;
			this.value = value;
		}
		public String getLabel() {
			return label;
		}
		public String getValue() {
			return value;
		}
	}
	
	private class LazyAnchor {
		private final String curValue;
		public LazyAnchor(String curValue) {
			this.curValue = curValue;
		}
		public String toString() {
			Info info = xref.get(curValue);
			return info != null? info.getValue() : curValue;
		}
	}

	private class LazyRef {
		private final String curValue;
		public LazyRef(String curValue) {
			this.curValue = curValue;
		}
		public String toString() {
			Info info = xref.get(curValue);
			return info != null? 
				info.getValue() + ' ' + info.getLabel() : curValue;
		}
	}
}