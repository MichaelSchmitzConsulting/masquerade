package masquerade.sim.db;

import masquerade.sim.channel.ChannelListenerRegistry;
import masquerade.sim.model.Channel;

import com.db4o.events.CommitEventArgs;
import com.db4o.events.Event4;
import com.db4o.events.EventListener4;
import com.db4o.foundation.Iterator4;
import com.db4o.internal.FrozenObjectInfo;
import com.db4o.internal.LazyObjectReference;

public class ChannelChangeTrigger implements EventListener4<CommitEventArgs> {
	
	private ChannelListenerRegistry channelListenerRegistry;
	
	public ChannelChangeTrigger(ChannelListenerRegistry channelListenerRegistry) {
		this.channelListenerRegistry = channelListenerRegistry;
	}
	
	@Override
	@SuppressWarnings("unchecked") // event registry is not generic, Iterator4 is
	public void onEvent(Event4<CommitEventArgs> event, CommitEventArgs eventArgs) {
		for (Iterator4<Object> it = eventArgs.added().iterator(); it.moveNext();) {
			LazyObjectReference reference = (LazyObjectReference) it.current();
			Object obj = reference.getObject();
			update(obj);
		}
		
		for (Iterator4<Object> it = eventArgs.updated().iterator(); it.moveNext();) {
			LazyObjectReference reference = (LazyObjectReference) it.current();
			Object obj = reference.getObject();
			update(obj);
		}
		
		for (Iterator4<Object> it = eventArgs.deleted().iterator(); it.moveNext();) {
			FrozenObjectInfo deletedInfo = (FrozenObjectInfo) it.current();
			Object obj = deletedInfo.getObject();
			delete(obj);
		}
	}
	
	private void delete(Object obj) {
		if (obj != null && obj instanceof Channel) {
			Channel channel = (Channel) obj;
			channelListenerRegistry.notifyChannelDeleted(channel.getName());
		}
	}

	private void update(Object obj) {
		if (obj != null && obj instanceof Channel) {
			Channel channel = (Channel) obj;
			channelListenerRegistry.notifyChannelChanged(channel);
		}
	}
}