package jwiki.util.diff;

/**
 * CharSequenceDiffComparable
 * @author kazuhiko arase
 */
public class CharSequenceDiffComparable 
implements IDiffComparable {
	private final CharSequence a;
	private final CharSequence b;
	public CharSequenceDiffComparable(
		final CharSequence a,
		final CharSequence b
	) {
		this.a = a;
		this.b = b;
	}
	public boolean equals(final int x, final int y) {
		return a.charAt(x) == b.charAt(y);
	}
	public int getM() {
		return a.length();
	}
	public int getN() {
		return b.length();
	}
}