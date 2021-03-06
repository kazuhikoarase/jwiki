package jwiki.decorator.link;

import jwiki.core.ILinkDecorator;
import jwiki.core.IWikiContext;
import jwiki.core.IWikiWriter;
import jwiki.core.PathUtil;

/**
 * AbstractLinkDecorator
 * @author kazuhiko arase
 */
public abstract class AbstractLinkDecorator implements ILinkDecorator {

	protected AbstractLinkDecorator() {
	}

	protected void writeUnknownLink(
		IWikiContext context,
		String path,
		String label,
		IWikiWriter out
	) throws Exception {
		out.writeEscaped(label);
		out.write("<a href=\"");
		out.write(context.createPathUrlEncoded(path) );
		out.write("?v=e\">?</a>");
	}
	
	protected String toCanonicalPath(
		IWikiContext context,
		String path
	) throws Exception {
		if (path.startsWith("/") ) {
			// '/' で開始する場合、絶対パス
			return PathUtil.trim(path);
		} else {
			// '/' 以外で開始する場合、相対パスとして解釈
			return PathUtil.buildPath(
				PathUtil.getParent(context.getPath() ), path);
		}
	}
}