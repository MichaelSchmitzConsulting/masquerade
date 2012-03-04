package masquerade.sim.model.listener;

import masquerade.sim.model.Settings;

/**
 * Listener for an update of the app's {@link Settings}
 */
public interface SettingsChangeListener {
	void settingsChanged(Settings oldSettings, Settings newSettings);
}
