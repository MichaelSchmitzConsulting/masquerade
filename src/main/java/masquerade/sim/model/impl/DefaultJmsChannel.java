package masquerade.sim.model.impl;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

/** 
 * A {@link masquerade.sim.model.Channel} receiving messages from an MQ
 * provider that can be initialized with URL, user and password. 
 */
public class DefaultJmsChannel extends JmsChannel {

	private String url = "";

	public DefaultJmsChannel(String name) {
		super(name);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return <code>true</code> If this channel is configured with an URL and a destination
	 */
	@Override
	public boolean isActive() {
		return isNotEmpty(url) && isNotEmpty(getDestinationName());
	}

	@Override
	public String toString() {
		return "Default JMS Channel " + getUrl();
	}
}
