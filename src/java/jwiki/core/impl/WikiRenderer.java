package jwiki.core.impl;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;
import jwiki.core.IWikiRendererWorker;
import jwiki.core.IParagraphDecorator;
import jwiki.core.Util;

/**
 * WikiRenderer
 * @author kazuhiko arase
 */
public class WikiRenderer {

	private IParagraphDecorator lastDecorator = null;
	private List<ILine<String[]>> groupList = null;

	public WikiRenderer() {
	}

	public void render(
		IWikiContext context,
		IWikiRendererWorker worker,
		String plainText
	) throws Exception {
		
		context.getPageScope().clear();
		int lineNumber = 0;
		BufferedReader in = new BufferedReader(
				new StringReader(plainText) );
		try {
			String line;
			while ( (line = in.readLine() ) != null) {
				lineNumber += 1;
				doLine(context, worker, 
					new Line<String>(lineNumber, Util.rtrim(line) ) );
			}
			flush(context, worker);
		} finally {
			in.close();
		}
	}

	protected void flush(
		IWikiContext context,
		IWikiRendererWorker worker
	) throws Exception {
		if (lastDecorator != null) {
			worker.render(context, lastDecorator, groupList);
			lastDecorator = null;
			groupList = null;
		}
	}
	
	protected void doLine(
		IWikiContext context,
		IWikiRendererWorker worker,
		ILine<String> line
	) throws Exception {

		if (endPatternFound(context, worker, line) ) {
			return;
		}

		for (IParagraphDecorator decorator : context.getDecorators() ) {
			Pattern pat = Pattern.compile(decorator.pattern() );
			Matcher mat = pat.matcher(line.get());
			if (mat.find() ) {
				if (lastDecorator == null || lastDecorator != decorator) {
					flush(context, worker);
					lastDecorator = decorator;
					groupList = new ArrayList<ILine<String[]>>();
				}
				groupList.add(new Line<String[]>(
						line.getLineNumber(), group(mat) ) );
				return;
			}
		}
		
		// ありえない条件
		throw new IllegalStateException("no decorator matches.");
	}
	
	private boolean endPatternFound(
		IWikiContext context,
		IWikiRendererWorker worker,
		ILine<String> line
	) throws Exception {

		if (lastDecorator == null) {
			return false;
		}
		
		String endPattern = lastDecorator.endPattern(groupList.get(0) );
		if (endPattern == null) {
			return false;
		}

		Pattern pat = Pattern.compile(endPattern);
		Matcher mat = pat.matcher(line.get() );
		if (mat.find() ) {
			// end.
			groupList.add(new Line<String[]>(
					line.getLineNumber(), group(mat) ) );
			flush(context, worker);
		} else {
			// not end.
			groupList.add(new Line<String[]>(
					line.getLineNumber(), new String[]{line.get()}) );
		}
		return true;
	}
	
	private String[] group(Matcher mat) {
		String[] groups = new String[mat.groupCount() + 1];
		for (int i = 0; i < groups.length; i += 1) {
			groups[i] = mat.group(i);
		}
		return groups;
	}
}