package masquerade.sim.model.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

import masquerade.sim.model.FileLoader;
import masquerade.sim.model.FileType;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFileFilter;

/**
 * File loader loading files from a directory, with subdirectories named
 * {@link FileType}.toLowerCase().
 */
public class FileLoaderImpl implements FileLoader {

	private File dir;
	
	/**
	 * File loader loading files from a directory, with
	 * subdirectories named {@link FileType}.toLowerCase().
	 * @param dir Root directory
	 */
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

	/**
	 * Loads a file (e.g. a template) of the given name
	 * @param type
	 * @param name
	 * @return {@link InputStream} for the file, or <code>null</code> if not found
	 */
	@Override
	public InputStream load(FileType type, String name)  {
		name = name.replace("..", "");
		
		File subdir = subdirForType(type);
		File file = new File(subdir, name);
		if (!file.exists() || !file.canRead()) {
			return null;
		} else {
			return load(file);
		}
	}

	/**
	 * Returns all available filenames for the given {@link FileType} (e.g. templates)
	 * @param type Type of files to list
	 */
	@Override
	public Collection<String> listTemplates(FileType type) {
		File subdir = subdirForType(type);
		String[] files = subdir.list(FileFileFilter.FILE);
		return Arrays.asList(files);
	}

	private File subdirForType(FileType type) {
		File subdir = new File(dir, dirName(type));
		return subdir;
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
