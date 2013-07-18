package jwiki.decorator;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jwiki.core.ILink;
import jwiki.core.ILinkDecorator;
import jwiki.core.IWikiContext;
import jwiki.core.IWikiWriter;
import jwiki.core.Util;
import jwiki.core.impl.Link;

/**
 * StyleUtil
 * @author kazuhiko arase
 */
public class StyleUtil {
	
	private StyleUtil() {
	}
	
	private static final String BOLD = "**";
	private static final String STRIKE = "--";
	private static final String UNDERLINE = "__";

	private static final String OPEN_TAG = "[[";
	private static final String CLOSE_TAG = "]]";

	public static void writeStyled(
		final IWikiWriter out,
		final IWikiContext context,
		final String plainText
	) throws Exception {

		final Stack<String> stack = new Stack<String>();
		int index = 0;
		
		while (index < plainText.length() ) {

			if (plainText.charAt(index) == '\\' && index + 1 < plainText.length() ) {
				out.writeEscaped(plainText.charAt(index + 1) );
				index += 2;
			} else if (stack.size() > 0 &&
					startsWith(plainText, stack.peek(), index) ) {
				index += stack.pop().length();
				out.write("</span>");
			} else if (startsWith(plainText, BOLD, index) ) {
				stack.push(BOLD);
				out.write("<span class=\"jwiki-bold\">");
				index += BOLD.length();
			} else if (startsWith(plainText, STRIKE, index) ) {
				stack.push(STRIKE);
				out.write("<span class=\"jwiki-strike\">");
				index += STRIKE.length();
			} else if (startsWith(plainText, UNDERLINE, index) ) {
				stack.push(UNDERLINE);
				out.write("<span class=\"jwiki-underline\">");
				index += UNDERLINE.length();
			} else if (startsWith(plainText, OPEN_TAG, index) ) {
				
				final int start = index + OPEN_TAG.length();
				int end = start;
				String url = null;
				
				while (end < plainText.length() ) {
					if (startsWith(plainText, CLOSE_TAG, end) ) {
						url = Util.trim(plainText.substring(start, end) );
						break;
					}
					end += 1;
				}

				if (!Util.isEmpty(url) ) {
					writeLink(out, context, url);
					index = end + CLOSE_TAG.length();
				} else {
					out.writeEscaped(plainText.charAt(index) );
					index += 1;
				}

			} else {	
				out.writeEscaped(plainText.charAt(index) );
				index += 1;
			}
		}

		while (stack.size() > 0) {
			stack.pop();
			out.write("</span>");
		}
	}

	private static void writeLink(
		final IWikiWriter out,
		final IWikiContext context,
		String url
	) throws Exception {

		final int index = url.indexOf('\u0020');
		
		String label = "";
		
		if (index != -1) {
			label = url.substring(index + 1);
			url = url.substring(0, index);
		}

		label = Util.trim(label);
		url = Util.trim(url);

		// scheme:path
		final Pattern pat = Pattern.compile("^([A-Za-z]+\\:)?(.+)$");
		final Matcher mat = pat.matcher(url);
		if (!mat.find() ) {
			out.writeEscaped(url);
			return;
		}
		
		final String scheme = Util.coalesce(mat.group(1), "");
		final String path = mat.group(2);

		final ILink link = new Link(path, label);
		for (final ILinkDecorator decorator :
				context.getLinkDecorators() ) {
			if (decorator.getScheme().equals(scheme) ) {
				decorator.render(context, link, out);
				return;
			}
		}

		// not found.
		out.writeEscaped(url);
	}

	private static boolean startsWith(
		final String s,
		final String c,
		final int index
	) {
		return index + c.length() <= s.length() &&
			s.substring(index, index + c.length() ).equals(c);
	}
}