package jwiki.core;

import java.io.Writer;
import java.util.List;

/**
 * IWikilet
 * @author kazuhiko arase
 */
public interface IWikilet {

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
	
	void render(IWikiContext context, List<ILine<String[]>> groupList,
			Writer out) throws Exception;
}
