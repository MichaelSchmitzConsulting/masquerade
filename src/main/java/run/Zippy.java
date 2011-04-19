package run;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class Zippy {
	private static final int BUFFER = 4096;
	
	public static void unzip(String zipFile, File destDir) throws ZipException, IOException {
		ZipFile zip = new ZipFile(zipFile);
		
		Enumeration<? extends ZipEntry> zipFileEntries = zip.entries();
		
		// Process each entry
		while (zipFileEntries.hasMoreElements()) {
			// grab a zip file entry
			ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
			
			File destFile = new File(destDir, entry.getName());
			File destinationParent = destFile.getParentFile();
			
			// create the parent directory structure if needed
			destinationParent.mkdirs();
			if (!entry.isDirectory()) {
				System.out.println("Unpacking " + entry.getName() + " to " + destFile.getAbsolutePath() + "...");
				extractFile(zip, entry, destFile);
			}
		}
		System.out.println("Done");
	}
	
	private static void extractFile(ZipFile zip, ZipEntry entry, File destFile) throws IOException, FileNotFoundException {
		BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
		int currentByte;
		// establish buffer for writing file
		byte data[] = new byte[BUFFER];

		// write the current file to disk
		FileOutputStream fos = new FileOutputStream(destFile);
		BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

		// read and write until last byte is encountered
		while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
			dest.write(data, 0, currentByte);
		}
		dest.flush();
		dest.close();
		is.close();
	}
}
