package jwiki.core;


/**
 * IWikiPage
 * @author kazuhiko arase
 */
public interface IWikiPage {
	String getPath();
	void render(IWikiWriter out, String plainText) throws Exception;
	void writeControls(IWikiWriter out) throws Exception;
	void writeWikiPage(IWikiWriter out) throws Exception;
}