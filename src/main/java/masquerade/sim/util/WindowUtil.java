package masquerade.sim.util;

import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

public class WindowUtil {
	public static Window getRoot(Window window) {
		Window root = null;
		while (window != null) {
			root = window;
			window = window.getParent();
		}
		return root;
	}
	
	public static void showErrorNotification(Window parent, String caption, String message) {
		WindowUtil.getRoot(parent).showNotification(
            caption,
            "<br/>" + message,
            Notification.TYPE_ERROR_MESSAGE);
	}
}
