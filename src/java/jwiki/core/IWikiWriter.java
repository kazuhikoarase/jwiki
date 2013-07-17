package jwiki.core;

import java.io.IOException;

/**
 * IWikiWriter
 * @author kazuhiko arase
 */
public interface IWikiWriter {
	void write(Object o) throws IOException;
	void write(char c) throws IOException;
}