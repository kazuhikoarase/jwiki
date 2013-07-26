package jwiki.servlet.action;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import jwiki.core.ILine;
import jwiki.core.IParagraphDecorator;
import jwiki.core.IWikiContext;
import jwiki.core.IWikiPage;
import jwiki.core.IWikiRendererWorker;
import jwiki.core.IWikiWriter;
import jwiki.core.PathUtil;
import jwiki.core.Util;
import jwiki.decorator.AttachedFileDecorator;
import jwiki.fs.IContent;
import jwiki.fs.IFile;
import jwiki.util.Base64;

/**
 * FileViewAction
 * @author kazuhiko arase
 */
public class FileViewAction extends WikiAction {
	
	private IWikiPage delegate = null;
	
	@Override
	public void outputResponse() throws Exception {

		String qs = request.getQueryString();
		if ("raw".equals(qs) ) {
			downloadAttachedFile();
			return;
		}

		String view = request.getParameter("v");
		if ("h".equals(view) ) {
			delegate = new HistoryPage();
		} else 	if ("d".equals(view) ) {
			delegate = new DiffPage();
		} else {
			delegate = new DefaultPage();
		}

		super.outputResponse();
	}
	
	private void downloadAttachedFile() throws Exception {
	
		final String id = getId();
		final IContent content = context.get(context.getPath(), id);

		final byte[][] data = { null };

		IWikiRendererWorker worker = new IWikiRendererWorker() {
			public void render(IWikiContext context,
					IParagraphDecorator decorator, List<ILine<String[]>> groupList) throws Exception {
				if (decorator instanceof AttachedFileDecorator) {
					AttachedFileDecorator attached = (AttachedFileDecorator)decorator;
					if (data[0] == null) {
						data[0] = attached.getContents(context, groupList);
					}
				}
			}
		};
		
		context.render(worker, dataToString(content.getData() ) );

		if (data[0] == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

        // キャッシュを有効化する。
		response.reset();
        response.setContentType("application/octet-stream");

        // ※IEの仕様により、日本語ファイル名は MS932 に固定
        // http://support.microsoft.com/default.aspx?scid=kb;ja;436616
        // ftp://ftp.rfc-editor.org/in-notes/rfc2231.txt
        String filenameEncoded  = new String(
        		PathUtil.getName(context.getPath() ).
        			getBytes("MS932"), "ISO-8859-1");
        
        response.setHeader("Content-Disposition",
        		"attachment; filename=\"" + filenameEncoded + "\"");

		OutputStream out = new BufferedOutputStream(
				response.getOutputStream() );
		try {
			out.write(Base64.decode(data[0]) );
		} finally {
			out.close();
		}
	}

	public void writeControls(IWikiWriter out) throws Exception {
		delegate.writeControls(out);
	}

	public void writeWikiPage(IWikiWriter out) throws Exception {
		delegate.writeWikiPage(out);
	}
	
	private String getId() {
		return request.getParameter("r");
	}
	
	private void writeFileInfo(IWikiWriter out, IFile file) throws Exception {

		out.write(' ');
		out.write(file.getId() );
		out.write(' ');
		out.write(Util.formatDate(file.getDate() ) );
		out.write(' ');
		out.write(file.getAuthor() );

		if (!Util.isEmpty(file.getEditingUser() ) ) {
			out.write(' ');
			out.write('(');
			out.write(context.getString("label.editing") );
			out.write(' ');
			out.write(Util.formatDate(file.getEditingDate() ) );
			out.write(context.getString("label.tilda") );
			out.write(' ');
			out.write(file.getEditingUser() );
			out.write(')');
		}
	}

	private class DefaultPage extends Delegate {
		public void writeControls(IWikiWriter out) throws Exception {
			final String id = getId();
			final IFile file = context.getFile(	context.getPath(), id);
			
			if (Util.isEmpty(id) ) {

				if (file.exists() ) {

					writeLinkButton(out,
						context.createPathUrlEncoded(context.getPath() ) + "?v=e",
						context.getString("label.edit") );
					out.write("<span class=\"jwiki-spacer\">|</span>");
					writeLinkButton(out,
						context.createPathUrlEncoded(context.getPath() ) + "?v=h",
						context.getString("label.history") );

					writeFileInfo(out, file);

				} else {
					// 存在しない場合
					writeLinkButton(out,
							context.createPathUrlEncoded(context.getPath() ) + "?v=e",
							context.getString("label.edit") );
				}

			} else {
				// 履歴
				writeLinkButton(out,
					context.createPathUrlEncoded(context.getPath() ) + "?v=h",
					context.getString("label.back") );
				writeFileInfo(out, file);
			}
		}
		public void writeWikiPage(IWikiWriter out) throws Exception {
			final String id = getId();
			final IContent content = context.get(context.getPath(), id);
			context.render(out, dataToString(content.getData() ) );
		}
	}
	
	private class HistoryPage extends Delegate {
		public void writeControls(IWikiWriter out) throws Exception {
			writeLinkButton(out,
					context.createPathUrlEncoded(context.getPath() ),
					context.getString("label.back") );
		}
		public void writeWikiPage(IWikiWriter out) throws Exception {
			context.render(out, "[[history]]");
		}
	}

	private class DiffPage extends Delegate {
		public void writeControls(IWikiWriter out) throws Exception {
			writeLinkButton(out,
					context.createPathUrlEncoded(context.getPath() ) + "?v=h",
					context.getString("label.back") );
		}
		public void writeWikiPage(IWikiWriter out) throws Exception {
			String lId = request.getParameter("lId");
			String rId = request.getParameter("rId");
			String lText = dataToString(context.get(context.getPath(), lId).getData() );
			String rText = dataToString(context.get(context.getPath(), rId).getData() );
			context.getRequestScope().put("lText", lText);
			context.getRequestScope().put("rText", rText);
			String text = String.format("[[diff(%s,%s,%s,%s)]]",
					lId, "lText", rId, "rText");
			context.render(out, text);
		}
	}

	private abstract class Delegate implements IWikiPage {
		public String getPath() {
			throw new RuntimeException("not implemented.");
		}
		public void render(IWikiWriter out, String plainText) throws Exception {
			throw new RuntimeException("not implemented.");
		}
	}
}
