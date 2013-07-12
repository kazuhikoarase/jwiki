package jwiki.core;

import java.io.Writer;
import java.util.List;

/**
 * IParagraphDecorator
 * @author kazuhiko arase
 */
public interface IParagraphDecorator {

	/**
	 * @return グループ化のパターン。
	 */
	String pattern();

	/**
	 * @param startGroup グループ化開始のマッチグループ
	 * @return グループ化終了のパターン。 通常は null を返します。 
	 * null 以外の値を返す場合、パターンが一致するまでグループ化されます。
	 */
	String endPattern(ILine<String[]> startGroup);

	List<String> normalize(IWikiContext context,
			List<ILine<String[]>> groupList) throws Exception;
	
	void render(IWikiContext context, List<ILine<String[]>> groupList,
			Writer out) throws Exception;
}
