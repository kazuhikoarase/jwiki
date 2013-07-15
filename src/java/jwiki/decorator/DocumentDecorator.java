package jwiki.decorator;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;
import jwiki.core.Util;

/**
 * DocumentDecorator
 * @author kazuhiko arase
 */
public class DocumentDecorator extends AbstractDecorator {

	private static final String KEY_ID = "id";
	
	public String pattern() {
		return "^(\\>+)\\s*([0-9\\-]*\\))?(.*)$";
	}

	@Override
	public List<String> normalize(IWikiContext context,
			List<ILine<String[]>> groupList) throws Exception {
	
		IndentContext ic = new IndentContext();
		int lastIndent = -1;
		
		XrefManager xm = new XrefManager();
		
		List<List<Object>> lazyLines = new ArrayList<List<Object>>();
		
		for (ILine<String[]> group : groupList) {
			
			final String leading = group.get()[1];
			final int indent = leading.length(); 
			final String header = group.get()[2]; 
			final String desc = group.get()[3]; 

			if (lastIndent == indent) {
			} else if (lastIndent < indent) {
				ic.push(indent, null, null, null);
			} else {
				while (lastIndent > indent && ic.size() > 1) {
					ic.pop(null);
					lastIndent = ic.peek().getIndent(); 
				}
			}

			List<Object> lazyLine = new ArrayList<Object>();

			lazyLine.add(leading + ' ');

			if (header != null) {
				
				Integer id = (Integer)ic.peek().getAttribute(KEY_ID);
				if (id == null) {
					id = Integer.valueOf(0);
				}
				id = Integer.valueOf(id.intValue() + 1);
				ic.peek().setAttribute(KEY_ID, id);

				String newHeader = buildHeader(ic);
				xm.putRef(header, Util.trim(desc), newHeader);
				lazyLine.add(newHeader);
			}

			Matcher mat = Pattern.compile("(\\[xref\\:)(\\S+)(.*)(\\])").
					matcher(desc);
			int start = 0;

			while (mat.find(start) ) {
				lazyLine.add(desc.substring(start, mat.start() ) );
				lazyLine.add(mat.group(1) );
				lazyLine.add(xm.getRef(Util.trim(mat.group(2) ) ) );
				lazyLine.add(mat.group(4) );
				start = mat.end();
			}
			lazyLine.add(desc.substring(start) );
			
			lazyLines.add(lazyLine);
			
			lastIndent = indent;
		}

		List<String> list = new ArrayList<String>();
		for (List<Object> lazy : lazyLines) {
			list.add(concat(lazy) );
		}
		return list;
	}
	
	public void render(
		IWikiContext context,
		List<ILine<String[]>> groupList,
		Writer out
	) throws Exception {

		final String tag = "div";
		final String attrs = "class=\"jwiki-doc\"";

		IndentContext ic = new IndentContext();
		int lastIndent = -1;

		StringBuilder buf = new StringBuilder();
		
		for (ILine<String[]> group : groupList) {

			final String leading = group.get()[1];
			final int indent = leading.length(); 
			final String header = group.get()[2]; 
			final String desc = group.get()[3]; 

			if (lastIndent != indent) {
				if (!Util.isEmpty(buf) ) {
					context.render(out, buf.toString() );
					buf = new StringBuilder();
				}
			}

			if (lastIndent == indent) {
			} else if (lastIndent < indent) {
				ic.push(indent, tag, attrs, out);
			} else {
				while (lastIndent > indent && ic.size() > 1) {
					ic.pop(out);
					lastIndent = ic.peek().getIndent(); 
				}
			}

			if (header != null) {
				if (lastIndent >= indent) {
					if (!Util.isEmpty(buf) ) {
						context.render(out, buf.toString() );
						buf = new StringBuilder();
					}
					ic.pop(out);
					ic.push(indent, tag, attrs, out);
				}
				buf.append("###");
				buf.append(Util.trim(header) );
				buf.append('\u0020');
				buf.append(Util.trim(desc) );
				buf.append('\n');
			} else {
				buf.append(desc);
				buf.append('\n');
			}

			lastIndent = indent;
		}

		if (!Util.isEmpty(buf) ) {
			context.render(out, buf.toString() );
		}
		
		while (ic.size() > 0) {
			ic.pop(out);
		}
	}
	
	public static String concat(List<Object> list) {
		StringBuilder buf = new StringBuilder();
		for (Object o : list) {
			buf.append(o);
		}
		return buf.toString();
	}

	public static String buildHeader(IndentContext ic) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < ic.size(); i += 1) {
			if (i > 0) {
				buf.append('-');
			}
			Integer currId = 
					(Integer)ic.get(i).getAttribute(KEY_ID);
			if (currId != null) {
				buf.append(currId);
			} else {
				buf.append('?');
			}
		}
		buf.append(')');
		return buf.toString();
	}
}

