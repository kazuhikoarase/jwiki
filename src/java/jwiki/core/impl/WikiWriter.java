package jwiki.core.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import jwiki.core.IWikiWriter;

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

	public void writeTo(Writer out) throws IOException {
		for (Object o : buffer) {
			out.write(o.toString() );
		}
	}
}