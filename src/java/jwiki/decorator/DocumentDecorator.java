package jwiki.decorator;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

			List<Object> buf = new ArrayList<Object>();

			buf.add(leading + ' ');

			if (header != null) {
				
				Integer id = (Integer)ic.peek().getAttribute("id");
				if (id == null) {
					id = Integer.valueOf(0);
				}
				id = Integer.valueOf(id.intValue() + 1);
				ic.peek().setAttribute("id", id);

				buf.add(xm.getAnchor(header, buildHeader(ic) ) );
//				buf.append(header);
			}
			
			//M
			Matcher mat = Pattern.
					compile("(\\[xref\\:)(.*)(\\])").
					matcher(desc);
			int start = 0;
			
			while (mat.find(start) ) {
				buf.add(desc.substring(start, mat.start() ) );
				buf.add(mat.group(1) );
				buf.add(xm.getRef(Util.trim(mat.group(2) ) ) );
				buf.add(mat.group(3) );
				start = mat.end();
			}
			buf.add(desc.substring(start) );
			
			lazyLines.add(buf);
			
			lastIndent = indent;
		}

		List<String> list = new ArrayList<String>();
		for (List<Object> lazy : lazyLines) {
			list.add(concat(lazy) );
		}
		return list;
	}
	
	public String concat(List<Object> list) {
		StringBuilder buf = new StringBuilder();
		for (Object o : list) {
			buf.append(o);
		}
		return buf.toString();
	}

	public static class XrefManager {
		private Map<String,String> xref = new HashMap<String, String>(); 
		public Object getAnchor(String curValue, String newValue) {
			xref.put(curValue, newValue);
			return new Lazy(curValue);
		}
		public Object getRef(String curValue) {
			return new Lazy(curValue);
		}
		private class Lazy {
			private final String curValue;
			public Lazy(String curValue) {
				this.curValue = curValue;
			}
			public String toString() {
				String newValue = xref.get(curValue);
				return Util.coalesce(newValue, curValue);
			}
		}
	}
	
	public String buildHeader(IndentContext ic) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < ic.size(); i += 1) {
			if (i > 0) {
				buf.append('-');
			}
			Integer currId = 
					(Integer)ic.get(i).getAttribute("id");
			if (currId != null) {
				buf.append(currId);
			} else {
				buf.append('?');
			}
		}
		buf.append(')');
		return buf.toString();
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

			final int indent = group.get()[1].length(); 
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
				buf.append("**");
				buf.append(Util.rtrim(header) );
				buf.append("**");
				buf.append('\u0020');
			}
			buf.append(Util.rtrim(desc) );
			buf.append('\n');

			lastIndent = indent;
		}

		if (!Util.isEmpty(buf) ) {
			context.render(out, buf.toString() );
		}
		
		while (ic.size() > 0) {
			ic.pop(out);
		}
	}
}

