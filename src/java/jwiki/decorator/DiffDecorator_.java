package jwiki.decorator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;
import jwiki.core.IWikiWriter;
import jwiki.util.diff.CharSequenceDiffComparable;
import jwiki.util.diff.Diff;
import jwiki.util.diff.IPathTracer;

/**
 * DiffDecorator
 * @author kazuhiko arase
 */
public class DiffDecorator_ extends SimpleDecorator {

	public String pattern() {
		return "^\\[\\[diff\\((.+),(.+),(.+),(.+)\\)\\]\\]$";
	}

	public void render(
		final IWikiContext context,
		final ILine<String[]> group,
		final IWikiWriter out
	) throws Exception {

		String lLabel = group.get()[1];
		String lName = group.get()[2];
		String rLabel = group.get()[3];
		String rName = group.get()[4];

//		List<String> lTextList = toList( (String)context.getRequestScope().get(lName) );
//		List<String> rTextList = toList( (String)context.getRequestScope().get(rName) );
		String lText = (String)context.getRequestScope().get(lName);
		String rText = (String)context.getRequestScope().get(rName);

		Diff diff = new Diff();
//		diff.onp(new ListDiffComparable<String>(lTextList, rTextList) );
		diff.onp(new CharSequenceDiffComparable(lText, rText) );
		final List<int[]> deltaList = new ArrayList<int[]>();
		diff.trace(new IPathTracer() {
			@Override
			public void trace(int deltaX, int deltaY) {
				deltaList.add(new int[]{deltaX, deltaY});
			}
		});
		Collections.reverse(deltaList);



		out.write("<table class='diff'>");
		out.write("<tr>");
		out.write("<th class=\"diff-left-only-odd\">");
		out.writeEscaped(lLabel);
		out.write("</th>");
		out.write("<th class=\"diff-right-only-odd\">");
		out.writeEscaped(rLabel);
		out.write("</th>");
		out.write("<th></th>");
		out.write("</tr>");
		
		final DiffLine lLine = new DiffLine(lText);
		final DiffLine rLine = new DiffLine(rText);
		final PartBuffer buffer = new PartBuffer();

		final int[] index = {0};

		Runnable flushLine = new Runnable() {
			public void run() {
				try{
					run_();
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			}

			private void run_() throws Exception {

				out.write("<tr>");
				out.write("<td class=\"diff-line-no\">");
				if (buffer.hasLeft() ) {
					out.write(String.valueOf(lLine.getLineNumber() + 1) );
				}
				out.write("</td>");
				out.write("<td class=\"diff-line-no\">");
				if (buffer.hasRight() ) {
					out.write(String.valueOf(rLine.getLineNumber() + 1) );
				}
				out.write("</td>");
				out.write("<td class=\"jwiki-code diff-rdiv\">");
				for (Part part : buffer.getParts() ) {
					int type = part.getType();
					String text = part.getText();
					if (text.startsWith("\n") ) {
			//			text = text.substring(1);
					}
					if (type == 'l') {
						out.write("<span class=\"diff-left-only");
						out.write(getClassSuffix(index[0]) );
						out.write("\">");
						out.writeEscaped(text, true);
						out.write("</span>");
					} else if (type =='r') {
						out.write("<span class=\"diff-right-only");
						out.write(getClassSuffix(index[0]) );
						out.write("\">");
						out.writeEscaped(text, true);
						out.write("</span>");
					} else {
						out.writeEscaped(text, true);
					}
				}
				out.write("</td>");
				out.write("</tr>");

				index[0] += 1;
				buffer.reset();
			}
		};

		for (int d = 0; d < deltaList.size(); d += 1) {

			final int[] delta = deltaList.get(d);
			final int lDelta = delta[0];
			final int rDelta = delta[1];

			if (lDelta == 0) {
				// right only
				for (int i = 0; i < rDelta; i += 1) {
					if (rLine.eol() ) {
						buffer.add('r', rLine.pop() );
						flushLine.run();
					}
					rLine.inc();
				}
				buffer.add('r', rLine.pop() );
			} else if (rDelta == 0) {
				// left only
				for (int i = 0; i < lDelta; i += 1) {
					if (lLine.eol() ) {
						buffer.add('l', lLine.pop() );
						flushLine.run();
					}
					lLine.inc();
				}
				buffer.add('l', lLine.pop() );
			} else {
				// match
				for (int i = 0; i < lDelta; i += 1) {
					if (lLine.eol() ) {
						buffer.add('m', lLine.pop() );
						rLine.pop(); // dispose
						flushLine.run();
					}
					lLine.inc();
					rLine.inc();
				}
				buffer.add('m', lLine.pop() );
				rLine.pop(); // dispose
			}
		}
		flushLine.run();

		out.write("</table>");		
	}
	
	private static class DiffLine {
		private int pos = 0;
		private int start = 0;
		private int lineNumber = 0;
		private final String text;
		public DiffLine(String text) {
			this.text = text;
		}
		public int getLineNumber() {
			return lineNumber;
		}
		public void inc() {
			if (eol() ) {
				lineNumber += 1;
			}
			pos += 1;
		}
		public boolean eol() {
			return text.charAt(pos) == '\n';
		}
		public String pop() {
			String part = text.substring(start, pos);
			start = pos;
			return part;
		}
	}
	
	private static class PartBuffer {
		private boolean hasLeft = false; 
		private boolean hasRight = false; 
		private List<Part> buffer = new ArrayList<Part>();
		public void add(int type, String text) {
			buffer.add(new Part(type, text) );
			hasLeft |= type == 'm' || type == 'l';
			hasRight |= type == 'm' || type == 'r';
		}
		public Iterable<Part> getParts() {
			return buffer;
		}
		public boolean hasLeft() {
			return hasLeft;
		}
		public boolean hasRight() {
			return hasRight;
		}
		public void reset() {
			buffer.clear();
			hasLeft = false;
			hasRight = false;
		}
	}
	private static class Part {
		private final int type;
		private final String text;
		public Part(int type, String text) {
			this.type = type;
			this.text = text;
		}
		public int getType() {
			return type;
		}
		public String getText() {
			return text;
		}
	}

	private String getClassSuffix(int index) {
		return (index % 2 == 0)? "-even" : "-odd";
	}
}