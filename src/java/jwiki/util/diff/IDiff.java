package jwiki.util.diff;

/**
 * IDiff
 * @author kazuhiko arase
 */
public interface IDiff {
	int onp(IDiffComparable target);
	void trace(IPathTracer tracer);
}