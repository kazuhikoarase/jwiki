package jwiki.servlet;

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

import jwiki.core.ILinkDecorator;
import jwiki.core.IParagraphDecorator;
import jwiki.core.IWikiContext;
import jwiki.core.PathUtil;
import jwiki.core.Util;
import jwiki.core.impl.WikiContext;
import jwiki.decorator.AttachedFileDecorator;
import jwiki.decorator.BlankDecorator;
import jwiki.decorator.CodeBlockDecorator;
import jwiki.decorator.DefaultDecorator;
import jwiki.decorator.DiffDecorator;
import jwiki.decorator.DocumentDecorator;
import jwiki.decorator.HeaderDecorator;
import jwiki.decorator.HistoryDecorator;
import jwiki.decorator.HrDecorator;
import jwiki.decorator.IndexDecorator;
import jwiki.decorator.ListDecorator;
import jwiki.decorator.NavigatorDecorator;
import jwiki.decorator.TableDecorator;
import jwiki.decorator.link.DefaultLinkDecorator;
import jwiki.decorator.link.ImageLinkDecorator;
import jwiki.fs.IFile;
import jwiki.fs.IFileSystem;
import jwiki.fs.IUserInfo;
import jwiki.fs.svn.SVNFileSystem;
import jwiki.servlet.action.DirectoryViewAction;
import jwiki.servlet.action.FileEditAction;
import jwiki.servlet.action.FileViewAction;
import jwiki.servlet.i18n.WikiResource;

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

	/**
	 * ローカルリポジトリのディレクトリをカスタマイズするためには、このメソッドをオーバーライドします。
	 * @param request
	 * @return
	 * @throws Exception
	 */
	protected File getLocalRepositoryDir(HttpServletRequest request) throws Exception {
		return new File(getServletContext().
				getRealPath("/WEB-INF/jwiki/svn/repo") );
	}

	/**
	 * ユーザー認証をカスタマイズするためには、このメソッドをオーバーライドします。
	 * @param request
	 * @return
	 * @throws Exception
	 */
	protected IUserInfo getUserInfo(HttpServletRequest request) throws Exception {
		return (IUserInfo)request.getAttribute(Constants.JWIKI_USER);
	}

	/**
	 * 文法を拡張するためには、このメソッドをオーバーライドします。
	 * @return
	 */
	protected Collection<ILinkDecorator> getLinkDecorators() {
		return DEFAULT_LINK_DECORATORS;
	}

	/**
	 * 文法を拡張するためには、このメソッドをオーバーライドします。
	 * @return
	 */
	protected Collection<IParagraphDecorator> getDecorators() {
		return DEFAULT_DECORATORS;
	}
	
	protected IFileSystem createFileSystem(HttpServletRequest request)
	throws Exception {
		if (!Util.isEmpty(svnUrl) ) {
			return new SVNFileSystem(SVNURL.parseURIEncoded(svnUrl) );
		}
		return new SVNFileSystem(getLocalRepository(request) );
	}
	
	private SVNURL getLocalRepository(HttpServletRequest request) throws Exception {
		File dir = getLocalRepositoryDir(request);
	    if (!dir.exists() ) {
	    	dir.mkdirs();
	    	SVNURL url = SVNRepositoryFactory.
	    			createLocalRepository(dir, true, false);
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

	protected String getPathPrefix(HttpServletRequest request) throws Exception {
		return request.getContextPath() + request.getServletPath();
	}
	
	protected String getPath(HttpServletRequest request) throws Exception {
		String path = Util.coalesce(request.getPathInfo(), "");
		path = new String(path.getBytes("ISO-8859-1"), "UTF-8");
		return PathUtil.trim(path);
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
		context.setLinkDecorators(getLinkDecorators() );
		context.setDecorators(getDecorators() );
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
	
	private static Collection<ILinkDecorator> createLinkDecorators() {
		List<ILinkDecorator> decorators = new ArrayList<ILinkDecorator>();
		decorators.add(new DefaultLinkDecorator() );
		decorators.add(new ImageLinkDecorator() );
		return decorators;
	}
	
	private static Collection<IParagraphDecorator> createDecorators() {
		List<IParagraphDecorator> decorators = new ArrayList<IParagraphDecorator>();
		decorators.add(new HeaderDecorator() );
		decorators.add(new IndexDecorator() );
		decorators.add(new NavigatorDecorator() );
		decorators.add(new HistoryDecorator() );
		decorators.add(new DiffDecorator() );
		decorators.add(new TableDecorator() );
		decorators.add(new DocumentDecorator() );
		decorators.add(new ListDecorator() );
		decorators.add(new HrDecorator() );
		decorators.add(new CodeBlockDecorator() );
		decorators.add(new AttachedFileDecorator() );
		// 以下の２つは固定
		decorators.add(new BlankDecorator() );
		decorators.add(new DefaultDecorator() );
		return decorators;
	}

	private static final Collection<ILinkDecorator> DEFAULT_LINK_DECORATORS =
			createLinkDecorators();
	private static final Collection<IParagraphDecorator> DEFAULT_DECORATORS =
			createDecorators();
}
