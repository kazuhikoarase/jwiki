package jwiki.servlet.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import jwiki.core.IWikiPage;
import jwiki.core.impl.WikiWriter;
import jwiki.servlet.Constants;

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
			WikiWriter out = new WikiWriter();
			wikiPage.render(out, getBodyContent().getString() );
			out.writeTo(pageContext.getOut() );
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