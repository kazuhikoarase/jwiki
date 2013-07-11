package jwiki.core.wikilet;

import java.io.Writer;
import java.util.List;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;
import jwiki.core.IWikilet;
import jwiki.core.WikiUtil;

/**
 * DefaultWikilet
 * @author kazuhiko arase
 */
public class DefaultWikilet implements IWikilet {

	public String pattern() {
		return "^(.+)$";
	}
	
	public String endPattern(ILine<String[]> startGroup) {
		return null;
	}
	
	public void render(
		IWikiContext context,
		List<ILine<String[]>> groupList,
		Writer out
	) throws Exception {

		out.write("<p>");

		for (ILine<String[]> group : groupList) {
			WikiUtil.writeStyled(out, context, group.get()[1]);
			out.write("<br/>");
		}
		
		out.write("</p>");
	}
}

