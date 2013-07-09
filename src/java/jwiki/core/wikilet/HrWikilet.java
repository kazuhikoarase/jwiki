package jwiki.core.wikilet;

import java.io.Writer;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;

/**
 * HrWikilet
 * @author kazuhiko arase
 */
public class HrWikilet extends SimpleWikilet {

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