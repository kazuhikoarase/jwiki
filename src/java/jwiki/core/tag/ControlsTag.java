package jwiki.core.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import jwiki.core.Constants;
import jwiki.core.IWikiPage;

/**
 * ControlsTag
 * @author kazuhiko arase
 */
@SuppressWarnings("serial")
public class ControlsTag extends TagSupport {

	@Override
	public int doStartTag() throws JspException {
		IWikiPage wikiPage = (IWikiPage)pageContext.
				getRequest().getAttribute(Constants.JWIKI_PAGE);
		try {
			wikiPage.writeControls(pageContext.getOut() );
		} catch(Exception e) {
			throw new JspException(e);
		}
		return EVAL_PAGE;
	}
}