package masquerade.sim.ui.field;

import java.util.Collection;

import com.vaadin.ui.Field;

import masquerade.sim.model.FileLoader;
import masquerade.sim.model.FileType;

public class FileSelectFieldFactory extends CollectionSelectFieldFactory {
	private FileLoader fileLoader;
	private FileType type;
	private String caption;
	
    public FileSelectFieldFactory(String caption, FileLoader fileLoader, FileType type, String defaultWidth) {
		this(caption, fileLoader, type, defaultWidth, true);
    }
    
    public FileSelectFieldFactory(String caption, FileLoader fileLoader, FileType type, String defaultWidth, boolean isRequired) {
		super(String.class, defaultWidth, isRequired);
		this.type = type;
	    this.fileLoader = fileLoader;
	    this.caption = caption;
    }
    
    @Override
    
	public Field createField(Object existingValue) {
		Field field = super.createField(existingValue);
		field.setCaption(caption);
		return field;
	}

	@Override
    protected Collection<?> getAll() {
		return fileLoader.listTemplates(type);
    }
}
