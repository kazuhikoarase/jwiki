package jwiki.fs;

import java.util.Map;

/**
 * IContent
 * @author kazuhiko arase
 */
public interface IContent {
	long getRevision();
	byte[] getData();
	Map<String,String> getProperties();
}