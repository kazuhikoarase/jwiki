package jwiki.core.action;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jwiki.core.IAction;
import jwiki.core.IWikiContext;

/**
 * Action
 * @author kazuhiko arase
 */
public abstract class Action implements IAction {

	protected ServletContext servletContext;
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected IWikiContext context;
	
	protected Action() {
	}

	public void init(
		ServletContext servletContext,
		HttpServletRequest request,
		HttpServletResponse response,
		IWikiContext context
	) throws Exception {
		this.servletContext = servletContext;
		this.request = request;
		this.response = response;
		this.context = context;
	}
}
