package jwiki.demo;

import javax.servlet.http.HttpServletRequest;

import jwiki.core.Constants;
import jwiki.fs.IUserInfo;
import jwiki.util.BasicAuthFilter;

/**
 * DemoBasicAuthFilter
 * @author kazuhiko arase
 */
public class DemoBasicAuthFilter extends BasicAuthFilter {
	protected boolean isValidUser(
		final HttpServletRequest request,
		final String username, 
		final String password
	) {
		
		request.setAttribute(Constants.JWIKI_USER, new IUserInfo() {
			public String getUsername() {
				return username;
			}
			public String getPassword() {
				return password;
			}
		} );

		return true;
	}
}