package jwiki.decorator;

import java.util.HashMap;
import java.util.Map;

public class XrefManager {

	private Map<String,Info> xref = new HashMap<String,Info>(); 
	
	public void putRef(String curValue, String label, String newValue) {
		if (xref.containsKey(curValue) ) {
			// 先勝ち
			return;
		}
		xref.put(curValue, new Info(label, newValue) );
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