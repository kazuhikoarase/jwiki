package jwiki.decorator.link;

import java.io.Writer;

import jwiki.core.ILink;
import jwiki.core.IWikiContext;
import jwiki.core.Util;
import jwiki.core.WikiUtil;

/**
 * ImageLinkDecorator
 * @author kazuhiko arase
 */
public class ImageLinkDecorator extends AbstractLinkDecorator {

	public String getScheme() {
		return "image:";
	}
	
	@SuppressWarnings("unused")
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
		
		out.write("<img src=\"");
		out.write(context.createPathUrlEncoded(path) );
		out.write("?raw\" alt=\"");
		WikiUtil.writeEscaped(out, label);
		out.write("\"/>");
	}
}