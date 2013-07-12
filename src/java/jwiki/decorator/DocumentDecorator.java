package jwiki.decorator;

import java.io.Writer;
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
		return "^\\s*(\\|+)\\s*([0-9\\-]*\\))?(.*)$";
	}

	public void render(
		IWikiContext context,
		List<ILine<String[]>> groupList,
		Writer out
	) throws Exception {

		final String tag = "div";
		final String attrs = " class=\"jwiki-doc\"";

		IndentContext ic = new IndentContext();
		int indent = -1;

		StringBuilder buf = null;
		
		for (ILine<String[]> group : groupList) {

			final int newIndent = group.get()[1].length(); 
			final String caption = group.get()[2]; 
			final String desc = group.get()[3]; 

			if (buf == null || indent != newIndent) {
				if (buf != null) {
					context.render(out, buf.toString() );
				}
				buf = new StringBuilder();
			}

			if (indent == newIndent) {
			} else if (indent < newIndent) {
				ic.push(out, indent, tag, attrs);
			} else {
				while (indent > newIndent && ic.size() > 1) {
					indent = ic.pop(out);
				}
			}

			if (caption != null) {
				if (!(indent < newIndent) ) {
					if (buf != null) {
						context.render(out, buf.toString() );
					}
					buf = new StringBuilder();
					ic.pop(out);
					ic.push(out, indent, tag, attrs);
				}
				buf.append("**");
				buf.append(Util.rtrim(caption) );
				buf.append("**");
				buf.append('\u0020');
			}
			buf.append(Util.rtrim(desc) );
			buf.append('\n');

			indent = newIndent;
		}

		if (buf != null) {
			context.render(out, buf.toString() );
		}
		
		while (ic.size() > 0) {
			ic.pop(out);
		}
	}
}

