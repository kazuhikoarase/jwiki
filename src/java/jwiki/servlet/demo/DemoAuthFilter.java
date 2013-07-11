package jwiki.servlet.demo;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import jwiki.fs.IUserInfo;
import jwiki.servlet.Constants;

/**
 * DemoAuthFilter
 * @author kazuhiko arase
 */
public class DemoAuthFilter implements Filter {

	protected FilterConfig config;

	public void init(FilterConfig config) throws ServletException {
		this.config = config;
	}

	public void destroy() {
	}

	public void doFilter(
		ServletRequest request,
		ServletResponse response,
		FilterChain chain
	) throws IOException, ServletException {

		request.setAttribute(Constants.JWIKI_USER, new IUserInfo() {
			public String getUsername() {
				return "demo user";
			}
			public String getPassword() {
				return "";
			}
		} );

		chain.doFilter(request, response);
	}
}