package jwiki.core.wikilet;

import java.io.Writer;
import java.util.List;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;
import jwiki.core.IWikilet;
import jwiki.core.Util;

/**
 * DocumentWikilet
 * @author kazuhiko arase
 */
public class DocumentWikilet implements IWikilet {

	public String pattern() {
		return "^\\s*(\\|+)\\s*([0-9\\-]*\\))?(.*)$";
	}
	
	public String endPattern() {
		return null;
	}
	
	public void render(
		IWikiContext context,
		List<ILine<String[]>> groupList,
		Writer out
	) throws Exception {

		String tag = "div";
		String attrs = " style=\"margin: 4px 2px 4px 8px;" +
			" padding: 2px;" +
			" border-width: 1px 1px 1px 4px;" +
			" border-style: solid;" +
			" border-color: #666699;\"";

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

