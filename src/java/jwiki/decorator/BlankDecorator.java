package jwiki.decorator;

import java.io.Writer;
import java.util.List;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;

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
		Writer out
	) throws Exception {
	}
}
