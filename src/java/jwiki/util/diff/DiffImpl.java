package jwiki.util.diff;

/**
 * DiffImpl
 * @author kazuhiko arase
 */
class DiffImpl implements IDiff {

	private IDiffComparable target = null;

    private Match[] matches = null;
	
	public DiffImpl() {
	}
	
	public int onp(final IDiffComparable target) {

		if (target.getM() > target.getN() ) {
			throw new IllegalArgumentException(
				target.getM() + ">" + target.getN() );
		}
		
		this.target = target;

		matches = new Match[target.getN() + 1];
		matches[0] = new Match(0, 0);

		final int delta = target.getN() - target.getM();
        final int offset = target.getM() + 1;

		final int[] fp = new int[target.getM() + target.getN() + 3];
		for (int i = 0; i < fp.length; i += 1) {
			fp[i] = -1;
		}

    	int k;
		int p = -1;
		
        do {

        	p += 1;
        	
        	for(k = -p; k < delta; k += 1) {
                fp[offset + k] = snake(k, Math.max(
                		fp[offset + k - 1] + 1,
                		fp[offset + k + 1] ) );
            }
        	
        	for(k = delta + p; k > delta; k -= 1) {
                fp[offset + k] = snake(k, Math.max(
                		fp[offset + k - 1] + 1,
                		fp[offset + k + 1] ) );
            }

			// k == delta
            fp[offset + k] = snake(k, Math.max(
            		fp[offset + k - 1] + 1,
            		fp[offset + k + 1] ) );

        } while (fp[offset + k] != target.getN() );
        
        // returns edit distance
        return delta + 2 * p;
    }

	private int snake(int k, int y) {
		
		int x = y - k;
		int length = 0;

		while(x < target.getM() && y < target.getN() &&
				target.equals(x, y) ) {
            x += 1;
            y += 1;
        	length += 1;
        }

		if (length > 0) {
			matches[y] = new Match(x, length);
		}
		
		return y;
    }
	
	public void trace(final IPathTracer tracer) {

		int x = target.getM();
		int y = target.getN();
		int lastX;
		int lastY;
		
		Match match = null;

		while (x > 0 || y > 0) {

			//-------------------------
			// b only

			lastX = x;
			lastY = y;
			while (y >= 0) {
			 	match = matches[y];
				if (match != null && match.x <= x) {
					// found.
					break;
				}
				y -= 1;
			}	
			if (lastY != y) {
				tracer.trace(lastX - x, lastY - y);
			}

			//-------------------------
			// a only

			lastX = x;
			lastY = y;
			while (x > match.x) {
				x -= 1;
			}
			if (lastX != x) {
				tracer.trace(lastX - x, lastY - y);
			}
			
			//-------------------------
			// match part

			lastX = x;
			lastY = y;
			while (x > match.x - match.length) {
				x -= 1;
				y -= 1;
			}
			if (lastX != x) {
				tracer.trace(lastX - x, lastY - y);
			}
		}
	}	
}	
