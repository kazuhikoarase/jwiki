package jwiki.decorator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;
import jwiki.core.IWikiWriter;
import jwiki.core.Util;
import jwiki.core.WikiUtil;

/**
 * TableDecorator
 * @author kazuhiko arase
 */
public class TableDecorator extends AbstractDecorator {

	private Pattern spcPattern = Pattern.compile("^(\\s*).+?(\\s*)$");

	public String pattern() {
		return "^\\s*\\|\\|(.+)\\|\\|$";
	}

	public void render(
		IWikiContext context,
		List<ILine<String[]>> groupList,
		IWikiWriter out
	) throws Exception {
		
		List<List<String>> table = new ArrayList<List<String>>();
		for (ILine<String[]> group : groupList) {
			table.add(Util.strictSplit(group.get()[1], "||") );
		}
		int maxCols = 0;
		for (List<String> row : table) {
			maxCols = Math.max(maxCols, row.size() );
		}

		String[] styleClasses = new String[maxCols];
		
		out.write("<table class=\"jwiki-solid\">");
		for (List<String> row : table) {
			
			out.write("<tr>");

			String tag = "td";
			
			for (int col = 0; col < maxCols; col += 1) {
				
				String item = "";

				if (col < row.size() ) {
					item = row.get(col);
				}
				if (item.startsWith("|") ) {
					item = item.substring(1);
					if (col == 0) {
						tag = "th";
					} else {
						styleClasses[col] = "jwiki-thick";
					}
				}

				out.write("<");
				out.write(tag);
				
				if (!Util.isEmpty(styleClasses[col]) ) {
					out.write(" class=\"");
					out.write(styleClasses[col]);
					out.write("\"");
				}
				
				Matcher mat = spcPattern.matcher(item);
				if (mat.find() ) {
					out.write(" style=\"text-align:");
					out.write(getTextAlign(mat) );
					out.write(";\"");
				}

				out.write(">");
				
				WikiUtil.writeStyled(out, context, item);

				out.write("</");
				out.write(tag);
				out.write(">");
			}
			out.write("</tr>");
		}
		out.write("</table>");
	}
	
	private String getTextAlign(Matcher mat) {
		int leftSpc = mat.group(1).length();
		int rightSpc = mat.group(2).length();
		if (leftSpc < rightSpc) {
			return "left";
		} else if (leftSpc > rightSpc) {
			return "right";
		} else {
			return "center";
		}
	}
}

