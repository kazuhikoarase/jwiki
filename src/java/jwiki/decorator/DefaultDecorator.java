package jwiki.decorator;

import java.io.Writer;
import java.util.List;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;
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

