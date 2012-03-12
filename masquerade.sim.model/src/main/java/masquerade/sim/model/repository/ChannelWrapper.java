package masquerade.sim.model.repository;

import masquerade.sim.model.Channel;

/**
 * Wraps a channel entry in the {@link ModelRepository}, providing an {{@link #isPersistent()} property
 */
public interface ChannelWrapper {
	Channel getChannel();
	boolean isPersistent();
}
