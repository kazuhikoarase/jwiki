package jwiki.core.wikilet;

import java.io.Writer;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;
import jwiki.core.PathUtil;
import jwiki.core.Util;
import jwiki.core.WikiUtil;
import jwiki.fs.IFile;

/**
 * IndexWikilet
 * @author kazuhiko arase
 */
public class IndexWikilet extends SimpleWikilet {

	public String pattern() {
		return "^\\[\\[index\\]\\]$";
	}
	
	public void render(
		IWikiContext context,
		ILine<String[]> group,
		Writer out
	) throws Exception {

		// ディレクトリの場合直接、それ以外の場合、親ディレクトリ。
		String dir = context.getFile(context.getPath(), -1).isDirectory()?
			context.getPath() :
			PathUtil.getParent(context.getPath() );
		
		out.write("<table class=\"index\">");
		
		out.write("<tr>");
		out.write("<th></th>");
		out.write("<th>");
		WikiUtil.writeEscaped(out, context.getString("label.name") );
		out.write("</th>");
		out.write("<th>");
		WikiUtil.writeEscaped(out, context.getString("label.size") );
		out.write("</th>");
		out.write("<th>");
		WikiUtil.writeEscaped(out, context.getString("label.date") );
		out.write("</th>");
		out.write("<th>");
		WikiUtil.writeEscaped(out, context.getString("label.author") );
		out.write("</th>");
		out.write("<th>");
		WikiUtil.writeEscaped(out, context.getString("label.message") );
		out.write("</th>");
		out.write("<th>");
		WikiUtil.writeEscaped(out, context.getString("label.editing") );
		out.write("</th>");
		out.write("</tr>");
		
		for (IFile file : context.listFiles(dir) ) {
			out.write("<tr>");
			out.write("<td>");
			WikiUtil.writeEscaped(out, file.isDirectory()? "D" : "F");
			out.write("</td>");
			out.write("<td><a href=\"");
			out.write(context.createPathUrlEncoded(file.getPath() ) );
			out.write("\">");
			WikiUtil.writeEscaped(out,
					PathUtil.getName(file.getPath() ) );
			out.write("</a></td>");
			out.write("<td style=\"text-align:right;\">");
			if (file.isFile() ) {
				WikiUtil.writeEscaped(out, Util.formatNumber(file.getSize() ) );
			}
			out.write("</td>");
			out.write("<td>");
			WikiUtil.writeEscaped(out, Util.formatDate(file.getDate() ) );
			out.write("</td>");
			out.write("<td>");
			WikiUtil.writeEscaped(out, file.getAuthor() );
			out.write("</td>");
			out.write("<td>");
			WikiUtil.writeEscaped(out, file.getMessage() );
			out.write("</td>");
			out.write("<td>");
			if (!Util.isEmpty(file.getEditingUser() ) ) {
				out.write(Util.formatDate(file.getEditingDate() ) );
				out.write(context.getString("label.tilda") );
				out.write(' ');
				WikiUtil.writeEscaped(out, file.getEditingUser() );
			}
			out.write("</td>");
			out.write("</tr>");
		}

		out.write("</table>");
	}
}