package jwiki.core;

import java.io.Writer;

/**
 * ILinkDecorator
 * @author kazuhiko arase
 */
public interface ILinkDecorator {
	String getScheme();
	void render(IWikiContext context, ILink link,
			Writer out) throws Exception;
}
