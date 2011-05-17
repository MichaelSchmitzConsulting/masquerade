package masquerade.sim;

import masquerade.sim.db.RequestHistoryCleanupJob;
import masquerade.sim.model.Settings;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

/**
 * Implementation of {@link SettingsChangeListener} listening
 * for settings changes, and applying them application-wide
 */
public class AppSettingsChangeListener implements SettingsChangeListener {

	private static final StatusLog log = StatusLogger.get(AppSettingsChangeListener.class);
	private static final long MINUTE = 60 * 1000;
	
	private RequestHistoryCleanupJob requestHistoryCleanupJob;
	private ConfigurationVariableHolder configVariableHolder;
	
	/**
	 * @param requestHistoryCleanupJob
	 * @param configVariableHolder 
	 */
	public AppSettingsChangeListener(RequestHistoryCleanupJob requestHistoryCleanupJob, ConfigurationVariableHolder configVariableHolder) {
		this.requestHistoryCleanupJob = requestHistoryCleanupJob;
		this.configVariableHolder = configVariableHolder;
	}

	@Override
	public void settingsChanged(Settings oldSettings, Settings newSettings) {
		requestHistoryCleanupSettings(oldSettings, newSettings);
		statusLogSettings(oldSettings, newSettings);
		configurationPropertiesSettings(oldSettings, newSettings);
	}

	private void statusLogSettings(Settings oldSettings, Settings newSettings) {
		// Request history size
		int newLimit = newSettings.getStatusLogEntryCountLimit();
		if (oldSettings.getStatusLogEntryCountLimit() != newLimit  ) {
			StatusLogger.REPOSITORY.setHistorySize(newLimit);
			log.trace("Status log history size set to " + newLimit + " entries");
		}
	}

	private void requestHistoryCleanupSettings(Settings oldSettings, Settings newSettings) {
		// Sleep period
		long sleepPeriod = newSettings.getRequestHistoryCleanupSleepPeriodMinutes();
		if (oldSettings.getRequestHistoryCleanupSleepPeriodMinutes() != sleepPeriod) {
			requestHistoryCleanupJob.setSleepPeriodMs(sleepPeriod * MINUTE);
			log.trace("Request history cleanup period set to " + sleepPeriod + " minutes");
		}
		
		// Request log size
		int requestsToKeep = newSettings.getRequestLogCountLimit();
		if (oldSettings.getRequestLogCountLimit() != requestsToKeep) {
			requestHistoryCleanupJob.setRequestsToKeep(requestsToKeep);
			log.trace("Request history size set to " + requestsToKeep);
		}
	}

	private void configurationPropertiesSettings(Settings oldSettings, Settings newSettings) {
		if (isConfigurationPropertiesChanged(oldSettings, newSettings)) {
			configVariableHolder.consumeConfigurationVariables(newSettings.getConfigurationProperties());
		}
	}

	private boolean isConfigurationPropertiesChanged(Settings oldSettings, Settings newSettings) {
		String oldValue = oldSettings.getConfigurationProperties();
		String newValue = newSettings.getConfigurationProperties();
		// Settings value might initally be null
		if (oldValue == null && newValue == null) {
			return false;
		} else if (oldValue == null && newValue != null) {
			return true;
		} else {
			return !oldValue.equals(newValue);
		}
	}
}
