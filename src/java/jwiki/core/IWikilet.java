package jwiki.core;

import java.io.Writer;
import java.util.List;

/**
 * IWikilet
 * @author kazuhiko arase
 */
public interface IWikilet {

	/**
	 * @return �O���[�v���̃p�^�[���B
	 */
	String pattern();

	/**
	 * @return �O���[�v���I���̃p�^�[���B �ʏ�� null ��Ԃ��܂��B 
	 * null �ȊO�̒l��Ԃ��ꍇ�A�p�^�[������v����܂ŃO���[�v������܂��B
	 */
	String endPattern();
	
	void render(IWikiContext context, List<ILine<String[]>> groupList,
			Writer out) throws Exception;
}
