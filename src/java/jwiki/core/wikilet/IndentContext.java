package jwiki.core.wikilet;

import java.io.Writer;
import java.util.Stack;

class IndentContext {
		
	private Stack<IndentInfo> stack = new Stack<IndentInfo>();

	public void push(Writer out, 
			int indent, String tag, String attrs) throws Exception {
		stack.push(new IndentInfo(indent, tag) );
		out.write('<');
		out.write(tag);
		if (attrs != null) {
			out.write(attrs);
		}
		out.write('>');
	}

	public int pop(Writer out) throws Exception {
		IndentInfo li = stack.pop();
		out.write('<');
		out.write('/');
		out.write(li.getTag() );
		out.write('>');
		return li.getIndent();
	}
	
	public int size() {
		return stack.size();
	}

	private static class IndentInfo {
		private final int indent;
		private final String tag;
		public IndentInfo(int indent, String tag) {
			this.indent = indent;
			this.tag = tag;
		}
		public int getIndent() {
			return indent;
		}
		public String getTag() {
			return tag;
		}
	}
}