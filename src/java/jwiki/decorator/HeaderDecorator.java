package jwiki.decorator;

import java.net.URLEncoder;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;
import jwiki.core.IWikiWriter;
import jwiki.core.Util;

/**
 * HeaderDecorator
 * @author kazuhiko arase
 */
public class HeaderDecorator extends SimpleDecorator {
	
	public String pattern() {
		return "^(#{1,6})(.+)$";
	}

	public void render(
		IWikiContext context,
		ILine<String[]> group,
		IWikiWriter out
	) throws Exception {

		String header = group.get()[1];
		String text = group.get()[2];
		if (text.endsWith(header) ) {
			text = text.substring(0, text.length() - header.length() );
		}
		text = Util.trim(text);

		out.write("<a name=\"");
		out.write(URLEncoder.encode(text, "UTF-8") );
		out.write("\"></a>");

		String tag = "h" + header.length();
		out.write("<");
		out.write(tag);
		out.write(">");
		out.writeEscaped(text);
		out.write("</");
		out.write(tag);
		out.write(">");

	}
}