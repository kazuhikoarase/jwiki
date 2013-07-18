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
public class DiffDecorator extends SimpleDecorator {

	private static final int MATCH = 0;
	private static final int RIGHT_ONLY = 1;
	private static final int LEFT_ONLY = 2;
	
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

		String lText = (String)context.getRequestScope().get(lName);
		String rText = (String)context.getRequestScope().get(rName);

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
		
		new DiffViewHelper().diff(out, lText, rText);
		
		out.write("</table>");		
	}
	
	private static class DiffViewHelper {
		
		private IWikiWriter out;
		private DiffLine lLine;
		private DiffLine rLine;

		private int index = 0;
		private PartBuffer buffer = new PartBuffer();
		private PartBuffer lastBuffer = new PartBuffer();
		
		private int lastLLineNumber = 0;
		private int lastRLineNumber = 0;

		public void diff(IWikiWriter out, String lText, String rText) throws Exception {

			this.out = out;
			this.lLine = new DiffLine(lText);
			this.rLine = new DiffLine(rText);
			
			Diff diff = new Diff();
			diff.onp(new CharSequenceDiffComparable(lText, rText) );
			final List<int[]> deltaList = new ArrayList<int[]>();
			diff.trace(new IPathTracer() {
				@Override
				public void trace(int deltaX, int deltaY) {
					deltaList.add(new int[]{deltaX, deltaY});
				}
			});
			Collections.reverse(deltaList);

			for (int d = 0; d < deltaList.size(); d += 1) {

				final int[] delta = deltaList.get(d);
				final int lDelta = delta[0];
				final int rDelta = delta[1];

				if (lDelta == 0) {
					// right only
					for (int i = 0; i < rDelta; i += 1) {
						if (rLine.eol() ) {
							buffer.add(RIGHT_ONLY, rLine.pop() );
							flushLine();
						}
						rLine.inc();
					}
					buffer.add(RIGHT_ONLY, rLine.pop() );
				} else if (rDelta == 0) {
					// left only
					for (int i = 0; i < lDelta; i += 1) {
						if (lLine.eol() ) {
							buffer.add(LEFT_ONLY, lLine.pop() );
							flushLine();
						}
						lLine.inc();
					}
					buffer.add(LEFT_ONLY, lLine.pop() );
				} else {
					// match
					for (int i = 0; i < lDelta; i += 1) {
						if (lLine.eol() ) {
							buffer.add(MATCH, lLine.pop() );
							rLine.pop(); // dispose
							flushLine();
						}
						lLine.inc();
						rLine.inc();
					}
					buffer.add(MATCH, lLine.pop() );
					rLine.pop(); // dispose
				}
			}
			flushLine();			
		}

		private void writeRow(PartBuffer buffer) throws Exception {
			out.write("<tr>");
			out.write("<td class=\"diff-line-no\">");
			if (buffer.hasLeft() ) {
				out.write(String.valueOf(lastLLineNumber + 1) );
			}
			out.write("</td>");
			out.write("<td class=\"diff-line-no\">");
			if (buffer.hasRight() ) {
				out.write(String.valueOf(lastRLineNumber + 1) );
			}
			out.write("</td>");
			out.write("<td class=\"jwiki-code diff-rdiv\">");
			for (Part part : buffer.getParts() ) {
				
				int type = part.getType();
				String text = part.getText();
				if (text.startsWith("\n") ) {
					text = text.substring(1);
				}

				if (type == LEFT_ONLY) {
					out.write("<span class=\"jwiki-code diff-left-only");
					out.write(getClassSuffix(index) );
					out.write("\">");
					out.writeEscaped(text, true);
					out.write("</span>");
				} else if (type ==RIGHT_ONLY) {
					out.write("<span class=\"jwiki-code diff-right-only");
					out.write(getClassSuffix(index) );
					out.write("\">");
					out.writeEscaped(text, true);
					out.write("</span>");
				} else {
					out.writeEscaped(text, true);
				}
			}
			out.write("</td>");
			out.write("</tr>");
			index += 1;
		}

		private String getClassSuffix(int index) {
			return (index % 2 == 0)? "-even" : "-odd";
		}
		
		private void flushLine() throws Exception {
			
			if (lastLLineNumber != lLine.getLineNumber() &&
					lastRLineNumber != rLine.getLineNumber() ) {
				
				writeRow(lastBuffer);
			
				lastBuffer.reset();
				for (Part part : buffer.getParts() ) {
					lastBuffer.add(part);
				}
				buffer.reset();

			} else {
				
				final PartBuffer tmpLBuffer = new PartBuffer();
				final PartBuffer tmpRBuffer = new PartBuffer();
				
				for (Part part : lastBuffer.getParts() ) {
					if (part.getType() == MATCH) {
						tmpLBuffer.add(LEFT_ONLY, part.getText() );
						tmpRBuffer.add(RIGHT_ONLY, part.getText() );
					} else if (part.getType() == LEFT_ONLY) {
						tmpLBuffer.add(part);
					} else if (part.getType() == RIGHT_ONLY) {
						tmpRBuffer.add(part);
					}
				}
				
				if (lastLLineNumber == lLine.getLineNumber()) {
					writeRow(tmpRBuffer);
					lastBuffer.reset();
					for (Part part : tmpLBuffer.getParts() ) {
						lastBuffer.add(part);
					}
				} else if (lastRLineNumber == rLine.getLineNumber() ) {
					writeRow(tmpLBuffer);
					lastBuffer.reset();
					for (Part part : tmpRBuffer.getParts() ) {
						lastBuffer.add(part);
					}
				}

				for (Part part : buffer.getParts() ) {
					lastBuffer.add(part);
				}
				buffer.reset();
			}

			lastLLineNumber = lLine.getLineNumber();
			lastRLineNumber = rLine.getLineNumber();
		}
		
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
			add(new Part(type, text) );
		}
		public void add(Part part) {
			buffer.add(part);
			hasLeft |= part.getType() == MATCH || part.getType() == LEFT_ONLY;
			hasRight |= part.getType() == MATCH || part.getType() == RIGHT_ONLY;
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

}