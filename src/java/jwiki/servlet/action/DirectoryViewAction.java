package jwiki.servlet.action;

import java.io.Writer;

import jwiki.core.PathUtil;
import jwiki.fs.IContent;

/**
 * DirectoryViewAction
 * @author kazuhiko arase
 */
public class DirectoryViewAction extends WikiAction {

	public void writeControls(Writer out) throws Exception {
		
		// index
		String indexPath = PathUtil.buildPath(context.getPath(), "index");

		writeLinkButton(out,
			context.createPathUrlEncoded(indexPath) + "?v=e",
			context.getString("label.edit") );
		
		out.write("<span class=\"jwiki-spacer\">|</span>");
		
		writeLinkButton(out,
			context.createPathUrlEncoded(
        	PathUtil.buildPath(context.getPath(), 
        			context.getString("label.new_page") ) ) + "?v=e",
        			context.getString("label.new_page") );
	}

	public void writeWikiPage(Writer out) throws Exception {

		// index
		String indexPath = PathUtil.buildPath(context.getPath(), "index");

		IContent content = context.get(indexPath, -1);
		if (content.getData().length > 0) {
			context.setPath(indexPath);
			context.render(out, dataToString(content.getData() ) );
		} else {
			context.render(out, "[[index]]");
		}
	}
}