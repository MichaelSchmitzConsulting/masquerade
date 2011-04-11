package masquerade.sim.util;

import com.vaadin.ui.Window;

public class WindowUtil {
	public static Window getRoot(Window window) {
		Window root = null;
		while (window != null) {
			root = window;
			window = window.getParent();
		}
		return root;
	}
}
