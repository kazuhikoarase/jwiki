package jwiki.core.action;

import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import jwiki.core.Constants;
import jwiki.core.IWikiPage;
import jwiki.core.Util;
import jwiki.core.WikiUtil;

import org.apache.commons.fileupload.DefaultFileItemFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;

/**
 * WikiAction
 * @author kazuhiko arase
 */
public abstract class WikiAction
extends Action implements IWikiPage {

	private static final String DATA_ENCODING = "UTF-8";
	
	private Map<String,String> params;
	
	private List<FileItem> fileItems;

	protected WikiAction() {
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public final void execute() throws Exception {

		params = new HashMap<String, String>();
		
		if (FileUpload.isMultipartContent(request) ) {
			DefaultFileItemFactory factory = new DefaultFileItemFactory();
			factory.setSizeThreshold(1024 * 1024 * 5); // max 5M bytes
			FileUpload fileUpload = new FileUpload(factory);
			fileUpload.setHeaderEncoding(request.getCharacterEncoding() );
			fileItems = (List<FileItem>)fileUpload.parseRequest(request);
		} else {
			fileItems = null;
		}
		
		outputResponse();
	}

	protected void outputResponse() throws Exception {

		String templatePage = (String)request.getAttribute(
				Constants.JWIKI_TEMPLATE_PAGE);
		if (Util.isEmpty(templatePage) ) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"template page not found.");
			return;
		}

		request.setAttribute(Constants.JWIKI_PAGE, this);
		servletContext.getRequestDispatcher(templatePage).
			forward(request, response);
	}

	public void render(Writer out, String plainText) throws Exception {
		context.render(out, plainText);
	}
	
	public String getPath() {
		return context.getPath();
	}

	protected void writeLinkButton(Writer out, String href, String label) 
	throws Exception {
		out.write("<a href=\"");
		out.write(href);
		out.write("\">");
		WikiUtil.writeEscaped(out, label);
		out.write("</a>");
	}

	protected void setParameter(String name, String value)
	throws Exception {
		params.put(name, value);
	}
	
	protected String getParameter(String name)
	throws Exception {
		if (params.containsKey(name) ) {
			return params.get(name);
		}
		if (fileItems == null) {
			return request.getParameter(name);
		}
		FileItem fi = getFileItem(name);
		if (fi != null) {
			return fi.getString(request.getCharacterEncoding() );
		}
		return null;
	}

	protected FileItem getFileItem(String name)
	throws Exception {
		if (fileItems != null) {
			for (FileItem fi : fileItems) {
				if (fi.getFieldName().equals(name) ) {
					return fi;
				}
			}
		}
		return null;
	}
	
	protected String dataToString(byte[] data) throws Exception {
		return new String(data, DATA_ENCODING);
	}
	
	protected byte[] stringToData(String s) throws Exception {
		return s.getBytes(DATA_ENCODING);
	}

}
