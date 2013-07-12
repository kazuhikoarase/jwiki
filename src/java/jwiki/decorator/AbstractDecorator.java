package jwiki.decorator;

import jwiki.core.ILine;
import jwiki.core.IParagraphDecorator;

/**
 * AbstractDecorator
 * @author kazuhiko arase
 */
public abstract class AbstractDecorator implements IParagraphDecorator {
	
	protected AbstractDecorator() {
	}

	public String endPattern(ILine<String[]> startGroup) {
		return null;
	}
}