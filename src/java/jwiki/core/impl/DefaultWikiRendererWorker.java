package jwiki.core.impl;

import java.io.Writer;
import java.util.List;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;
import jwiki.core.IWikiRendererWorker;
import jwiki.core.IParagraphDecorator;
import jwiki.core.WikiUtil;

/**
 * DefaultWikiRendererWorker
 * @author kazuhiko arase
 */
public class DefaultWikiRendererWorker
implements IWikiRendererWorker {
	
	private final Writer out;
	
	public DefaultWikiRendererWorker(Writer out) {
		this.out = out;
	}

	public void render(
		IWikiContext context,
		IParagraphDecorator decorator,
		List<ILine<String[]>> groupList
	) throws Exception {
		
		try {
			decorator.render(context, groupList, out);
		} catch(Exception e) {

			e.printStackTrace();

			for (ILine<String[]> group : groupList) {
				WikiUtil.writeEscaped(out, group.get()[0]);
			}
		}
	}
}