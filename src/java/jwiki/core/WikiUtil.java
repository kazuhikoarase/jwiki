package jwiki.core;

import java.io.IOException;
import java.io.Writer;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
			Writer out, char c) throws IOException {
		writeEscaped(out, c, false);
	}
	
	public static void writeEscaped(
			Writer out, char c, boolean pre) throws IOException {

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
			Writer out, char c) throws IOException {
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
			Writer out, String s) throws IOException {
		writeEscaped(out, s, false);
	}
	
	public static void writeEscaped(
			Writer out, String s, boolean pre) throws IOException {
		for (int i = 0; i < s.length(); i += 1) {
			writeEscaped(out, s.charAt(i), pre);
		}
	}

	private static boolean startsWith(String s, int index, String c) {
		return index + c.length() <= s.length() &&
			s.substring(index, index + c.length() ).equals(c);
	}
	
	private static final String BOLD = "**";
	private static final String STRIKE = "--";
	private static final String UNDERLINE = "__";
	
	public static void writeStyled(
		Writer out,
		IWikiContext context,
		String s
	) throws Exception {

		Stack<String> stack = new Stack<String>();
		int index = 0;
		
		while (index < s.length() ) {

			if (s.charAt(index) == '\\' && index + 1 < s.length() ) {
				writeEscaped(out, s.charAt(index + 1) );
				index += 2;
			} else if (stack.size() > 0 && startsWith(s, index, stack.peek() ) ) {
				index += stack.pop().length();
				out.write("</span>");
			} else if (startsWith(s, index, BOLD) ) {
				stack.push(BOLD);
				out.write("<span class=\"jwiki-bold\">");
				index += BOLD.length();
			} else if (startsWith(s, index, STRIKE) ) {
				stack.push(STRIKE);
				out.write("<span class=\"jwiki-strike\">");
				index += STRIKE.length();
			} else if (startsWith(s, index, UNDERLINE) ) {
				stack.push(UNDERLINE);
				out.write("<span class=\"jwiki-underline\">");
				index += UNDERLINE.length();
			} else if (s.charAt(index) == '[') {
				int start = index + 1;
				int end = start;
				String url = null;
				while (end < s.length() ) {
					if (s.charAt(end) == ']') {
						url = Util.trim(s.substring(start, end) );
						break;
					}
					end += 1;
				}
				if (!Util.isEmpty(url) ) {
					writeLink(out, context, url);
					index = end + 1;
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
		Writer out,
		IWikiContext context,
		String url
	) throws Exception {
		int index = url.indexOf('\u0020');
		String label = "";
		if (index != -1) {
			label = Util.trim(url.substring(index + 1) );
			url = Util.trim(url.substring(0, index) );
		}
		if (Util.isEmpty(label) ) {
			label = url;
		}

		//[(prot:)(path)]
		Pattern pat = Pattern.compile("^([A-Za-z]+\\:)?([^\\:\\?]+)(\\?.+)?$");
		Matcher mat = pat.matcher(url);
		if (!mat.find() ) {
			writeEscaped(out, url);
			return;
		}
		
		String prot = mat.group(1);
		String path = toCanonicalPath(context, mat.group(2) );
		String query = mat.group(3);
		boolean exists = context.getFile(path, -1).exists();
		
		if (Util.isEmpty(prot) ) {

			if (!exists) {
				writeUnknownLink(out, context, path, label);
				return;
			}

			out.write("<a href=\"");
			out.write(context.createPathUrlEncoded(path) );
			if (!Util.isEmpty(query) ) {
				out.write(query);
			}
			out.write("\">");
			writeEscaped(out, label);
			out.write("</a>");

		} else if ("image:".equals(prot) ) {

			if (!exists) {
				writeUnknownLink(out, context, path, label);
				return;
			}

			out.write("<img src=\"");
			out.write(context.createPathUrlEncoded(path) );
			out.write("?raw\" alt=\"");
			writeEscaped(out, label);
			out.write("\" />");

		} else {
			writeEscaped(out, url);
		}
	}
	
	private static void writeUnknownLink(
		Writer out, IWikiContext context,
		String path, String label
	) throws Exception {
		writeEscaped(out, label);
		out.write("<a href=\"");
		out.write(context.createPathUrlEncoded(path) );
		out.write("?v=e\">?</a>");
	}
	
	private static String toCanonicalPath(IWikiContext context, String path)
	throws Exception {
		if (path.startsWith("/") ) {
			// '/' で開始する場合、絶対パス
			return PathUtil.trim(path);
		} else {
			// '/' 以外で開始する場合、相対パスとして解釈
			return PathUtil.buildPath(
				PathUtil.getParent(context.getPath() ), path);
		}
	}
}