package jwiki.core.impl;

import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import jwiki.core.IWikiContext;
import jwiki.core.IWikiRendererWorker;
import jwiki.core.IParagraphDecorator;
import jwiki.fs.IContent;
import jwiki.fs.IFile;
import jwiki.fs.IFileSystem;
import jwiki.fs.IUserInfo;

/**
 * WikiContext
 * @author kazuhiko arase
 */
public class WikiContext implements IWikiContext {

	private int idCount = 0;

	private Collection<IParagraphDecorator> decorators = null;
	private String pathPrefix = "";
	private String path = "";
	private ResourceBundle resource;
	private IFileSystem fs = null;
	private IUserInfo userInfo = null;
	
	private Map<String,Object> requestScope = new HashMap<String, Object>();
	private Map<String,Object> pageScope = new HashMap<String, Object>();

	private Map<String, IFile> cache = new HashMap<String, IFile>();

	
	public WikiContext() {
	}

	public Collection<IParagraphDecorator> getDecorators() {
		return decorators;
	}

	public void setDecorators(Collection<IParagraphDecorator> decorators) {
		this.decorators = decorators;
	}

	public Map<String, Object> getPageScope() {
		return pageScope;
	}

	public Map<String, Object> getRequestScope() {
		return requestScope;
	}

	public String generateUniqueId() {
		return String.valueOf(idCount++);
	}

	public ResourceBundle getResource() {
		return resource;
	}

	public void setResource(ResourceBundle resource) {
		this.resource = resource;
	}
	
	public String getString(String key) {
		return resource.getString(key);
	}

	public String getPathPrefix() {
		return pathPrefix;
	}

	public void setPathPrefix(String pathPrefix) {
		this.pathPrefix = pathPrefix;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String createPathUrlEncoded(String path) {
		try {
			StringBuilder buf = new StringBuilder();
			buf.append(pathPrefix);
			for (String name : path.split("/") ) {
				buf.append('/');
				buf.append(URLEncoder.encode(name, "UTF-8").
						replaceAll("\\+", "%20") );
			}
			return buf.toString();
		} catch(UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public IFileSystem getFs() {
		return fs;
	}

	public void setFs(IFileSystem fs) {
		this.fs = fs;
	}

	public IUserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(IUserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public String getUsername() {
		return getUserInfo().getUsername();
	}

	public void put(String path, long revision, byte[] data,
			Map<String,String> props, String message) throws Exception {
		fs.put(userInfo, path, revision, data, props, message);
	}

	public void remove(String path, String message) throws Exception {
		fs.remove(userInfo, path, message);
	}

	public IContent get(String path, long revision) throws Exception {
		return fs.get(userInfo, path, revision);
	}
	
	public IFile getFile(String path, long revision) throws Exception {
		final String key = path + ":r" + revision;
		if (cache.containsKey(key) ) {
			return cache.get(key);
		}
		IFile file = fs.getFile(userInfo, path, revision);
		cache.put(key, file);
		return file;
	}

	public List<IFile> listFiles(String path) throws Exception {
		return fs.listFiles(userInfo, path);
	}

	public List<IFile> listHistory(String path) throws Exception {
		return fs.listHistory(userInfo, path);
	}
	
	public String getLockOwner(String path) throws Exception {
		return fs.getLockOwner(userInfo, path);
	}
	
	public void lock(String path, long revision) throws Exception {
		fs.lock(userInfo, path, revision);
	}

	public void unlock(String path) throws Exception {
		fs.unlock(userInfo, path);
	}

	public void render(IWikiRendererWorker worker, 
			String plainText) throws Exception {
		WikiRenderer renderer = new WikiRenderer();
		renderer.render(this, worker, plainText);
	}
	
	public void render(Writer out, String plainText) throws Exception {
		render(new DefaultWikiRendererWorker(out), plainText);
	}
}