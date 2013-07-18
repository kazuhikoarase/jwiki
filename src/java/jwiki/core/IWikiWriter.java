package jwiki.core;

import java.io.IOException;

/**
 * IWikiWriter
 * @author kazuhiko arase
 */
public interface IWikiWriter {
	void write(char c) throws IOException;
	void write(String s) throws IOException;
	void writeEscaped(char c) throws IOException;
	void writeEscaped(String s) throws IOException;
	void writeEscaped(char c, boolean pre) throws IOException;
	void writeEscaped(String s, boolean pre) throws IOException;
}