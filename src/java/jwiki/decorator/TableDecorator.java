package jwiki.decorator;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;
import jwiki.core.Util;
import jwiki.core.WikiUtil;

/**
 * TableDecorator
 * @author kazuhiko arase
 */
public class TableDecorator extends AbstractDecorator {

	public String pattern() {
		return "^\\s*\\|\\|(.+)\\|\\|$";
	}

	public void render(
		IWikiContext context,
		List<ILine<String[]>> groupList,
		Writer out
	) throws Exception {
		
		List<List<String>> table = new ArrayList<List<String>>();
		for (ILine<String[]> group : groupList) {
			table.add(Util.strictSplit(group.get()[1], "||") );
		}
		int maxCols = 0;
		for (List<String> row : table) {
			maxCols = Math.max(maxCols, row.size() );
		}

		Pattern spcPattern = Pattern.compile("^(\\s*).+?(\\s*)$");
		String[] styleClasses = new String[maxCols];
		
		out.write("<table class=\"jwiki-solid\">");
		for (List<String> row : table) {
			
			out.write("<tr>");

			String tag = "td";
			
			for (int i = 0; i < maxCols; i += 1) {
				
				String item = "";

				if (i < row.size() ) {
					item = row.get(i);
				}
				if (item.startsWith("|") ) {
					if (i == 0) {
						tag = "th";
					} else {
						styleClasses[i] = "jwiki-thick";
					}
					item = item.substring(1);
				}

				out.write("<");
				out.write(tag);
				
				if (!Util.isEmpty(styleClasses[i]) ) {
					out.write(" class=\"");
					out.write(styleClasses[i]);
					out.write("\"");
				}
				
				Matcher mat = spcPattern.matcher(item);
				if (mat.find() ) {
					out.write(" style=\"text-align:");
					int leftSpc = mat.group(1).length();
					int rightSpc = mat.group(2).length();
					if (leftSpc < rightSpc) {
						out.write("left");
					} else if (leftSpc > rightSpc) {
						out.write("right");
					} else {
						out.write("center");
					}
				}

				out.write(";\">");
				WikiUtil.writeStyled(out, context, item);
				out.write("</");
				out.write(tag);
				out.write(">");
			}
			out.write("</tr>");
		}
		out.write("</table>");
	}
}

