package jwiki.util.diff;

/**
 * IDiffComparable
 * @author kazuhiko arase
 */
public interface IDiffComparable {
	boolean equals(int x, int y);
	int getM();
	int getN();
}