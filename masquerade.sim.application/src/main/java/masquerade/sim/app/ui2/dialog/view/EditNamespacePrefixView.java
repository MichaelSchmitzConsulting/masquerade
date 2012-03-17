package masquerade.sim.app.ui2.dialog.view;

/**
 * Dialog for editing namespace prefix to URI mappings
 */
public interface EditNamespacePrefixView {
	interface EditNamespacePrefixViewCallback {
		void onPrefixUpdated(String originalPrefix, String prefix, String uri);
		boolean isPrefixAvailable(String pfx);
	}
	
	void show(EditNamespacePrefixViewCallback callback, String prefix, String uri);
}
