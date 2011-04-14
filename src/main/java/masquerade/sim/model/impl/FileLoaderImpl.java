package masquerade.sim.model.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import masquerade.sim.model.FileLoader;
import masquerade.sim.model.FileType;

import org.apache.commons.io.FileUtils;

public class FileLoaderImpl implements FileLoader {

	private File dir;
	
	public FileLoaderImpl(File dir) {
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException(dir.getAbsolutePath() + " is no a directory");
		}
		this.dir = dir;
		
		for (FileType type : FileType.values()) {
			String dirName = dirName(type);
			File subdir = new File(dir, dirName);
			try {
				FileUtils.forceMkdir(subdir);
			} catch (IOException e) {
				throw new IllegalArgumentException("Cannot create directory " + subdir.getAbsolutePath());
			}
		}
	}

	@Override
	public InputStream load(FileType type, String name)  {
		name = name.replace("..", "");
		
		File subdir = new File(dir, dirName(type));
		File file = new File(subdir, name);
		if (!file.exists() || !file.canRead()) {
			return null;
		} else {
			return load(file);
		}
	}

	private InputStream load(File file) {
		try {
			return new FileInputStream(file);
		} catch (IOException e) {
			throw new IllegalArgumentException("File " + file.getAbsolutePath() + " exists, but cannot open a stream on it");
		}
	}
	
	private String dirName(FileType type) {
		return type.name().toLowerCase();
	}
}
