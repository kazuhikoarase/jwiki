package jwiki.decorator;

import java.io.ByteArrayOutputStream;
import java.util.List;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;
import jwiki.core.IWikiWriter;
import jwiki.core.PathUtil;

/**
 * AttachedFileDecorator
 * @author kazuhiko arase
 */
public class AttachedFileDecorator extends AbstractDecorator {

	public String pattern() {
		return "^\\[\\[attached\\]\\]$";
	}
	
	public String endPattern(ILine<String[]> startGroup) {
		return "^$";
	}
	
	public void render(
		IWikiContext context,
		List<ILine<String[]>> groupList,
		IWikiWriter out
	) throws Exception {
		out.write("<p>");
		out.write(context.getString("label.attached_file") );
		out.write(": ");
		out.write("<a href=\"");
		out.write(context.createPathUrlEncoded(context.getPath() ) + "?raw");
		out.write("\">");
		out.writeEscaped(PathUtil.getName(context.getPath() ) );
		out.write("</a>");
		out.write("</p>");
	}
	
	public byte[] getContents(
		IWikiContext context,
		List<ILine<String[]>> groupList
	) throws Exception {

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try {
			for (int i = 1; i < groupList.size(); i += 1) {
				String line = groupList.get(i).get()[0];
				bout.write(line.getBytes("ISO-8859-1") );
			}
		} finally {
			bout.close();
		}
		return bout.toByteArray();
	}
	
}

