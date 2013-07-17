package jwiki.decorator;

import java.util.List;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;
import jwiki.core.IWikiWriter;
import jwiki.core.WikiUtil;

/**
 * DefaultDecorator
 * @author kazuhiko arase
 */
public class DefaultDecorator extends AbstractDecorator {

	public String pattern() {
		return "^(.+)$";
	}

	public void render(
		IWikiContext context,
		List<ILine<String[]>> groupList,
		IWikiWriter out
	) throws Exception {

		out.write("<p>");

		for (ILine<String[]> group : groupList) {
			WikiUtil.writeStyled(out, context, group.get()[1]);
			out.write("<br/>");
		}
		
		out.write("</p>");
	}
}

