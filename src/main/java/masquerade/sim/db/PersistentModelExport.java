package masquerade.sim.db;

import java.io.File;

import com.db4o.ObjectContainer;

public class PersistentModelExport implements ModelExport {

	private ObjectContainer container;

	public PersistentModelExport(ObjectContainer container) {
		this.container = container;
	}
	
	@Override
	public void exportModel(File file) {
		container.ext().backup(file.getAbsolutePath());
	}
}
