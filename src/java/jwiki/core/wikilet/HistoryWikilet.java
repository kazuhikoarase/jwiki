package jwiki.core.wikilet;

import java.io.Writer;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;
import jwiki.core.PathUtil;
import jwiki.core.Util;
import jwiki.core.WikiUtil;
import jwiki.fs.IFile;

/**
 * HistoryWikilet
 * @author kazuhiko arase
 */
public class HistoryWikilet extends SimpleWikilet {

	public String pattern() {
		return "^\\[\\[history\\]\\]$";
	}

	public void render(
		IWikiContext context,
		ILine<String[]> group,
		Writer out
	) throws Exception {
		
		if (context.getFile(context.getPath(), -1).isDirectory() ) {
			// �f�B���N�g���̗����͔�Ή�
			return;
		}

		String id = context.generateUniqueId();
		
		out.write("<form name=\"");
		out.write(id);
		out.write("\">");
		
		out.write("<table class=\"index\">");
		
		out.write("<tr>");
		
		out.write("<th>");
		out.write("<a href=\"javascript:void(0)\" onclick=\"");
		
		out.write("(function() {");
		out.write("var f = document.forms['");
		out.write(id);
		out.write("'];");
		out.write("var $ = function(n) {");
		out.write("var e = f[n];");
		out.write("if(typeof e == 'undefined'){e=[];}");
		out.write("if(typeof e.length == 'undefined'){e=[e];}");
		out.write("return e;");
		out.write("};");
		out.write("var r = function(n) {");
		out.write("var e = $(n);");
		out.write("for(var i = 0; i &lt; e.length; i += 1) {");
		out.write("if (e[i].checked) {return e[i].value;}");
		out.write("}");
		out.write("return '';");
		out.write("};");
		out.write("var lRev = r('lRev');");
		out.write("var rRev = r('rRev');");
		out.write("if (!lRev||!rRev) { alert('");
		WikiUtil.writeEscaped(out, context.getString("message.select_compare_targets") );
		out.write("');return;}");
		out.write("location.href='");
		out.write(context.createPathUrlEncoded(context.getPath() ) );
		out.write("?v=d&lRev='+lRev+'&rRev='+rRev;");
		out.write("}())");

		out.write("\">");
		WikiUtil.writeEscaped(out, context.getString("label.compare") );
		out.write("</a>");
		out.write("</th>");
		out.write("<th></th>");

		out.write("<th>");
		WikiUtil.writeEscaped(out, context.getString("label.name") );
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
		out.write("</tr>");
		
		for (IFile file : context.listHistory(context.getPath() ) ) {
			String revision = String.valueOf(file.getRevision() );
			out.write("<tr>");
			out.write("<td>");
			out.write("<input type=\"radio\" name=\"lRev\" value=\"");
			out.write(revision);
			out.write("\"/>");
			out.write("<input type=\"radio\" name=\"rRev\" value=\"");
			out.write(revision);
			out.write("\"/>");
			out.write("</td>");
			out.write("<td>r");
			out.write(revision);
			out.write("</td>");
			out.write("<td><a href=\"");
			out.write(context.createPathUrlEncoded(file.getPath() ) );
			out.write("?r=");
			out.write(revision);
			out.write("\">");
			WikiUtil.writeEscaped(out, PathUtil.getName(file.getPath() ) );
			out.write("</a></td>");
			out.write("<td>");
			WikiUtil.writeEscaped(out, Util.formatDate(file.getDate() ) );
			out.write("</td>");
			out.write("<td>");
			WikiUtil.writeEscaped(out, file.getAuthor() );
			out.write("</td>");
			out.write("<td>");
			WikiUtil.writeEscaped(out, file.getMessage() );
			out.write("</td>");
			out.write("</tr>");
		}

		out.write("</table>");
		out.write("</form>");
	}
}