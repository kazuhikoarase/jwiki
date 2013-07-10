package jwiki.core.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import jwiki.core.Constants;
import jwiki.core.IWikiPage;

/**
 * RenderTag
 * @author kazuhiko arase
 */
@SuppressWarnings("serial")
public class RenderTag extends BodyTagSupport {

	@Override
	public int doEndTag() throws JspException {
		IWikiPage wikiPage = (IWikiPage)pageContext.
				getRequest().getAttribute(Constants.JWIKI_PAGE);
		try {
			wikiPage.render(pageContext.getOut(), 
				getBodyContent().getString() );
		} catch(Exception e) {
			throw new JspException(e);
		}
		return EVAL_PAGE;
	}

	@Override
	public int doAfterBody() throws JspException {
		return SKIP_BODY;
	}
}