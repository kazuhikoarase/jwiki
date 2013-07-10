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
				
			{"label.top", "トップ"},
			{"label.back", "戻る"},
			{"label.edit", "編集"},
			{"label.history", "履歴"},
			{"label.new_page", "新しいページ"},
			{"label.attached_file", "添付ファイル"},
			{"label.save", "保存"},
			{"label.preview", "プレビュー"},
			{"label.message", "メッセージ"},
			{"label.name", "名前"},
			{"label.size", "サイズ"},
			{"label.date", "日付"},
			{"label.author", "更新者"},
			{"label.compare", "比較"},
			{"label.edit_anyway", "それでも編集する"},
			{"label.latest", "最新"},
			{"label.editing", "編集中"},
			{"label.tilda", "\u301c"},
			
			{"message.no_message", "<no message>"},
			{"message.bad_page_name", "ページ名に使用できない文字が含まれています。"},
			{"message.select_compare_targets",
				"比較対象を選択して下さい。"},
			{"message.locked", "さんが編集中です。"},
		};
	}
}