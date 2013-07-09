package jwiki.core;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * IAction
 * @author kazuhiko arase
 */
public interface IAction {
	
	void init(
		ServletContext servletContext,
		HttpServletRequest request,
		HttpServletResponse response,
		IWikiContext context
	) throws Exception;

	void execute() throws Exception;
}
