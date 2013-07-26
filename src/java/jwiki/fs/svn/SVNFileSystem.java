package jwiki.fs.svn;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jwiki.core.Util;
import jwiki.fs.IContent;
import jwiki.fs.IFile;
import jwiki.fs.IFileSystem;
import jwiki.fs.IUserInfo;
import jwiki.fs.impl.Content;
import jwiki.fs.impl.File;

import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLock;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNPropertyValue;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.util.SVNPathUtil;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * SVNFileSystem
 * @author kazuhiko arase
 */
public class SVNFileSystem implements IFileSystem {

	private final SVNURL url;
	
	public SVNFileSystem(SVNURL url) throws Exception {
		this.url = url;
	}
	
	public IFile getFile(IUserInfo userInfo, String path, String id) throws Exception {
		return getFile(userInfo, path, toRevision(id) );
	}
	
	public IFile getFile(IUserInfo userInfo, String path, long revision) throws Exception {
		return getFile(createRepository(userInfo), path, revision);
	}
	
	public IContent get(IUserInfo userInfo, String path, String id) throws Exception {
		return get(userInfo, path, toRevision(id) );
	}
	
	public IContent get(IUserInfo userInfo, String path, long revision) throws Exception {
		return get(createRepository(userInfo), path, revision);
	}
	
	public void put(IUserInfo userInfo, String path, String id, byte[] data,
			Map<String, String> props, String message) throws Exception {
		put(userInfo, path, toRevision(id), data, props, message);
	}

	public void put(IUserInfo userInfo, String path, long revision, byte[] data, Map<String,String> props, String message) throws Exception {

		SVNRepository repo = createRepository(userInfo);

		IContent oldContent = null;
		
		List<IFile> pathList = createPathList(repo, path, -1);
		IFile file = pathList.get(pathList.size() - 1);

		if (file.getPath().matches("^\\.+$") ) {
			throw new Exception("bad path:" + file.getPath() );
		}
		
		if (file.exists() ) {

			oldContent = get(repo, path, revision);

			if (Arrays.equals(oldContent.getData(), data) ) {
				// 相違が無い
				return;
			}
		}

		Map<String,String> locks = new HashMap<String, String>();
		if (file.isFile() ) {
			SVNLock lock = checkLockOwner(userInfo, repo, file.getPath() );
			locks.put(path, lock.getID() );
		}
		ISVNEditor editor = repo.getCommitEditor(
			message, locks, false, new CommitMediator() );

		try {
			
			editor.openRoot(-1);

			for (int i = 0; i < pathList.size() - 1; i += 1) {
				IFile dir = pathList.get(i);
				if (dir.exists() ) {
					editor.openDir(dir.getPath(), -1);
				} else {
					editor.addDir(dir.getPath(), null, -1);
				}
			}

			if (file.exists() ) {
				editor.openFile(path, -1);
			} else {
				editor.addFile(path, null, -1);
			}

			if (props != null) {
				for (Entry<String,String> entry : props.entrySet() ) {
					editor.changeFileProperty(path,
						entry.getKey(),
						SVNPropertyValue.create(entry.getValue() ) );
				}
			}

			editor.applyTextDelta(path, null);

			SVNDeltaGenerator deltaGen = new SVNDeltaGenerator();

			String checksum;

			if (file.exists() ) {
				checksum = deltaGen.sendDelta(path,
						new ByteArrayInputStream(oldContent.getData() ), 0,
						new ByteArrayInputStream(data), editor, true);
			} else {
				checksum = deltaGen.sendDelta(path,
						new ByteArrayInputStream(data), editor, true);
			}

			editor.closeFile(path, checksum);

			editor.closeEdit();

		} catch(SVNException e) {
			editor.abortEdit();
		}	
	}

	public void remove(IUserInfo userInfo, String path, String message) throws Exception {
		
		SVNRepository repo = createRepository(userInfo);
		
		List<IFile> pathList = createPathList(repo, path, -1);
		IFile file = pathList.get(pathList.size() - 1);

		if (!file.exists() ) {
			return;
		}

		Map<String,String> locks = new HashMap<String, String>();
		if (file.isFile() ) {
			SVNLock lock = checkLockOwner(userInfo, repo, path);
			locks.put(path, lock.getID() );
		}
		ISVNEditor editor = repo.getCommitEditor(
			message, locks, false, new CommitMediator() );

		try {
			
			editor.openRoot(-1);

			for (int i = 0; i < pathList.size() - 1; i += 1) {
				IFile dir = pathList.get(i);
				editor.openDir(dir.getPath(), -1);
			}

			editor.deleteEntry(path, -1);

			editor.closeEdit();

		} catch(SVNException e) {
			editor.abortEdit();
		}	
	}

	public List<IFile> listFiles(IUserInfo userInfo, final String path) throws Exception {

		final SVNRepository repo = createRepository(userInfo);

		final List<SVNDirEntry> entries = new ArrayList<SVNDirEntry>();
		repo.getDir(path, -1, null, new ISVNDirEntryHandler() {
			public void handleDirEntry(SVNDirEntry entry) throws SVNException {
				entries.add(entry);
			}
		} );

		final List<IFile> list = new ArrayList<IFile>();
		for (SVNDirEntry entry : entries) {
			String subPath = SVNPathUtil.
				append(path, entry.getRelativePath() );
			entry.setLock(repo.getLock(subPath) );
			list.add(createFile(subPath, entry) );
		}
		
		Collections.sort(list, new Comparator<IFile>() {
			public int compare(IFile f1, IFile f2) {
				return f1.getPath().compareTo(f2.getPath() );
			}
		});
		return list;
	}

	public List<IFile> listHistory(IUserInfo userInfo, final String path) throws Exception {

		SVNRepository repo = createRepository(userInfo);

		//final long end = repo.getLatestRevision();
		//final long start = Math.max(0, end - 100);
		final SVNNodeKind kind = repo.checkPath(path, -1);
	
		final List<IFile> list = new ArrayList<IFile>();
		repo.log(new String[]{path}, 0, -1, false, true, new ISVNLogEntryHandler() {
			public void handleLogEntry(SVNLogEntry entry) throws SVNException {
				list.add(new File(
					path,
					kind,
					toId(entry.getRevision() ),
					entry.getDate(),
					entry.getAuthor(),
					entry.getMessage(),
					-1,
					null,
					null) );
			}
		} );
		Collections.sort(list, new Comparator<IFile>() {
			@Override
			public int compare(IFile f1, IFile f2) {
				Long rev1 = toRevision(f1.getId() );
				Long rev2 = toRevision(f2.getId() );
				return rev2.compareTo(rev1);
			}
		});
		return list;
	}

	public String getLockOwner(IUserInfo userInfo, String path) throws Exception {
		SVNRepository repo = createRepository(userInfo);
		SVNLock lock = repo.getLock(path);
		return lock != null? lock.getOwner() : null;
	}

	public void lock(IUserInfo userInfo, String path, String id) throws Exception {
		lock(userInfo, path, toRevision(id) );
	}

	public void lock(IUserInfo userInfo, String path, long revision) throws Exception {
		SVNRepository repo = createRepository(userInfo);
		Map<String,Long> paths = new HashMap<String, Long>();
		paths.put(path, Long.valueOf(revision) );
		repo.lock(paths, "<lock>", true, null);
	}
	
	public void unlock(IUserInfo userInfo, String path) throws Exception {
		SVNRepository repo = createRepository(userInfo);
		SVNLock lock = repo.getLock(path);
		if (lock != null) {
			Map<String,String> paths = new HashMap<String, String>();
			paths.put(path, lock.getID() );
			repo.unlock(paths, true, null);
		}
	}

	protected SVNRepository createRepository(IUserInfo userInfo) throws Exception {
		SVNRepository repo = SVNRepositoryFactory.create(url);
		repo.setAuthenticationManager(SVNWCUtil.
				createDefaultAuthenticationManager(
						userInfo.getUsername(),
						userInfo.getPassword() ) );
		return repo;
	}

	protected List<IFile> createPathList(SVNRepository repo, String path, long revision) throws Exception {
		List<IFile> pathList = new ArrayList<IFile>();
		pathList.add(getFile(repo, path, revision) );
		int index;
		while ( (index = path.lastIndexOf('/') ) != -1) {
			path = path.substring(0, index);
			pathList.add(getFile(repo, path, revision) );
		}
		Collections.reverse(pathList);
		return pathList;
	}
	
	protected IFile createFile(String path, SVNDirEntry entry) throws SVNException {
		SVNLock lock = entry.getLock();
		return new File(
			path,
			entry.getKind(),
			toId(entry.getRevision() ),
			entry.getDate(),
			entry.getAuthor(),
			entry.getCommitMessage(),
			entry.getSize(),
			lock != null? lock.getCreationDate() : null,
			lock != null? lock.getOwner() : null);
	}

	protected IFile getFile(SVNRepository repo, final String path, long revision) throws Exception {

		final SVNNodeKind kind = repo.checkPath(path, revision);
		
		if (kind == SVNNodeKind.DIR || kind == SVNNodeKind.FILE) {
			SVNDirEntry entry = repo.info(path, revision);
			entry.setLock(repo.getLock(path) );
			return createFile(path, entry);
		}
		
		return new File(path, kind, toId(revision),
				null, null, null, -1, null, null);
	}
	
	protected IContent get(SVNRepository repo, String path, long revision) throws Exception {

		IFile file = getFile(repo, path, revision);

		if (!file.exists() ) {
			return Content.EMPTY;
		}
		
		SVNProperties svnProps = new SVNProperties();
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try {
			revision = repo.getFile(path, revision, svnProps, bout);
		} finally {
			bout.close();
		}

		Map<String,String> props = new HashMap<String, String>();
		for (String key : svnProps.nameSet() ) {
			props.put(key, svnProps.getStringValue(key) );
		}
		return new Content(toId(revision), bout.toByteArray(), props);
	}
	
	protected SVNLock checkLockOwner(IUserInfo userInfo, SVNRepository repo, String path) throws Exception {
		SVNLock lock = repo.getLock(path);
		if (lock == null || !lock.getOwner().equals(
				userInfo.getUsername() ) ) {
			throw new Exception("not lock owner");
		}
		return lock;
	}

	protected static String toId(long revision) {
		return revision == -1? "" : "r" + revision;
	}
	
	protected static long toRevision(String id) {
		return Util.isEmpty(id)? -1 : Long.valueOf(id.substring(1) );
	}
}

