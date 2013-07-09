package jwiki.core.wikilet;

import java.io.Writer;
import java.util.List;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;
import jwiki.core.IWikilet;

/**
 * BlankWikilet
 * @author kazuhiko arase
 */
public class BlankWikilet implements IWikilet {

	public String pattern() {
		return "^$";
	}
	
	public String endPattern() {
		return null;
	}
	
	public void render(
		IWikiContext context,
		List<ILine<String[]>> groupList,
		Writer out
	) throws Exception {
	}
}
