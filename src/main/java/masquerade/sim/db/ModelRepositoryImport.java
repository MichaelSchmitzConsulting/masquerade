package masquerade.sim.db;

import java.io.File;
import java.util.Collection;

import masquerade.sim.model.Channel;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

/**
 * {@link ModelImport} implementation importing simulation
 * configuration into the {@link ModelRepository}.
 */
public class ModelRepositoryImport implements ModelImport {

	private StatusLog log = StatusLogger.get(ModelRepositoryImport.class);
	
	private ModelRepository modelRepository;

	public ModelRepositoryImport(ModelRepository modelRepository) {
		this.modelRepository = modelRepository;
	}

	@Override
	public void importModel(File file, boolean isReplaceExisting) {
		DatabaseLifecycle lifecycle = new DatabaseLifecycle();
		
		try {
			// Load uploaded configuration
			ObjectContainer db = lifecycle.start(file);
			ObjectSet<Object> allObjs = db.query(Object.class);
			ObjectSet<Channel> channels = db.query(Channel.class);
			
			// Successfully loaded - now import all objects in the new
			// configuration into the model repository.
			importObjects(allObjs, channels, isReplaceExisting);
		} finally {
			lifecycle.stop();
		}
	}
	
	private void importObjects(Collection<Object> resultSet, Collection<Channel> newChannels, boolean isReplaceExisting) {
		// Should the existing configuration be replaced?
		if (isReplaceExisting) {
			modelRepository.clear();
		} else {
			// No replacement - check for conflicting channel names, remove channels with conflicting
			// names first.
			for (Channel newChannel : newChannels) {
				String name = newChannel.getName();
				Channel existingChannel = modelRepository.getChannelByName(name);
				if (existingChannel != null) {
					log.warning("Replacing existing channel " + name + " with imported channel of the same name");
					modelRepository.notifyDelete(existingChannel);
				}
			}
		}
		
		for (Object obj : resultSet) {
			modelRepository.notifyCreate(obj);
		}
	}
}
