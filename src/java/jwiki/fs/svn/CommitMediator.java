package jwiki.fs.svn;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNPropertyValue;
import org.tmatesoft.svn.core.io.ISVNWorkspaceMediator;

/**
 * CommitMediator
 * @author kazuhiko arase
 */
public class CommitMediator implements ISVNWorkspaceMediator {
	public SVNPropertyValue getWorkspaceProperty(
			String path, String name) throws SVNException {
    	return null;
    }
	public void setWorkspaceProperty(
			String path, String name,
			SVNPropertyValue value) throws SVNException {
	}
}

