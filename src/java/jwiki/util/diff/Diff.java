package jwiki.util.diff;

/**
 * Diff
 * @author kazuhiko arase
 */
public final class Diff implements IDiff {

	private DiffTargetWrapper wrapper;

	private IDiff diff;
	
	public Diff() {
	}
	
	public int onp(final IDiffComparable target) {
		wrapper = new DiffTargetWrapper(target);
		diff = new DiffImpl();
		return diff.onp(wrapper);
	}

	public void trace(final IPathTracer tracer) {
		diff.trace(new IPathTracer() {
			public void trace(final int deltaX, final int deltaY) {
				if (wrapper.isReverse() ) {
					tracer.trace(deltaY, deltaX);
				} else {
					tracer.trace(deltaX, deltaY);
				}
			}
		} );
	}
	
	private static final class DiffTargetWrapper
	implements IDiffComparable {
		private final IDiffComparable target;
		private final boolean reverse; 
		public DiffTargetWrapper(final IDiffComparable target) {
			this.target = target;
			this.reverse = target.getM() > target.getN();
		}
		public boolean isReverse() {
			return reverse;
		}
		public boolean equals(final int x, final int y) {
			return reverse? target.equals(y, x) : target.equals(x, y);
		}
		public int getM() {
			return reverse? target.getN() : target.getM();
		}
		public int getN() {
			return reverse? target.getM() : target.getN();
		}
	}
}	
