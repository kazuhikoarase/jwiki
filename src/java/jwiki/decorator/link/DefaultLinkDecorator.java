package jwiki.decorator.link;

import jwiki.core.ILink;
import jwiki.core.IWikiContext;
import jwiki.core.IWikiWriter;
import jwiki.core.Util;

/**
 * DefaultLinkDecorator
 * @author kazuhiko arase
 */
public class DefaultLinkDecorator extends AbstractLinkDecorator {
	
	public String getScheme() {
		return "";
	}
	
	public void render(IWikiContext context, ILink link,
			IWikiWriter out) throws Exception {
		
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
		out.writeEscaped(label);
		out.write("</a>");
	}
}