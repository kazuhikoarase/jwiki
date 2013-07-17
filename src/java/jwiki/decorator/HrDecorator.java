package jwiki.decorator;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;
import jwiki.core.IWikiWriter;

/**
 * HrDecorator
 * @author kazuhiko arase
 */
public class HrDecorator extends SimpleDecorator {

	public String pattern() {
		return "^\\-{2,}$";
	}
	
	public void render(
		IWikiContext context,
		ILine<String[]> group,
		IWikiWriter out
	) throws Exception {
		out.write("<hr/>");
	}
}