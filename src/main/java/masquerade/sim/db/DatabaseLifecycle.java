package masquerade.sim.db;

import java.io.File;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;

/**
 * Controls database lifecycle by creating and closing
 * {@link ObjectContainer object containers}.
 * 
 * <p>Creates a new databse if it doesn't exist yet.
 */
public class DatabaseLifecycle {

	private ObjectContainer db;
	
	public ObjectContainer getDb() {
		return db;
	}

	/**
	 * Create {@link ObjectContainer}, open/create DB file
	 * @param dbFile
	 * @return
	 */
	public ObjectContainer start(File dbFile) {
		stop();
		db = startDatabase(dbFile);
		return db;
	}
	
	/**
	 * Stop {@link ObjectContainer}
	 */
	public void stop() {
		if (this.db != null) {
			ObjectContainer db = this.db;
			this.db = null;
			stopDatabase(db);
		}
	}

	/**
	 * Configure and create the DB
	 * @param dbFile
	 * @return Root {@link ObjectContainer}, use ext().startSession() to create a new session 
	 */
	private static ObjectContainer startDatabase(File dbFile) {
		EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
		configuration.common().activationDepth(Integer.MAX_VALUE);
		configuration.common().updateDepth(Integer.MAX_VALUE);
		
		ObjectContainer container = Db4oEmbedded.openFile(configuration, dbFile.getAbsolutePath());
		return container;
	}

	/**
	 * Closes an {@link ObjectContainer}
	 * @param db
	 */
	private void stopDatabase(ObjectContainer db) {
		db.close();
    }
}
