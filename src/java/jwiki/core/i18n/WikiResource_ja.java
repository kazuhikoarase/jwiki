package jwiki.core.i18n;

import java.util.ListResourceBundle;

/**
 * WikiResource_ja
 * @author kazuhiko arase
 */
public class WikiResource_ja extends ListResourceBundle {
	@Override
	protected Object[][] getContents() {
		return new Object[][]{
				
			{"label.top", "�g�b�v"},
			{"label.back", "�߂�"},
			{"label.edit", "�ҏW"},
			{"label.history", "����"},
			{"label.new_page", "�V�����y�[�W"},
			{"label.attached_file", "�Y�t�t�@�C��"},
			{"label.save", "�ۑ�"},
			{"label.preview", "�v���r���["},
			{"label.message", "���b�Z�[�W"},
			{"label.name", "���O"},
			{"label.size", "�T�C�Y"},
			{"label.date", "���t"},
			{"label.author", "�X�V��"},
			{"label.compare", "��r"},
			{"label.edit_anyway", "����ł��ҏW����"},
			{"label.latest", "�ŐV"},
			{"label.editing", "�ҏW��"},
			{"label.tilda", "\u301c"},
			
			{"message.no_message", "<no message>"},
			{"message.bad_page_name", "�y�[�W���Ɏg�p�ł��Ȃ��������܂܂�Ă��܂��B"},
			{"message.select_compare_targets",
				"��r�Ώۂ�I�����ĉ������B"},
			{"message.locked", "���񂪕ҏW���ł��B"},
		};
	}
}