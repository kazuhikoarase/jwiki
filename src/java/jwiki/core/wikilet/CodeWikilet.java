package jwiki.core.wikilet;

import java.io.Writer;
import java.util.List;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;
import jwiki.core.IWikilet;
import jwiki.core.WikiUtil;

/**
 * CodeWikilet
 * @author kazuhiko arase
 */
public class CodeWikilet implements IWikilet {

	public String pattern() {
		return "^```$";
	}
	
	public String endPattern() {
		return pattern();
	}
	
	public void render(
		IWikiContext context,
		List<ILine<String[]>> groupList,
		Writer out
	) throws Exception {

		out.write("<p class=\"jwiki-code-block jwiki-code\">");
		for (int i = 1; i < groupList.size(); i += 1) {
			ILine<String[]> group = groupList.get(i);
			if (group.get()[0].matches(endPattern() ) ) {
				break;
			}
			WikiUtil.writeEscaped(out, group.get()[0], true);
			out.write("<br/>");
		}
		out.write("</p>");
	}
}

