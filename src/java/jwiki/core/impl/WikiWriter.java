package jwiki.core.impl;

import java.io.IOException;
import java.io.Writer;

import jwiki.core.IWikiWriter;

/**
 * WikiWriter
 * @author kazuhiko arase
 */
public class WikiWriter implements IWikiWriter {
	
	private static final char HT = 0x09;
	private static final char LF = 0x0a;
	private static final char SP = 0x20;
	
	private static final String NBSP = "&#160;";

	private final Writer out;
	private final int tabSize;
	
	public WikiWriter(final Writer out) {
		this(out, 4);
	}
	
	public WikiWriter(final Writer out, final int tabSize) {
		this.out = out;
		this.tabSize = tabSize;
	}
	
	public void write(final char c) throws IOException {
		out.write(c);
	}

	public void write(final String s) throws IOException {
		out.write(s);
	}
	
	public void writeEscaped(final char c) throws IOException {
		writeEscaped(c, false);
	}
	
	public void writeEscaped(final char c,
			final boolean pre) throws IOException {

		if (pre && preformat(c) ) {
			// preformatted.
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

	public void writeEscaped(final String s) throws IOException {
		writeEscaped(s, false);
	}
	
	public void writeEscaped(final String s,
			final boolean pre) throws IOException {
		for (int i = 0; i < s.length(); i += 1) {
			writeEscaped(s.charAt(i), pre);
		}
	}
	
	private boolean preformat(final char c) throws IOException {
		if (c == HT) {
			for (int i = 0; i < tabSize; i += 1) {
				write(NBSP);
			}
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
}
