package jwiki.core.wikilet;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;
import jwiki.core.PathUtil;
import jwiki.core.Util;
import jwiki.core.WikiUtil;

/**
 * NavigatorWikilet
 * @author kazuhiko arase
 */
public class NavigatorWikilet extends SimpleWikilet {

	public String pattern() {
		return "^\\[\\[navigator\\]\\]$";
	}
	
	public void render(
		IWikiContext context,
		ILine<String[]> group,
		Writer out
	) throws Exception {

		List<String> pathList = new ArrayList<String>();
		String path = context.getPath();
		while (true) {
			pathList.add(path);
			if (Util.isEmpty(path) ) {
				break;
			}
			path = PathUtil.getParent(path);
		}
		Collections.reverse(pathList);

		for (int i = 0; i < pathList.size(); i += 1) {
			
			if (i > 0) {
				out.write("/");
			}

			String dir = pathList.get(i);
			String name = PathUtil.getName(dir);
			String label = Util.coalesce(name,
					context.getString("label.top") );

			if (i == pathList.size() - 1) {
				out.write("<span class=\"jwiki-button jwiki-current\">");
				WikiUtil.writeEscaped(out, label);
				out.write("</span>");
			} else {
				out.write("<span class=\"jwiki-button\">");
				out.write("<a href=\"");
				out.write(context.createPathUrlEncoded(dir) );
				out.write("\">");
				WikiUtil.writeEscaped(out, label);
				out.write("</a>");
				out.write("</span>");
			}
		}

	}
}