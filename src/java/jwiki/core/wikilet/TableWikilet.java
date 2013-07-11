package jwiki.core.wikilet;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;
import jwiki.core.IWikilet;
import jwiki.core.Util;
import jwiki.core.WikiUtil;

/**
 * TableWikilet
 * @author kazuhiko arase
 */
public class TableWikilet implements IWikilet {

	public String pattern() {
		return "^\\s*\\|\\|(.+)\\|\\|$";
	}
	
	public String endPattern(ILine<String[]> startGroup) {
		return null;
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
		
		out.write("<table class=\"jwiki-solid\">");
		for (List<String> row : table) {
			out.write("<tr>");
			for (int i = 0; i < maxCols; i += 1) {
				if (i < row.size() ) {
					String item = row.get(i);
					boolean header = item.matches("^\\s*\\*.*\\*\\s*$");
					String tag = header? "th" : "td";
					out.write("<");
					out.write(tag);
					
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
				} else {
					out.write("<td></td>");
				}
			}
			out.write("</tr>");
		}
		out.write("</table>");
	}
}

