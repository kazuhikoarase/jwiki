package jwiki.decorator;

import java.io.Writer;
import java.util.List;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;

/**
 * SimpleDecorator
 * @author kazuhiko arase
 */
public abstract class SimpleDecorator extends AbstractDecorator {

	protected SimpleDecorator() {
	}

	public void render(
		IWikiContext context,
		List<ILine<String[]>> groupList,
		Writer out
	) throws Exception {
		for (ILine<String[]> group : groupList) {
			render(context, group, out);
		}
	}

	public abstract void render(IWikiContext context,
			ILine<String[]> group, Writer out) throws Exception;
}