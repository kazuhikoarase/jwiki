package jwiki.decorator.link;

import java.io.Writer;

import jwiki.core.ILink;
import jwiki.core.IWikiContext;
import jwiki.core.Util;
import jwiki.core.WikiUtil;

/**
 * XrefDecorator
 * @author kazuhiko arase
 */
public class XrefDecorator extends AbstractLinkDecorator {

	public String getScheme() {
		return "xref:";
	}
	
	public void render(IWikiContext context, ILink link,
			Writer out) throws Exception {

		out.write("<a href=\"#");
		WikiUtil.writeEscaped(out, link.getPath() );
		out.write("\">");
		WikiUtil.writeEscaped(out, link.getPath() );
		if (!Util.isEmpty(link.getLabel() ) ) {
			out.write(' ');
			WikiUtil.writeEscaped(out, link.getLabel() );
		}
		out.write("</a>");
	}
}