package jwiki.servlet.i18n;

import java.util.ListResourceBundle;

/**
 * WikiResource
 * @author kazuhiko arase
 */
public class WikiResource extends ListResourceBundle {
	@Override
	protected Object[][] getContents() {
		return new Object[][]{
				
			{"label.top", "Top"},
			{"label.back", "Back"},
			{"label.edit", "Edit"},
			{"label.history", "History"},
			{"label.new_page", "NewPage"},
			{"label.attached_file", "Attached File"},
			{"label.save", "Save"},
			{"label.preview", "Preview"},
			{"label.message", "Message"},
			{"label.name", "Name"},
			{"label.size", "Size"},
			{"label.date", "Date"},
			{"label.author", "Author"},
			{"label.compare", "Compare"},
			{"label.edit_anyway", "Edit Anyway"},
			{"label.latest", "Latest"},
			{"label.editing", "Editing"},
			{"label.tilda", "~"},
			
			{"message.bad_page_name", "bad page name."},
			{"message.select_compare_targets",
				"select compare targets."},
			{"message.locked", " is locking."},
						
		};
	}
}