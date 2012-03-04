package masquerade.sim.util;

import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

public class WindowUtil {
	private static final int MAX_STACKTRACE_LEN = 400;

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

	public static void showErrorNotification(Window parent, String caption, Throwable exception) {
		String message = StringUtil.strackTrace(exception);
		
		if (message.length() > MAX_STACKTRACE_LEN) {
			message = message.substring(0, MAX_STACKTRACE_LEN);
		}
		
		WindowUtil.getRoot(parent).showNotification(
            caption,
            "<br/>" + message,
            Notification.TYPE_ERROR_MESSAGE);
	}
}
