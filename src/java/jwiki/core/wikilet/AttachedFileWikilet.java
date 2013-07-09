package jwiki.core.wikilet;

import java.io.ByteArrayOutputStream;
import java.io.Writer;
import java.util.List;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;
import jwiki.core.IWikilet;
import jwiki.core.PathUtil;
import jwiki.core.WikiUtil;

/**
 * AttachedFileWikilet
 * @author kazuhiko arase
 */
public class AttachedFileWikilet implements IWikilet {

	public String pattern() {
		return "^\\[\\[attached\\]\\]$";
	}
	
	public String endPattern() {
		return "^$";
	}
	
	public void render(
		IWikiContext context,
		List<ILine<String[]>> groupList,
		Writer out
	) throws Exception {
		out.write("<p>");
		out.write(context.getString("label.attached_file") );
		out.write(": ");
		out.write("<a href=\"");
		out.write(context.createPathUrlEncoded(context.getPath() ) + "?raw");
		out.write("\">");
		WikiUtil.writeEscaped(out, PathUtil.getName(context.getPath() ) );
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

