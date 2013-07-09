package jwiki.fs.svn;

import java.util.Date;

import jwiki.fs.IFile;
import jwiki.core.Util;

import org.tmatesoft.svn.core.SVNNodeKind;

/**
 * SVNFile
 * @author kazuhiko arase
 */
public class SVNFile implements IFile {
	private final String path;
	private final SVNNodeKind kind;
	private final Date date;
	private final long revision;
	private final long size;
	private final String author;
	private final String message;
	private final Date editingDate;
	private final String editingUser;
	public SVNFile(
		String path,
		SVNNodeKind kind,
		long revision,
		Date date,
		String author,
		String message,
		long size,
		Date editingDate,
		String editingUser
	) {
		this.path = path;
		this.kind = kind;
		this.revision = revision;
		this.date = date;
		this.author = Util.coalesce(author, "");
		this.message = Util.coalesce(message, "");
		this.size = size;
		this.editingDate = editingDate;
		this.editingUser = Util.coalesce(editingUser, "");
	}
	public String getPath() {
		return path;
	}
	public boolean isFile() {
		return kind == SVNNodeKind.FILE;
	}
	public boolean isDirectory() {
		return kind == SVNNodeKind.DIR;
	}
	public boolean exists() {
		return isFile() || isDirectory();
	}
	public long getSize() {
		return size;
	}
	public long getRevision() {
		return revision;
	}
	public Date getDate() {
		return date;
	}
	public String getAuthor() {
		return author;
	}
	public String getMessage() {
		return message;
	}
	public Date getEditingDate() {
		return editingDate;
	}
	public String getEditingUser() {
		return editingUser;
	}
	
}
