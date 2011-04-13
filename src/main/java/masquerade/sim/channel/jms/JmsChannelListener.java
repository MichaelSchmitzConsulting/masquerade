package masquerade.sim.channel.jms;

import java.util.logging.Logger;

import masquerade.sim.model.ChannelListener;
import masquerade.sim.model.SimulationRunner;
import masquerade.sim.model.impl.JmsChannel;

public class JmsChannelListener implements ChannelListener<JmsChannel> {

	private final static Logger log = Logger.getLogger(JmsChannelListener.class.getName());

	private String url;
	private String user;
	//private String password;
	
	@Override
	public void start(JmsChannel channel, SimulationRunner runner) {
		url = channel.getUrl();
		user = channel.getUser();
		//password = channel.getPassword();
		log.info("Starting JmsChannelListener at " + url + " with user " + user);
	}

	@Override
	public void stop() {
		log.info("Stopping JmsChannelListener");
	}
}
