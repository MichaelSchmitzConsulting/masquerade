package masquerade.sim.model.settings;

import masquerade.sim.model.Settings;

/**
 * Provides access to {@link Settings}
 */
public interface SettingsProvider {

	/**
	 * @return Current {@link Settings}
	 */
	Settings getSettings();

	/**
	 * Call to persist changed settings
	 * @param settings
	 */
	void notifyChanged(Settings settings);

}
