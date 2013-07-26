package jwiki.fs;

import java.util.Date;

/**
 * IFile
 * @author kazuhiko arase
 */
public interface IFile {
	String getPath();
	boolean exists();
	boolean isFile();
	boolean isDirectory();
	String getId();
	Date getDate();
	String getAuthor();
	String getMessage();
	long getSize();
	Date getEditingDate();
	String getEditingUser();
}