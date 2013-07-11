package jwiki.servlet.auth;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import jwiki.util.Base64;

/**
 * BasicAuth
 * @author kazuhiko arase
 */
public class BasicAuth {

	private static final String US_ASCII = "ISO-8859-1";
	
	private static final String BASIC_AUTH_TYPE = "basic";
	
	private String username;
	
	private String password;
	
	public BasicAuth() {
	}

	public boolean authenticate(HttpServletRequest request) {

		String auth = request.getHeader("Authorization");
		String encoding = request.getCharacterEncoding();
    	if (encoding == null) {
    		encoding = US_ASCII;
    	}
    	setUsername(null);
    	setPassword(null);

        if(auth == null || auth.length() < BASIC_AUTH_TYPE.length() ) {
            return false;
        }

        String authType = auth.substring(0, BASIC_AUTH_TYPE.length() );
        if (!BASIC_AUTH_TYPE.equalsIgnoreCase(authType) ) {
            return false;
        }

        String userAndPass;

        try {
	        userAndPass = new String(Base64.decode(
	            auth.substring(BASIC_AUTH_TYPE.length() ).
	            trim().getBytes("ISO-8859-1") ), encoding);
        } catch(IOException e) {
        	e.printStackTrace();
        	return false;
        }
        
        int index = userAndPass.indexOf(':');
        if (index == -1) {
            return false;
        }

        setUsername(userAndPass.substring(0, index) );
        setPassword(userAndPass.substring(index + 1) );
        
        return true;
    }

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
