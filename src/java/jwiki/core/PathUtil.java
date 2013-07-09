package jwiki.core;

import java.util.List;

/**
 * PathUtil
 * @author kazuhiko arase
 */
public class PathUtil {
	
	private PathUtil() {
	}

	public static String getParent(String path) {
		int index = path.lastIndexOf('/');
		if (index != -1) {
			return path.substring(0, index);
		}
		return "";
	}

	public static String getName(String path) {
		int index = path.lastIndexOf('/');
		if (index != -1) {
			return path.substring(index + 1);
		}
		return path;
	}
	
	public static String buildPath(String parent, String path) {
		return trim(parent + "/" + path);
	}

	public static String trim(String path) {
		// 前後のスラッシュを除去、連続するスラッシュをひとつに。
		return path.
			replaceAll("/+", "/").
			replaceAll("^/+|/+$", "");
	}
	
	public static boolean isValidPath(String path) {
		List<String> pathList = Util.strictSplit(path, "/");
		if (pathList.size() == 0) {
			return false;
		}
		for (String name : pathList) {
			if (Util.isEmpty(name) ) {
				return false;
			} else if (name.startsWith(".") || name.endsWith(".") ) {
				return false;
			} else if (!name.matches("^[A-Za-z0-9_@\\.\\-\\u0080-\\uffff]+$") ) {
				return false;
			}
		}
		return true;
	}
}