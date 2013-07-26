package jwiki.fs;

import java.util.Map;

/**
 * IContent
 * @author kazuhiko arase
 */
public interface IContent {
	String getId();
	byte[] getData();
	Map<String,String> getProperties();
}