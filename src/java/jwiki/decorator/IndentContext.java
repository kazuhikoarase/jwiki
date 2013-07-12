package jwiki.decorator;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class IndentContext extends Stack<IndentInfo> {

	public IndentContext() {
	}

	public void push(int indent, Writer out, String tag, String attrs) throws Exception {
		push(new IndentInfo(indent, tag) );
		if (out != null) {
			out.write('<');
			out.write(tag);
			if (attrs != null) {
				out.write('\u0020');
				out.write(attrs);
			}
			out.write('>');
		}
	}

	public IndentInfo pop(Writer out) throws Exception {
		IndentInfo indexInfo = pop();
		if (out != null) {
			out.write('<');
			out.write('/');
			out.write(indexInfo.getTag() );
			out.write('>');
		}
		return indexInfo;
	}
}