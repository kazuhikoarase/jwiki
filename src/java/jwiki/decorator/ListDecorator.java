package jwiki.decorator;

import java.util.List;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;
import jwiki.core.IWikiWriter;
import jwiki.core.Util;
import jwiki.core.WikiUtil;

/**
 * ListDecorator
 * @author kazuhiko arase
 */
public class ListDecorator extends AbstractDecorator {

	public String pattern() {
		return "^(\\s+)(\\*|[0-9]+\\.)\\s+(.+)$";
	}

	public void render(
		IWikiContext context,
		List<ILine<String[]>> groupList,
		IWikiWriter out
	) throws Exception {

		IndentContext ic = new IndentContext();
		int lastIndent = -1;

		for (ILine<String[]> group : groupList) {
			
			final int indent = group.get()[1].length(); 
			final String symbol = group.get()[2];
			final String desc = group.get()[3];
			
			if (lastIndent == indent) {
			} else if (lastIndent < indent) {
				String tag = symbol.equals("*") ? "ul" : "ol";
				ic.push(indent, tag , null, out);
			} else {
				while (lastIndent > indent && ic.size() > 1) {
					ic.pop(out);
					lastIndent = ic.peek().getIndent(); 
				}
			}
			
			out.write("<li>");
			WikiUtil.writeStyled(out, context, Util.trim(desc) );
			out.write("</li>");

			lastIndent = indent;
		}

		while (ic.size() > 0) {
			ic.pop(out);
		}
	}

	
}

