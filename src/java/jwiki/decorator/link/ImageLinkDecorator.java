package jwiki.decorator.link;

import java.io.Writer;

import jwiki.core.ILink;
import jwiki.core.IWikiContext;
import jwiki.core.WikiUtil;

public class ImageLinkDecorator extends AbstractLinkDecorator {

	public String getScheme() {
		return "image:";
	}
	
	public void render(IWikiContext context, ILink link,
			Writer out) throws Exception {
		
		String path = toCanonicalPath(context, link.getPath() );

		out.write("<img src=\"");
		out.write(context.createPathUrlEncoded(path) );
		out.write("?raw\" alt=\"");
		WikiUtil.writeEscaped(out, link.getLabel() );
		out.write("\" />");
	}
}