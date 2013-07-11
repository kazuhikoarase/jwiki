package jwiki.decorator;

import java.io.Writer;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;

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
		Writer out
	) throws Exception {
		out.write("<hr/>");
	}
}