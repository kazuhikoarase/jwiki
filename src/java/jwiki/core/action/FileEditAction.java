package jwiki.core.action;

import java.io.Writer;
import java.util.List;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;
import jwiki.core.IWikiRendererWorker;
import jwiki.core.IWikilet;
import jwiki.core.PathUtil;
import jwiki.core.Util;
import jwiki.core.WikiUtil;
import jwiki.core.wikilet.AttachedFileWikilet;
import jwiki.fs.IContent;
import jwiki.fs.IFile;
import jwiki.util.Base64;

import org.apache.commons.fileupload.FileItem;

/**
 * FileEditAction
 * @author kazuhiko arase
 */
public class FileEditAction extends WikiAction {

	@Override
	public void outputResponse() throws Exception {

		String method = getParameter("m");

		if (Util.isEmpty(method) ) {

		    IContent content = context.get(context.getPath(), -1);
		    IFile file = context.getFile(context.getPath(), -1);
		    setParameter("pageName", PathUtil.getName(context.getPath() ) );
			setParameter("revision", String.valueOf(file.getRevision() ) );
			setParameter("data", dataToString(content.getData() ) );
			setParameter("message", "");

			if (file.exists() ) {
				tryLock(file);
			}

		} else if ("s".equals(method) ) {
			// save
			if (save() ) {
				return;
			}
		} else if ("a".equals(method) ) {
			attachFile();
		}

		super.outputResponse();
	}

	private void tryLock(IFile file) throws Exception {
		boolean force = "t".equals(getParameter("f") );
		String owner = lock(file, force);
		if (owner != null) {
			if (force) {
				// 強制ロック失敗
				throw new Exception("fail to force lock");
			}
			context.getRequestScope().put("locked", owner);
		}
	}

	private String lock(IFile file, boolean force) throws Exception {

		String owner = context.getLockOwner(file.getPath() );
		if (owner != null && !owner.equals(context.getUsername() ) ) {
			// 所有者が現在のユーザと異なる
			if (!force) {
				return owner;
			}
		}
		context.lock(file.getPath(), file.getRevision() );
		return null;
	}
	
	private void attachFile() throws Exception {

		FileItem fi = getFileItem("attached");
		
		// data に追加して上書き
		final StringBuilder buf = new StringBuilder();

		IWikiRendererWorker worker = new IWikiRendererWorker() {
			public void render(IWikiContext context,
					IWikilet wikilet, List<ILine<String[]>> groupList) throws Exception {
				if (wikilet instanceof AttachedFileWikilet) {
					// 既存の添付ファイルは消す。
					return;
				}
				for (ILine<String[]> group : groupList) {
					buf.append(group.get()[0]);
					buf.append("\n");
				}
			}
		};
		
		context.render(worker, getParameter("data") );		
		
		buf.append("\n");
		buf.append("\n");
		buf.append("[[attached]]");
		byte[] encoded = Base64.encode(fi.get() );
		for (int i = 0; i < encoded.length; i += 1) {
			if (i % 76 == 0) {
				buf.append("\n");
			}
			buf.append( (char)encoded[i]);
		}

		setParameter("pageName", fi.getName() );
		setParameter("data", buf.toString() );
	}

	private boolean save() throws Exception {

		String pageName = Util.trim(getParameter("pageName") );
		String revision = getParameter("revision");
		String data = Util.rtrim(getParameter("data") );
		String message = Util.trim(getParameter("message") );
		
		String parent = PathUtil.getParent(context.getPath() );
		String path = PathUtil.buildPath(parent, pageName);

		if (!PathUtil.isValidPath(pageName) ) {
			// ページ名不正
			context.getRequestScope().put("errorMessage",
					context.getString("message.bad_page_name") );
			return false;
		}

		message = Util.coalesce(message,
			context.getString("message.no_message") );

		if (data.length() > 0) {

			// 追加・更新
			context.put(path,
				Long.valueOf(revision),
				stringToData(data),
				null,
				message);

			// ロック解除
			context.unlock(path);
			
			response.sendRedirect(response.encodeRedirectURL(
				context.createPathUrlEncoded(path) ) );

		} else {

			// 削除
			context.remove(path, message);

			// ブランクの親フォルダを連鎖して消す。
			while (parent.length() > 0 &&
					context.listFiles(parent).size() == 0) {
				context.remove(parent, message);
				parent = PathUtil.getParent(parent);
			}

		    response.sendRedirect(response.encodeRedirectURL(
				context.createPathUrlEncoded(parent) ) );
		}

		return true;
	}

	public void writeWikiPage(Writer out) throws Exception {

		String pageName = Util.trim(getParameter("pageName") );
		String revision = getParameter("revision");
		String data = Util.rtrim(getParameter("data") );
		String message = Util.trim(getParameter("message") );

		IFile file = context.getFile(context.getPath(), -1);

		if (file.exists() ) {
			out.write("<div class=\"action-area\">");
			writeLinkButton(out,
				context.createPathUrlEncoded(context.getPath() ),
				context.getString("label.back") );
			out.write("</div>");
		} else {
			// 存在しない場合、親ディレクトリ
			out.write("<div class=\"action-area\">");
			writeLinkButton(out,
				context.createPathUrlEncoded(
				PathUtil.getParent(context.getPath() ) ),
				context.getString("label.back") );
			out.write("</div>");
		}
		
		String lockOwner = (String)context.getRequestScope().get("locked");
		if (lockOwner != null) {
			// ロックされている
			out.write("<div class=\"jwiki-error-message\">");
			WikiUtil.writeEscaped(out, lockOwner);
			out.write(' ');
			WikiUtil.writeEscaped(out, context.getString("message.locked") );
			out.write("</div>");
			out.write("<div>");
			out.write("<a href=\"?v=e&f=t\">");
			WikiUtil.writeEscaped(out, context.getString("label.edit_anyway") );
			out.write("</a>");
			out.write("</div>");

			context.render(out, data);
			return;
		}

		out.write("<form method=\"POST\" action=\"\" enctype=\"multipart/form-data\">");
		out.write("<input type=\"hidden\" name=\"m\" value=\"\" />");

		out.write("<input type=\"hidden\" name=\"revision\" value=\"");
		WikiUtil.writeEscaped(out, revision);
		out.write("\" />");

		String errorMessage = 
			(String)context.getRequestScope().get("errorMessage");
		if (errorMessage != null) {
			out.write("<div class=\"jwiki-error-message\">");
			WikiUtil.writeEscaped(out, errorMessage);
			out.write("</div>");
		}
		
		out.write("<input type=\"text\"");
		out.write(" style=\"width:720px;\"");
		out.write(" name=\"pageName\" value=\"");
		WikiUtil.writeEscaped(out, pageName);
		out.write("\" />");
		
		out.write("<br/>");

		out.write("<textarea name=\"data\"");
		out.write(" class=\"jwiki-code\"");
		out.write(" style=\"width:720px;height:200px;\">");

		WikiUtil.writeEscaped(out, data);

		out.write("</textarea>");

		out.write("<br/>");

		WikiUtil.writeEscaped(out, context.getString("label.attached_file") );
		out.write(": ");
		out.write("<input type=\"file\" name=\"attached\"");
		out.write(" onchange=\"form.m.value='a';form.submit();\" />");

		out.write("<br/>");

		WikiUtil.writeEscaped(out, context.getString("label.message") );
		out.write(": ");
		out.write("<input type=\"text\" name=\"message\"");
		out.write(" value=\"");
		WikiUtil.writeEscaped(out, message);
		out.write("\"");
		out.write(" style=\"width:400px;\"");
		out.write(" />");

		out.write("<br/>");
		
		// ボタン
		outputSubmitButton(out, context.getString("label.preview"), "p");
		outputSubmitButton(out, context.getString("label.compare"), "c");
		outputSubmitButton(out, context.getString("label.save"), "s");
		
		out.write("</form>");

		String method = getParameter("m");
		
		if ("c".equals(method) ) {

			String latestData = dataToString(context.get(context.getPath(), -1).getData() );
			context.getRequestScope().put("lText", data);
			context.getRequestScope().put("rText", latestData);

			String text = String.format("[[diff(%s,%s,%s,%s)]]",
				context.getString("label.editing"), "lText",
				context.getString("label.latest"), "rText");

			context.render(out, text);
			
		} else if (!Util.isEmpty(method) ) {
			// プレビュー表示
			context.render(out, data);
		}
	}
	
	private void outputSubmitButton(
			Writer out, String label, String method) throws Exception {
		out.write("<input type=\"submit\" value=\"");
		WikiUtil.writeEscaped(out, label);
		out.write("\" onclick=\"form.m.value='");
		out.write(method);
		out.write("';return true;\" />");
		
	}
}