package jwiki.decorator.link;

import java.io.Writer;
import java.net.URLEncoder;

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
			ILink link, Writer out) throws Exception {
		String label = buildLabel(link);
		out.write("<a href=\"#");
		out.write(URLEncoder.encode(label, "UTF-8") );
		out.write("\">");
		WikiUtil.writeEscaped(out, label);
		out.write("</a>");
	}
}