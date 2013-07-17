package jwiki.core.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jwiki.core.ILink;
import jwiki.core.ILinkDecorator;
import jwiki.core.IWikiContext;
import jwiki.core.IWikiWriter;
import jwiki.core.Util;

/**
 * WikiWriter
 * @author kazuhiko arase
 */
public class WikiWriter implements IWikiWriter {

	private List<Object> buffer = new ArrayList<Object>();

	public WikiWriter() {
	}

	public void write(char c) throws IOException {
		buffer.add(Character.valueOf(c) );
	}

	public void write(Object o) throws IOException {
		buffer.add(o);
	}
	
	private static final char HT = 0x09;
	private static final char LF = 0x0a;
	private static final char SP = 0x20;
	
	private static final String NBSP = "&#160;";
	
	public void writeEscaped(
		final char c
	) throws IOException {
		writeEscaped(c, false);
	}
	
	public void writeEscaped(
		final char c,
		final boolean pre
	) throws IOException {

		if (pre && preformat(c) ) {
			// preformat
			return;
		}

		if (c == '<') {
			write("&lt;");
		} else if (c == '>') {
			write("&gt;");
		} else if (c == '&') {
			write("&amp;");
		} else if (c == '"') {
			write("&quot;");
		} else {
			write(c);
		}
	}
	
	private boolean preformat(
		final char c
	) throws IOException {
		if (c == HT) {
			write(NBSP);
			write(NBSP);
			write(NBSP);
			write(NBSP);
			return true;
		} else if (c == LF) {
			write("<br/>");
			return true;
		} else if (c == SP) {
			write(NBSP);
			return true;
		}
		return false;
	}

	public void writeEscaped(
		final String s
	) throws IOException {
		writeEscaped(s, false);
	}
	
	public void writeEscaped(
		final String s,
		final boolean pre
	) throws IOException {
		for (int i = 0; i < s.length(); i += 1) {
			writeEscaped(s.charAt(i), pre);
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

	public void writeStyled(
		final IWikiContext context,
		final String s
	) throws Exception {

		final Stack<String> stack = new Stack<String>();
		int index = 0;
		
		while (index < s.length() ) {

			if (s.charAt(index) == '\\' && index + 1 < s.length() ) {
				writeEscaped(s.charAt(index + 1) );
				index += 2;
			} else if (stack.size() > 0 &&
					startsWith(s, stack.peek(), index) ) {
				index += stack.pop().length();
				write("</span>");
			} else if (startsWith(s, BOLD, index) ) {
				stack.push(BOLD);
				write("<span class=\"jwiki-bold\">");
				index += BOLD.length();
			} else if (startsWith(s, STRIKE, index) ) {
				stack.push(STRIKE);
				write("<span class=\"jwiki-strike\">");
				index += STRIKE.length();
			} else if (startsWith(s, UNDERLINE, index) ) {
				stack.push(UNDERLINE);
				write("<span class=\"jwiki-underline\">");
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
					writeLink(context, url);
					index = end + CLOSE_TAG.length();
				} else {
					writeEscaped(s.charAt(index) );
					index += 1;
				}

			} else {	
				writeEscaped(s.charAt(index) );
				index += 1;
			}
		}

		while (stack.size() > 0) {
			stack.pop();
			write("</span>");
		}
	}

	public void writeLink(
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
			writeEscaped(url);
			return;
		}
		
		final String scheme = Util.coalesce(mat.group(1), "");
		final String path = mat.group(2);

		final ILink link = new Link(path, label);
		for (final ILinkDecorator decorator :
				context.getLinkDecorators() ) {
			if (decorator.getScheme().equals(scheme) ) {
				decorator.render(context, link, this);
				return;
			}
		}

		// not found.
		writeEscaped(url);
	}
	
	public void writeTo(Writer out) throws IOException {
		for (Object o : buffer) {
			out.write(o.toString() );
		}
	}
}