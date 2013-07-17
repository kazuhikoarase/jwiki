package jwiki.core;


/**
 * ILinkDecorator
 * @author kazuhiko arase
 */
public interface ILinkDecorator {
	String getScheme();
	void render(IWikiContext context, ILink link,
			IWikiWriter out) throws Exception;
}
