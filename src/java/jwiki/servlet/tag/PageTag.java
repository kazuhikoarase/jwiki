package jwiki.servlet.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import jwiki.core.IWikiPage;
import jwiki.core.impl.WikiWriter;
import jwiki.servlet.Constants;

/**
 * PageTag
 * @author kazuhiko arase
 */
@SuppressWarnings("serial")
public class PageTag extends TagSupport {

	@Override
	public int doStartTag() throws JspException {
		IWikiPage wikiPage = (IWikiPage)pageContext.
				getRequest().getAttribute(Constants.JWIKI_PAGE);
		try {
			WikiWriter out = new WikiWriter();
			wikiPage.writeWikiPage(out);
			out.writeTo(pageContext.getOut() );
		} catch(Exception e) {
			throw new JspException(e);
		}
		return EVAL_PAGE;
	}
}