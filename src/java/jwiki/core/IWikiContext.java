package jwiki.core;

import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jwiki.fs.IContent;
import jwiki.fs.IFile;

/**
 * IWikiContext
 * @author kazuhiko arase
 */
public interface IWikiContext {
	
	String getUsername();

	String getPath();
	void setPath(String path);
	String createPathUrlEncoded(String path);
	String generateUniqueId();
	
	Collection<IParagraphDecorator> getDecorators();
	
	Map<String,Object> getRequestScope();
	Map<String,Object> getPageScope();
	
	String getString(String key);
	
	IContent get(String path, long revision) throws Exception;
	void put(String path, long revision, byte[] content, Map<String, String> props, String message) throws Exception;
	void remove(String path, String message) throws Exception;
	IFile getFile(String path, long revision) throws Exception;
	List<IFile> listFiles(String path) throws Exception;
	List<IFile> listHistory(String path) throws Exception;
	void lock(String path, long revision) throws Exception;
	void unlock(String path) throws Exception;
	String getLockOwner(String path) throws Exception;

	void render(IWikiRendererWorker worker, String plainText) throws Exception;
	void render(Writer out, String plainText) throws Exception;
}
