package jwiki.decorator;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jwiki.core.ILine;
import jwiki.core.IWikiContext;
import jwiki.core.IWikiWriter;
import jwiki.util.diff.CharSequenceDiffComparable;
import jwiki.util.diff.Diff;
import jwiki.util.diff.IPathTracer;
import jwiki.util.diff.ListDiffComparable;

/**
 * DiffDecorator
 * @author kazuhiko arase
 */
public class DiffDecorator extends SimpleDecorator {

	public String pattern() {
		return "^\\[\\[diff\\((.+),(.+),(.+),(.+)\\)\\]\\]$";
	}

	public void render(
		IWikiContext context,
		ILine<String[]> group,
		IWikiWriter out
	) throws Exception {

		String lLabel = group.get()[1];
		String lName = group.get()[2];
		String rLabel = group.get()[3];
		String rName = group.get()[4];

		List<String> lTextList = toList( (String)context.getRequestScope().get(lName) );
		List<String> rTextList = toList( (String)context.getRequestScope().get(rName) );

		Diff diff = new Diff();
		diff.onp(new ListDiffComparable<String>(lTextList, rTextList) );
		final List<int[]> deltaList = new ArrayList<int[]>();
		diff.trace(new IPathTracer() {
			@Override
			public void trace(int deltaX, int deltaY) {
				deltaList.add(new int[]{deltaX, deltaY});
			}
		});
		Collections.reverse(deltaList);

		DiffContext dc = new DiffContext(lTextList, rTextList);

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
		
		for (int d = 0; d < deltaList.size(); d += 1) {

			int[] delta = deltaList.get(d);
			int lDelta = delta[0];
			int rDelta = delta[1];

			if (isConflict(deltaList, d) ) {
				
				// conflict
				
				int[] delta2 = deltaList.get(d + 1);
				lDelta += delta2[0];
				rDelta += delta2[1];

				for (int i = 0; i < lDelta; i += 1) {
					outputConflictRow(out, dc);
				}
				
				d += 1;

			} else if (lDelta == 0) {
				// right only
				for (int i = 0; i < rDelta; i += 1) {
					outputRightOnlyRow(out, dc);
				}
			} else if (rDelta == 0) {
				// left only
				for (int i = 0; i < lDelta; i += 1) {
					outputLeftOnlyRow(out, dc);
				}
			} else {
				// match
				for (int i = 0; i < lDelta; i += 1) {
					outputMatchRow(out, dc);
				}
			}
		}

		out.write("</table>");		
	}
	
	private boolean isConflict(List<int[]> deltaList, int d) {
		if (d + 1 < deltaList.size() ) {
			int[] delta = deltaList.get(d);
			int[] delta2 = deltaList.get(d + 1);
			return
				delta[0] == 0 && delta2[1] == 0 && delta[1] == delta2[0] ||
				delta[1] == 0 && delta2[0] == 0 && delta[0] == delta2[1];
		}
		return false;
	}

	private List<String[]> diff(final String lText, final String rText)
	throws Exception {
		final List<String[]> list = new ArrayList<String[]>();
		final int[] pos = new int[] {lText.length(), rText.length()};
		Diff diff = new Diff();
		diff.onp(new CharSequenceDiffComparable(lText, rText) );
		diff.trace(new IPathTracer() {
			public void trace(int deltaX, int deltaY) {
				pos[0] -= deltaX;
				pos[1] -= deltaY;
				list.add(new String[] {
					lText.substring(pos[0], pos[0] + deltaX),
					rText.substring(pos[1], pos[1] + deltaY) });
			}
		});
		Collections.reverse(list);
		return list;
	}

	private void outputConflictRow(IWikiWriter out, DiffContext dc) throws Exception {

		List<String[]> list = diff(dc.getLText(), dc.getRText() );

		out.write("<tr>");
		out.write("<td class=\"diff-line-no\">");
		out.write(String.valueOf(dc.getLIndex() + 1) );
		out.write("</td>");
		out.write("<td class=\"diff-line-no\">");
		out.write(String.valueOf(dc.getRIndex() + 1) );
		out.write("</td>");
		out.write("<td class=\"jwiki-code diff-rdiv\">");
		for (String[] item : list) {
			if (item[0].length() == 0) {
				out.write("<span class=\"jwiki-code diff-right-only");
				out.write(getClassSuffix(dc.getIndex() ) );
				out.write("\">");
				out.writeEscaped(item[1], true);
				out.write("</span>");
			} else if (item[1].length() == 0) {
				out.write("<span class=\"jwiki-code diff-left-only");
				out.write(getClassSuffix(dc.getIndex() ) );
				out.write("\">");
				out.writeEscaped(item[0], true);
				out.write("</span>");
			} else {
				out.writeEscaped(item[0], true);
			}
		}
		out.write("</td>");
		out.write("</tr>");
		dc.increment(true, true);
	}	

	private void outputLeftOnlyRow(IWikiWriter out, DiffContext dc) throws Exception {
		out.write("<tr>");
		out.write("<td class=\"diff-line-no\">");
		out.write(String.valueOf(dc.getLIndex() + 1) );
		out.write("</td>");
		out.write("<td class=\"diff-line-no\"></td>");
		out.write("<td class=\"jwiki-code diff-rdiv diff-left-only");
		out.write(getClassSuffix(dc.getIndex() ) );
		out.write("\">");
		out.writeEscaped(dc.getLText(), true);
		out.write("</td>");
		out.write("</tr>");
		dc.increment(true, false);
	}	

	private void outputRightOnlyRow(IWikiWriter out, DiffContext dc) throws Exception {
		out.write("<tr>");
		out.write("<td class=\"diff-line-no\"></td>");
		out.write("<td class=\"diff-line-no\">");
		out.write(String.valueOf(dc.getRIndex() + 1) );
		out.write("</td>");
		out.write("<td class=\"jwiki-code diff-rdiv diff-right-only");
		out.write(getClassSuffix(dc.getIndex() ) );
		out.write("\">");
		out.writeEscaped(dc.getRText(), true);
		out.write("</td>");
		out.write("</tr>");
		dc.increment(false, true);

	}	
	
	private void outputMatchRow(IWikiWriter out, DiffContext dc) throws Exception {
		out.write("<tr class=\"src");
		out.write(getClassSuffix(dc.getIndex() ) );
		out.write("\">");
		out.write("<td class=\"diff-line-no\">");
		out.write(String.valueOf(dc.getLIndex() + 1) );
		out.write("</td>");
		out.write("<td class=\"diff-line-no\">");
		out.write(String.valueOf(dc.getRIndex() + 1) );
		out.write("</td>");
		out.write("<td class=\"jwiki-code diff-rdiv\">");
		out.writeEscaped(dc.getLText(), true);
		out.write("</td>");
		out.write("</tr>");
		dc.increment(true, true);
	}
	
	private List<String> toList(String s) throws Exception {
		BufferedReader in = new BufferedReader(new StringReader(s) );
		try {
			List<String> lines = new ArrayList<String>();
			String line;
			while ( (line = in.readLine() ) != null) {
				lines.add(line);
			}
			return lines;
		} finally {
			in.close();
		}
	}
	
	private String getClassSuffix(int index) {
		return (index % 2 == 0)? "-even" : "-odd";
	}

	protected static class DiffContext {
		private List<String> lTextList;
		private List<String> rTextList;
		private int lIndex;
		private int rIndex;
		private int index;
		public DiffContext(List<String> lTextList, List<String> rTextList) {
			this.lTextList = lTextList;
			this.rTextList = rTextList;
			this.lIndex = 0;
			this.rIndex = 0;
			this.index = 0;
		}
		public void increment(boolean l, boolean r) {
			if (l) {
				lIndex += 1;
			}
			if (r) {
				rIndex += 1;
			}
			index += 1;
		}
		public String getLText() {
			return lTextList.get(lIndex);
		}
		public String getRText() {
			return rTextList.get(rIndex);
		}
		public int getIndex() {
			return index;
		}
		public int getLIndex() {
			return lIndex;
		}
		public int getRIndex() {
			return rIndex;
		}
	}
}