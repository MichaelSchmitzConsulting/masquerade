package masquerade.sim.ui.field;

import java.util.Collection;

import masquerade.sim.db.ModelRepository;

public class ModelSelectFieldFactory extends CollectionSelectFieldFactory {
	private ModelRepository modelRepository;
	private Class<?> type;
	
    public ModelSelectFieldFactory(ModelRepository modelRepository, Class<?> type, String defaultWidth) {
		this(modelRepository, type, defaultWidth, true);
    }
    
    public ModelSelectFieldFactory(ModelRepository modelRepository, Class<?> type, String defaultWidth, boolean isRequired) {
		super(type, defaultWidth, isRequired);
		this.type = type;
	    this.modelRepository = modelRepository;
    }
    
    @Override
    protected Collection<?> getAll() {
		return modelRepository.getAll(type);
    }
}
