package jwiki.decorator;

import java.util.ArrayList;
import java.util.List;

import jwiki.core.ILine;
import jwiki.core.IParagraphDecorator;
import jwiki.core.IWikiContext;

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

	public List<String> normalize(IWikiContext context,
			List<ILine<String[]>> groupList) throws Exception {
		List<String> list = new ArrayList<String>();
		for (ILine<String[]> group : groupList) {
			list.add(group.get()[0]);
		}
		return list;
	}
}