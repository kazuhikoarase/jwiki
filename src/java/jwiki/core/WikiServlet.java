package jwiki.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jwiki.core.action.DirectoryViewAction;
import jwiki.core.action.FileEditAction;
import jwiki.core.action.FileViewAction;
import jwiki.core.i18n.WikiResource;
import jwiki.core.impl.WikiContext;
import jwiki.core.wikilet.AttachedFileWikilet;
import jwiki.core.wikilet.BlankWikilet;
import jwiki.core.wikilet.CodeWikilet;
import jwiki.core.wikilet.DefaultWikilet;
import jwiki.core.wikilet.DiffWikilet;
import jwiki.core.wikilet.DocumentWikilet;
import jwiki.core.wikilet.HeaderWikilet;
import jwiki.core.wikilet.HistoryWikilet;
import jwiki.core.wikilet.HrWikilet;
import jwiki.core.wikilet.IndexWikilet;
import jwiki.core.wikilet.ListWikilet;
import jwiki.core.wikilet.NavigatorWikilet;
import jwiki.core.wikilet.TableWikilet;
import jwiki.fs.IFile;
import jwiki.fs.IFileSystem;
import jwiki.fs.IUserInfo;
import jwiki.fs.svn.SVNFileSystem;

import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

/**
 * WikiServlet
 * @author kazuhiko arase
 */
@SuppressWarnings("serial")
public class WikiServlet extends HttpServlet {

	private String svnUrl;
	
	private String templatePage;
	
	public WikiServlet() {
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		svnUrl = config.getInitParameter("svn-url");
		templatePage = config.getInitParameter("template-page");
	}

	protected IFileSystem createFileSystem(HttpServletRequest request)
	throws Exception {
		if (!Util.isEmpty(svnUrl) ) {
			return new SVNFileSystem(SVNURL.parseURIEncoded(svnUrl) );
		}
		return new SVNFileSystem(getLocalRepository(request) );
	}
	
	protected SVNURL getLocalRepository(HttpServletRequest request) throws Exception {
		File dir = getLocalRepositoryDir(request);
	    if (!dir.exists() ) {
	    	dir.mkdirs();
	    	SVNURL url = SVNRepositoryFactory.createLocalRepository(
		    		dir, true, false);
	    	putDefaultPages(request, url);
		}
	    return SVNURL.fromFile(dir);
	}
	
	protected void putDefaultPages(
		HttpServletRequest request, SVNURL url
	) throws Exception {
    	String[] defaultPages = {"index", "syntax", "cat.jpg"};
    	SVNFileSystem fs = new SVNFileSystem(url);
    	for (String path : defaultPages) {
    		byte[] data = Util.getResource("/jwiki/assets/" + path);
	    	fs.put(getUserInfo(request), path, -1, data, null, "");
    	}
	}
	
	protected File getLocalRepositoryDir(HttpServletRequest request) throws Exception {
		return new File(getServletContext().
				getRealPath("/WEB-INF/jwiki/svn/repo") );
	}
	
	protected String getPathPrefix(HttpServletRequest request) throws Exception {
		return request.getContextPath() + request.getServletPath();
	}
	
	protected String getPath(HttpServletRequest request) throws Exception {
		String path = Util.coalesce(request.getPathInfo(), "");
		path = new String(path.getBytes("ISO-8859-1"), "UTF-8");
		return PathUtil.trim(path);
	}

	protected IUserInfo getUserInfo(HttpServletRequest request) throws Exception {
		return (IUserInfo)request.getAttribute(Constants.JWIKI_USER);
	}
	
	protected Collection<IWikilet> getWikilets() {
		return DEFAULT_WIKILETS;
	}
	
	protected IAction createAction(
		HttpServletRequest request,
		HttpServletResponse response,
		IWikiContext context
	) throws Exception {

		IFile file = context.getFile(context.getPath(), -1);
		String view = request.getParameter("v");
	
		if ("e".equals(view) ) {
			return new FileEditAction();
		} else {
			if (file.isFile() ) {
				return new FileViewAction();
			} else if (file.isDirectory() ) {
				return new DirectoryViewAction();
			} else {
				// not exists.
				return new FileViewAction();
			}
		}
	}
	
	@Override
	protected void service(
		HttpServletRequest request,
		HttpServletResponse response
	) throws ServletException, IOException {
		try {
			serviceImpl(request, response);
		} catch(Exception e) {
			throw new ServletException(e);
		}
	}

	private void serviceImpl(
		HttpServletRequest request,
		HttpServletResponse response
	) throws Exception {

		if (!Util.isEmpty(templatePage) ) {
			request.setAttribute(
				Constants.JWIKI_TEMPLATE_PAGE,
				templatePage);
		}

		if (Util.isEmpty(request.getCharacterEncoding() ) ) {
			request.setCharacterEncoding("UTF-8");
		}

		WikiContext context = new WikiContext();
		context.setWikilets(getWikilets() );
		context.setFs(createFileSystem(request) );
		context.setPathPrefix(getPathPrefix(request) );
		context.setPath(getPath(request) );
		context.setUserInfo(getUserInfo(request) );
		context.setResource(ResourceBundle.getBundle(
			WikiResource.class.getName(),
			request.getLocale() ) );
		
		IAction action = createAction(request, response, context);
		action.init(getServletContext(),
				request, response, context);
		action.execute();
	}
	
	private static Collection<IWikilet> createWikilets() {
		List<IWikilet> wikilets = new ArrayList<IWikilet>();
		wikilets.add(new HeaderWikilet() );
		wikilets.add(new IndexWikilet() );
		wikilets.add(new NavigatorWikilet() );
		wikilets.add(new HistoryWikilet() );
		wikilets.add(new DiffWikilet() );
		wikilets.add(new TableWikilet() );
		wikilets.add(new DocumentWikilet() );
		wikilets.add(new ListWikilet() );
		wikilets.add(new HrWikilet() );
		wikilets.add(new CodeWikilet() );
		wikilets.add(new AttachedFileWikilet() );
		// 以下の２つは固定
		wikilets.add(new BlankWikilet() );
		wikilets.add(new DefaultWikilet() );
		return wikilets;
	}

	private static final Collection<IWikilet> DEFAULT_WIKILETS =
		createWikilets();
}
