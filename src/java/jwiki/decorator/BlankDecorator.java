package jwiki.decorator;

import java.io.Writer;
import java.util.List;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;
import jwiki.core.IParagraphDecorator;

/**
 * BlankDecorator
 * @author kazuhiko arase
 */
public class BlankDecorator implements IParagraphDecorator {

	public String pattern() {
		return "^$";
	}
	
	public String endPattern(ILine<String[]> startGroup) {
		return null;
	}
	
	public void render(
		IWikiContext context,
		List<ILine<String[]>> groupList,
		Writer out
	) throws Exception {
	}
}
