package masquerade.sim.model.ui;

import java.lang.reflect.Constructor;

import masquerade.sim.util.WindowUtil;

import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

/**
 * Factory for model objects, showing an error if an object cannot be created
 */
public class ModelObjectFactory {
	
	public static Object createModelObject(Window window, Class<?> type, String name) {
		String typeName = type.getSimpleName();
		try {
			// By convention, a constructor with a single String argument is the constructor that accepts an object name. 
			Constructor<?> constructor;
			try {
				constructor = type.getConstructor(String.class);
			} catch (NoSuchMethodException ex) {
				notifyError(window,  
					"No possible constructor found to create a " + typeName + ", expected " + typeName + "(String name) or " + typeName + "()",
					typeName, ex);
				return null;
			}
			Object value = constructor.newInstance(name);
			return value;
		} catch (Exception e) {
			String exMessage = e.getMessage();
			String errMsg = e.getClass().getName() + " " + (exMessage == null ? "" : exMessage);
			notifyError(window, errMsg, typeName, e);
			return null;
		}
	}
	
	private static void notifyError(Window window, String msg, String typeName, Exception e) {
		String caption = "Unable to create " + typeName;
		
		WindowUtil.getRoot(window).showNotification(
             caption,
             "<br/>" + msg,
             Notification.TYPE_ERROR_MESSAGE);
	}
}
