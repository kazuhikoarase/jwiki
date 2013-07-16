package jwiki.core;

import java.io.IOException;
import java.io.Writer;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jwiki.core.impl.Link;

/**
 * WikiUtil
 * @author kazuhiko arase
 */
public class WikiUtil {

	private WikiUtil() {
	}

	private static final char HT = 0x09;
	private static final char LF = 0x0a;
	private static final char SP = 0x20;
	
	private static final String NBSP = "&#160;";
	
	public static void writeEscaped(
		final Writer out,
		final char c
	) throws IOException {
		writeEscaped(out, c, false);
	}
	
	public static void writeEscaped(
		final Writer out,
		final char c,
		final boolean pre
	) throws IOException {

		if (pre && preformat(out, c) ) {
			// preformat
			return;
		}

		if (c == '<') {
			out.write("&lt;");
		} else if (c == '>') {
			out.write("&gt;");
		} else if (c == '&') {
			out.write("&amp;");
		} else if (c == '"') {
			out.write("&quot;");
		} else {
			out.write(c);
		}
	}
	
	private static boolean preformat(
		final Writer out,
		final char c
	) throws IOException {
		if (c == HT) {
			out.write(NBSP);
			out.write(NBSP);
			out.write(NBSP);
			out.write(NBSP);
			return true;
		} else if (c == LF) {
			out.write("<br/>");
			return true;
		} else if (c == SP) {
			out.write(NBSP);
			return true;
		}
		return false;
	}

	public static void writeEscaped(
		final Writer out,
		final String s
	) throws IOException {
		writeEscaped(out, s, false);
	}
	
	public static void writeEscaped(
		final Writer out,
		final String s,
		final boolean pre
	) throws IOException {
		for (int i = 0; i < s.length(); i += 1) {
			writeEscaped(out, s.charAt(i), pre);
		}
	}

	private static boolean startsWith(
		final String s,
		final String c,
		final int index
	) {
		return index + c.length() <= s.length() &&
			s.substring(index, index + c.length() ).equals(c);
	}
	
	private static final String BOLD = "**";
	private static final String STRIKE = "--";
	private static final String UNDERLINE = "__";

	private static final String OPEN_TAG = "[[";
	private static final String CLOSE_TAG = "]]";

	public static void writeStyled(
		final Writer out,
		final IWikiContext context,
		final String s
	) throws Exception {

		final Stack<String> stack = new Stack<String>();
		int index = 0;
		
		while (index < s.length() ) {

			if (s.charAt(index) == '\\' && index + 1 < s.length() ) {
				writeEscaped(out, s.charAt(index + 1) );
				index += 2;
			} else if (stack.size() > 0 &&
					startsWith(s, stack.peek(), index) ) {
				index += stack.pop().length();
				out.write("</span>");
			} else if (startsWith(s, BOLD, index) ) {
				stack.push(BOLD);
				out.write("<span class=\"jwiki-bold\">");
				index += BOLD.length();
			} else if (startsWith(s, STRIKE, index) ) {
				stack.push(STRIKE);
				out.write("<span class=\"jwiki-strike\">");
				index += STRIKE.length();
			} else if (startsWith(s, UNDERLINE, index) ) {
				stack.push(UNDERLINE);
				out.write("<span class=\"jwiki-underline\">");
				index += UNDERLINE.length();
			} else if (startsWith(s, OPEN_TAG, index) ) {
				
				final int start = index + OPEN_TAG.length();
				int end = start;
				String url = null;
				
				while (end < s.length() ) {
					if (startsWith(s, CLOSE_TAG, end) ) {
						url = Util.trim(s.substring(start, end) );
						break;
					}
					end += 1;
				}

				if (!Util.isEmpty(url) ) {
					writeLink(out, context, url);
					index = end + CLOSE_TAG.length();
				} else {
					writeEscaped(out, s.charAt(index) );
					index += 1;
				}

			} else {	
				writeEscaped(out, s.charAt(index) );
				index += 1;
			}
		}

		while (stack.size() > 0) {
			stack.pop();
			out.write("</span>");
		}
	}

	public static void writeLink(
		final Writer out,
		final IWikiContext context,
		String url
	) throws Exception {

		final int index = url.indexOf('\u0020');
		String label = "";
		if (index != -1) {
			label = Util.trim(url.substring(index + 1) );
			url = Util.trim(url.substring(0, index) );
		}

		// scheme:path
		final Pattern pat = Pattern.compile("^([A-Za-z]+\\:)?(.+)$");
		final Matcher mat = pat.matcher(url);
		if (!mat.find() ) {
			writeEscaped(out, url);
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
		writeEscaped(out, url);
	}
}