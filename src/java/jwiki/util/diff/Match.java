package jwiki.util.diff;

/**
 * Match
 * @author kazuhiko arase
 */
public final class Match {
	public final int x;
	public final int length;
	public Match(final int x, final int length) {
		this.x = x;
		this.length = length;
	}
	@Override
	public String toString() {
		return "Match(x=" + x + ",length=" + length + ")";
	}
}

