package masquerade.sim.db;

import java.io.File;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;

public class DatabaseLifecycle {

	private ObjectContainer db;
	
	public ObjectContainer getDb() {
		return db;
	}

	public ObjectContainer start(File dbFile) {
		stop();
		db = startDatabase(dbFile);
		return db;
	}
	
	public void stop() {
		if (this.db != null) {
			ObjectContainer db = this.db;
			this.db = null;
			stopDatabase(db);
		}
	}

	private static ObjectContainer startDatabase(File dbFile) {
		EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
		configuration.common().activationDepth(Integer.MAX_VALUE);
		configuration.common().updateDepth(Integer.MAX_VALUE);
		
		ObjectContainer container = Db4oEmbedded.openFile(configuration, dbFile.getAbsolutePath());
		return container;
	}
	
	private void stopDatabase(ObjectContainer db) {
		db.close();
    }
}
