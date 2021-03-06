package jwiki.core;

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
	Collection<ILinkDecorator> getLinkDecorators();
	
	Map<String,Object> getRequestScope();
	Map<String,Object> getPageScope();
	
	String getString(String key);
	
	IContent get(String path, String id) throws Exception;
	void put(String path, String id, byte[] content, Map<String, String> props, String message) throws Exception;
	void remove(String path, String message) throws Exception;
	IFile getFile(String path, String id) throws Exception;
	List<IFile> listFiles(String path) throws Exception;
	List<IFile> listHistory(String path) throws Exception;
	void lock(String path, String id) throws Exception;
	void unlock(String path) throws Exception;
	String getLockOwner(String path) throws Exception;

	void render(IWikiRendererWorker worker, String plainText) throws Exception;
	void render(IWikiWriter out, String plainText) throws Exception;
}
