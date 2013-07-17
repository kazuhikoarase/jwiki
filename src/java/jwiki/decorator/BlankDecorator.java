package jwiki.decorator;

import java.util.List;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;
import jwiki.core.IWikiWriter;

/**
 * BlankDecorator
 * @author kazuhiko arase
 */
public class BlankDecorator extends AbstractDecorator {

	public String pattern() {
		return "^$";
	}

	public void render(
		IWikiContext context,
		List<ILine<String[]>> groupList,
		IWikiWriter out
	) throws Exception {
	}
}
