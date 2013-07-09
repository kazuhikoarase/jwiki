package jwiki.core;

import java.util.List;

/**
 * IWikiRendererWorker
 * @author kazuhiko arase
 */
public interface IWikiRendererWorker {
	void render(IWikiContext context, IWikilet wikilet,
		List<ILine<String[]>> groupList) throws Exception;
}