package jwiki.decorator.link;

import java.io.Writer;

import jwiki.core.ILink;
import jwiki.core.IWikiContext;
import jwiki.core.Util;
import jwiki.core.WikiUtil;

/**
 * DefaultLinkDecorator
 * @author kazuhiko arase
 */
public class DefaultLinkDecorator extends AbstractLinkDecorator {
	
	public String getScheme() {
		return "";
	}
	
	public void render(IWikiContext context, ILink link,
			Writer out) throws Exception {
		
		String path = link.getPath();
		String label = Util.coalesce(link.getLabel(), link.getPath() );
		
		int index;

		String hash = null;
		index = path.indexOf('#');
		if (index != -1) {
			hash = path.substring(index);
			path = path.substring(0, index);
		}

		String query = null;
		index = path.indexOf('?');
		if (index != -1) {
			query = path.substring(index);
			path = path.substring(0, index);
		}
		

		path = toCanonicalPath(context, path);

		if (!context.getFile(path, -1).exists() ) {
			writeUnknownLink(context, path, label, out);
			return;
		}

		out.write("<a href=\"");
		out.write(context.createPathUrlEncoded(path) );
		if (!Util.isEmpty(query) ) {
			out.write(query);
		}
		if (!Util.isEmpty(hash) ) {
			out.write(hash);
		}
		out.write("\">");
		WikiUtil.writeEscaped(out, label);
		out.write("</a>");
	}
}