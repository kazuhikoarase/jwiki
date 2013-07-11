package jwiki.core;

import java.util.List;

/**
 * IWikiRendererWorker
 * @author kazuhiko arase
 */
public interface IWikiRendererWorker {
	void render(IWikiContext context, IParagraphDecorator decorators,
		List<ILine<String[]>> groupList) throws Exception;
}