package jwiki.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * BasicAuthFilter
 * @author kazuhiko arase
 */
public abstract class BasicAuthFilter implements Filter {

	protected FilterConfig config;
	
	protected BasicAuthFilter() {
	}
	
	public void init(FilterConfig config) throws ServletException {
		this.config = config;
	}

	public void destroy() {
	}
	
	protected abstract boolean isValidUser(
		HttpServletRequest request, String username, String password);

	public void doFilter(
		ServletRequest servletRequest,
		ServletResponse servletResponse,
		FilterChain chain
	) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest)servletRequest;
		HttpServletResponse response = (HttpServletResponse)servletResponse;
		
		final BasicAuth auth = new BasicAuth();

		if (auth.authenticate(request) && 
				isValidUser(request,
						auth.getUsername(),
						auth.getPassword() ) ) {
			chain.doFilter(request, response);
            return;
		}

		response.setHeader("WWW-Authenticate", "Basic Realm=\"jwiki\"");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}
}