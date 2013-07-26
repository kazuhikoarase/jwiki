package jwiki.fs;

import java.util.List;
import java.util.Map;

/**
 * IFileSystem
 * @author kazuhiko arase
 */
public interface IFileSystem {
	void lock(IUserInfo userInfo, String path, String id) throws Exception;
	void unlock(IUserInfo userInfo, String path) throws Exception;
	String getLockOwner(IUserInfo userInfo, String path) throws Exception;
	IContent get(IUserInfo userInfo, String path, String id) throws Exception;
	void put(IUserInfo userInfo, String path, String id, byte[] data, Map<String,String> props, String message) throws Exception;
	void remove(IUserInfo userInfo, String path, String message) throws Exception;
	IFile getFile(IUserInfo userInfo, String path, String id) throws Exception;
	List<IFile> listFiles(IUserInfo userInfo, String path) throws Exception;
	List<IFile> listHistory(IUserInfo userInfo, String path) throws Exception;
}
