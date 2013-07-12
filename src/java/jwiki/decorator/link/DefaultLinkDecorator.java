package jwiki.decorator.link;

import java.io.Writer;

import jwiki.core.ILink;
import jwiki.core.IWikiContext;
import jwiki.core.Util;
import jwiki.core.WikiUtil;

public class DefaultLinkDecorator extends AbstractLinkDecorator {
	
	public String getScheme() {
		return "";
	}
	
	public void render(IWikiContext context, ILink link,
			Writer out) throws Exception {
		
		String path = toCanonicalPath(context, link.getPath() );

		if (!context.getFile(path, -1).exists() ) {
			writeUnknownLink(out, context, path, link.getLabel() );
			return;
		}

		out.write("<a href=\"");
		out.write(context.createPathUrlEncoded(path) );
		if (!Util.isEmpty(link.getQuery() ) ) {
			out.write(link.getQuery() );
		}
		out.write("\">");
		WikiUtil.writeEscaped(out, link.getLabel() );
		out.write("</a>");
	}
}