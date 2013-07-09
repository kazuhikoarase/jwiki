package jwiki.util.diff;

import java.util.List;

/**
 * ListDiffComparable
 * @author kazuhiko arase
 */
public class ListDiffComparable<T> 
implements IDiffComparable {
	private final List<T> a;
	private final List<T> b;
	public ListDiffComparable(
		final List<T> a,
		final List<T> b
	) {
		this.a = a;
		this.b = b;
	}
	public boolean equals(final int x, final int y) {
		return a.get(x).equals(b.get(y) );
	}
	public int getM() {
		return a.size();
	}
	public int getN() {
		return b.size();
	}
}