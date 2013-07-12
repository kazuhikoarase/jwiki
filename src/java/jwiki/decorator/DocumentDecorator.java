package jwiki.decorator;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;
import jwiki.core.Util;

/**
 * DocumentDecorator
 * @author kazuhiko arase
 */
public class DocumentDecorator extends AbstractDecorator {

	public String pattern() {
		return "^(\\>+)\\s*([0-9\\-]*\\))?(.*)$";
	}

	@Override
	public List<String> normalize(IWikiContext context,
			List<ILine<String[]>> groupList) throws Exception {
	
		IndentContext ic = new IndentContext();
		int lastIndent = -1;
		
		List<String> list = new ArrayList<String>();

		for (ILine<String[]> group : groupList) {
			final String leading = group.get()[1];
			final int indent = leading.length(); 
			final String header = group.get()[2]; 
			final String desc = group.get()[3]; 

			if (lastIndent == indent) {
			} else if (lastIndent < indent) {
				ic.push(indent, null, null, null);
			} else {
				while (lastIndent > indent && ic.size() > 1) {
					ic.pop(null);
					lastIndent = ic.peek().getIndent(); 
				}
			}
			
			Integer id = (Integer)ic.peek().getAttribute("id");
			if (id == null) {
				id = Integer.valueOf(0);
			}
			id = Integer.valueOf(id.intValue() + 1);
			ic.peek().setAttribute("id", id);
			
			StringBuilder buf = new StringBuilder();
			buf.append(leading);
			buf.append(' ');
			if (header != null) {
				for (int i = 0; i < ic.size(); i += 1) {
					if (i > 0) {
						buf.append('-');
					}
					buf.append(ic.get(i).getAttribute("id") );
				}
				buf.append(')');
//				buf.append(header);
			}
			buf.append(desc);
			list.add(buf.toString() );
			
			lastIndent = indent;
		}

		return list;
	}

	public void render(
		IWikiContext context,
		List<ILine<String[]>> groupList,
		Writer out
	) throws Exception {

		final String tag = "div";
		final String attrs = "class=\"jwiki-doc\"";

		IndentContext ic = new IndentContext();
		int lastIndent = -1;

		StringBuilder buf = new StringBuilder();
		
		for (ILine<String[]> group : groupList) {

			final int indent = group.get()[1].length(); 
			final String header = group.get()[2]; 
			final String desc = group.get()[3]; 

			if (lastIndent != indent) {
				if (!Util.isEmpty(buf) ) {
					context.render(out, buf.toString() );
					buf = new StringBuilder();
				}
			}

			if (lastIndent == indent) {
			} else if (lastIndent < indent) {
				ic.push(indent, out, tag, attrs);
			} else {
				while (lastIndent > indent && ic.size() > 1) {
					ic.pop(out);
					lastIndent = ic.peek().getIndent(); 
				}
			}

			if (header != null) {
				if (lastIndent >= indent) {
					if (!Util.isEmpty(buf) ) {
						context.render(out, buf.toString() );
						buf = new StringBuilder();
					}
					ic.pop(out);
					ic.push(indent, out, tag, attrs);
				}
				buf.append("**");
				buf.append(Util.rtrim(header) );
				buf.append("**");
				buf.append('\u0020');
			}
			buf.append(Util.rtrim(desc) );
			buf.append('\n');

			lastIndent = indent;
		}

		if (!Util.isEmpty(buf) ) {
			context.render(out, buf.toString() );
		}
		
		while (ic.size() > 0) {
			ic.pop(out);
		}
	}
}

