package jwiki.decorator;

import java.util.Stack;

import jwiki.core.IWikiWriter;

@SuppressWarnings("serial")
public class IndentContext extends Stack<IndentInfo> {

	public IndentContext() {
	}

	public void push(int indent, String tag, String attrs, IWikiWriter out) throws Exception {
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

	public IndentInfo pop(IWikiWriter out) throws Exception {
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