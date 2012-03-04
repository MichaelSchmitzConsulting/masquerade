package masquerade.sim.model.settings;

import masquerade.sim.model.Settings;
import masquerade.sim.model.repository.ModelRepository;

/**
 * Provides access to the application-wide {@link Settings} to avoid dependencies
 * from clients directly to the {@link ModelRepository}. 
 */
public class ModelSettingsProvider implements SettingsProvider {

	private final ModelRepository modelRepository;
	
	public ModelSettingsProvider(ModelRepository modelRepository) {
		this.modelRepository = modelRepository;
	}

	/* (non-Javadoc)
	 * @see masquerade.sim.model.settings.SettingsProvider#getSettings()
	 */
	@Override
	public Settings getSettings() {
		return modelRepository.getSettings();
	}

	/* (non-Javadoc)
	 * @see masquerade.sim.model.settings.SettingsProvider#notifyChanged(masquerade.sim.model.Settings)
	 */
	@Override
	public void notifyChanged(Settings settings) {
		modelRepository.updateSettings(settings);
	}

}
