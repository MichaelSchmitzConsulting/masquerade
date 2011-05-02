package masquerade.sim.model;

/**
 * Settings bean, contains Masquerade application setting
 * values.
 * <p>TODO: Add validation for sensible values in edit dialog, apply settings not only @ startup  
 */
public class Settings {
	private int statusLogEntryCountLimit = 100;
	private int requestLogCountLimit = 100;
	private int requestHistoryCleanupSleepPeriodMinutes = 5;
	
	public static Settings NO_SETTINGS = new Settings();
	static {
		NO_SETTINGS.statusLogEntryCountLimit = Integer.MIN_VALUE;
		NO_SETTINGS.requestLogCountLimit = Integer.MIN_VALUE;
		NO_SETTINGS.requestHistoryCleanupSleepPeriodMinutes = Integer.MIN_VALUE;
	}
	
	/**
	 * @return the requestLogCountLimit
	 */
	public int getRequestLogCountLimit() {
		return requestLogCountLimit;
	}
	/**
	 * @param requestLogCountLimit the requestLogCountLimit to set
	 */
	public void setRequestLogCountLimit(int requestLogCountLimit) {
		this.requestLogCountLimit = requestLogCountLimit;
	}
	/**
	 * @return the statusLogEntryCountLimit
	 */
	public int getStatusLogEntryCountLimit() {
		return statusLogEntryCountLimit;
	}
	/**
	 * @param statusLogEntryCountLimit the statusLogEntryCountLimit to set
	 */
	public void setStatusLogEntryCountLimit(int statusLogEntryCountLimit) {
		this.statusLogEntryCountLimit = statusLogEntryCountLimit;
	}
	/**
	 * @return the requestHistoryCleanupSleepPeriodMinutes
	 */
	public int getRequestHistoryCleanupSleepPeriodMinutes() {
		return requestHistoryCleanupSleepPeriodMinutes;
	}
	/**
	 * @param requestHistoryCleanupSleepPeriodMinutes the requestHistoryCleanupSleepPeriodMinutes to set
	 */
	public void setRequestHistoryCleanupSleepPeriodMinutes(int requestHistoryCleanupSleepPeriodMinutes) {
		this.requestHistoryCleanupSleepPeriodMinutes = requestHistoryCleanupSleepPeriodMinutes;
	}
	
	@Override
	public Settings clone() {
		Settings copy = new Settings();
		
		copy.requestHistoryCleanupSleepPeriodMinutes = this.requestHistoryCleanupSleepPeriodMinutes;
		copy.requestLogCountLimit = this.requestLogCountLimit;
		copy.statusLogEntryCountLimit = this.statusLogEntryCountLimit;
		
		return copy;
	}
	
	@Override
	public String toString() {
		return "Settings [requestLogCountLimit=" + requestLogCountLimit + ", statusLogEntryCountLimit=" + statusLogEntryCountLimit
				+ ", requestHistoryCleanupSleepPeriodMinutes=" + requestHistoryCleanupSleepPeriodMinutes + "]";
	}
}
