package masquerade.sim.ui.field;

import java.util.Collection;

import masquerade.sim.db.ModelRepository;

public class ModelSelectFieldFactory extends CollectionSelectFieldFactory {
	private ModelRepository modelRepository;
	private Class<?> type;
	
    public ModelSelectFieldFactory(ModelRepository modelRepository, Class<?> type, String defaultWidth) {
		super(type, defaultWidth);
		this.type = type;
	    this.modelRepository = modelRepository;
    }

    @Override
    protected Collection<?> getAll() {
		return modelRepository.getAll(type);
    }
}
