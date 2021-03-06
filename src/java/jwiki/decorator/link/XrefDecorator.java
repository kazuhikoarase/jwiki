package jwiki.decorator.link;

import java.net.URLEncoder;

import jwiki.core.ILink;
import jwiki.core.IWikiContext;
import jwiki.core.IWikiWriter;
import jwiki.core.Util;

/**
 * XrefDecorator
 * @author kazuhiko arase
 */
public class XrefDecorator extends AbstractLinkDecorator {

	public String getScheme() {
		return "xref:";
	}
	
	private String buildLabel(ILink link) {
		StringBuilder buf = new StringBuilder();
		buf.append(link.getPath() );
		if (!Util.isEmpty(link.getLabel() ) ) {
			buf.append('\u0020');
			buf.append(link.getLabel() );
		}
		return buf.toString();
	}
	
	public void render(IWikiContext context,
			ILink link, IWikiWriter out) throws Exception {
		String label = buildLabel(link);
		out.write("<a href=\"#");
		out.write(URLEncoder.encode(label, "UTF-8") );
		out.write("\">");
		out.writeEscaped(label);
		out.write("</a>");
	}
}