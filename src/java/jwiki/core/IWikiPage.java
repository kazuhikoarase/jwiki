package jwiki.core;

import java.io.Writer;

/**
 * IWikiPage
 * @author kazuhiko arase
 */
public interface IWikiPage {
	String getPath();
	void render(Writer out, String plainText) throws Exception;
	void writeControls(Writer out) throws Exception;
	void writeWikiPage(Writer out) throws Exception;
}