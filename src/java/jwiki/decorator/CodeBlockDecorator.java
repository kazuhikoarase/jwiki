package jwiki.decorator;

import java.io.Writer;
import java.util.List;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;
import jwiki.core.WikiUtil;

/**
 * CodeBlockDecorator
 * @author kazuhiko arase
 */
public class CodeBlockDecorator extends AbstractDecorator {

	public String pattern() {
		return "^```|\\{\\{\\{$";
	}
	
	public String endPattern(ILine<String[]> startGroup) {
		if (startGroup.get()[0].equals("{{{") ) {
			return "^\\}\\}\\}$";
		} else {
			return "^```$";
		}
	}
	
	public void render(
		IWikiContext context,
		List<ILine<String[]>> groupList,
		Writer out
	) throws Exception {

		out.write("<p class=\"jwiki-code-block jwiki-code\">");
		ILine<String[]> startGroup = groupList.get(0);
		for (int i = 1; i < groupList.size(); i += 1) {
			ILine<String[]> group = groupList.get(i);
			if (group.get()[0].matches(endPattern(startGroup) ) ) {
				break;
			}
			WikiUtil.writeEscaped(out, group.get()[0], true);
			out.write("<br/>");
		}
		out.write("</p>");
	}
}

