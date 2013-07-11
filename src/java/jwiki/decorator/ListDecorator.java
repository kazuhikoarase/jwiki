package jwiki.decorator;

import java.io.Writer;
import java.util.List;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;
import jwiki.core.IParagraphDecorator;
import jwiki.core.Util;
import jwiki.core.WikiUtil;

/**
 * ListDecorator
 * @author kazuhiko arase
 */
public class ListDecorator implements IParagraphDecorator {

	public String pattern() {
		return "^(\\s+)(\\*|[0-9]+\\.)\\s+(.+)$";
	}
	
	public String endPattern(ILine<String[]> startGroup) {
		return null;
	}
	
	public void render(
		IWikiContext context,
		List<ILine<String[]>> groupList,
		Writer out
	) throws Exception {

		IndentContext ic = new IndentContext();
		int indent = -1;

		for (ILine<String[]> group : groupList) {
			
			final int newIndent = group.get()[1].length(); 
			final String symbol = group.get()[2];
			final String desc = group.get()[3];
			
			if (indent == newIndent) {
			} else if (indent < newIndent) {
				String tag = symbol.equals("*") ? "ul" : "ol";
				ic.push(out, newIndent, tag , null);
			} else {
				while (indent > newIndent && ic.size() > 1) {
					indent = ic.pop(out);
				}
			}
			
			out.write("<li>");
			WikiUtil.writeStyled(out, context, Util.trim(desc) );
			out.write("</li>");

			indent = newIndent;
		}
		while (ic.size() > 0) {
			ic.pop(out);
		}
	}

	
}

