package com.kichang.util.fortify;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;

public class ZipUtil {
	static Logger logger = Logger.getLogger(ZipUtil.class);
	public static boolean isZip(File file) {
		if (!(file.exists())) {
			return false;
		}
		ZipFile zip = null;
		try {
			zip = new ZipFile(file);
			if (zip != null)
				FileUtil.close(zip);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public static ZipEntry getEntry(ZipFile zipFile, Pattern pattern) {
		Enumeration entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry) entries.nextElement();
			String entryName = zipEntry.getName();
			Matcher m = pattern.matcher(entryName);
			if (m.matches()) {
				return zipEntry;
			}
		}
		return null;
	}

	public static ZipEntry getEntryEndingWith(ZipFile zipFile, String path) {
		Enumeration entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry) entries.nextElement();
			String entryName = zipEntry.getName();
			while (entryName.endsWith("/")) {
				entryName = entryName.substring(0, entryName.length() - 1);
			}
			if (entryName.endsWith(path)) {
				return zipEntry;
			}
		}
		return null;
	}

	public static boolean isEar(File possibleEar) throws IOException {
		if (possibleEar.getName().toLowerCase().endsWith(".ear")) {
			return true;
		}
		if (possibleEar.isDirectory()) {
			File[] files = possibleEar.listFiles();
			for (int i = 0; i < files.length; ++i) {
				File file = files[i];
				String fileName = file.getName().toLowerCase();
				if (fileName.endsWith(".ear")) {
					return true;
				}
				if (isWar(file)) {
					return true;
				}

			}

		}

		return false;
	}

	public static boolean isWar(File possibleWar) throws IOException {
		if (possibleWar.getName().toLowerCase().endsWith(".war")) {
			return true;
		}
		if (possibleWar.isDirectory()) {
			File[] files = possibleWar.listFiles();
			for (int i = 0; i < files.length; ++i) {
				File file = files[i];
				if (file.getName().equalsIgnoreCase("web-inf")) {
					return true;
				}
			}
		}
		return false;
	}

	public static void explodeWebappInPlace(File webappFile) throws IOException {
		extractZipInPlace(webappFile);
		FileFilter filter = new FileFilter() {
			public boolean accept(File pathname) {
				return ((ZipUtil.isZip(pathname)) && (((pathname.getName()
						.toLowerCase().endsWith(".war")) || (pathname.getName()
						.toLowerCase().endsWith(".ear")))));
			}
		};
		File[] webappFiles = webappFile.listFiles(filter);
		for (int i = 0; i < webappFiles.length; ++i)
			explodeWebappInPlace(webappFiles[i]);
	}

	public static boolean extractZipInPlace(File zipFile) throws IOException {
		logger.info("Extracting zip in place: "
				+ zipFile.getAbsolutePath());
		if (isZip(zipFile)) {
			File tempWarRoot = FileUtil.makeTempDirectory("fortify", null);
			extractZip(zipFile, tempWarRoot.getAbsolutePath());

			zipFile.delete();
			zipFile.mkdir();
			FileUtil.copyDirectoryStructure(tempWarRoot, zipFile);
			FileUtil.deleteDirectoryStructure(tempWarRoot);
			return true;
		}
		return false;
	}

	public static void extractZip(File file, String toDir)
			throws IOException {
		logger.info("extracting zip..." + file.getAbsolutePath());
		ZipInputStream zipInput = new ZipInputStream(new FileInputStream(file));
		try {
			extractZip(zipInput, toDir);
			logger.info("done extracting");
		} finally {
			if (FileUtil.close(zipInput) != null)
				logger.info("extractZip: Error closing input stream\n");
		}
	}

	public static void extractZip(ZipInputStream zipDataInput, String toDir)
			throws IOException {
		logger.info("in extracting");

		ZipEntry dataEntry;
		while ((dataEntry = zipDataInput.getNextEntry()) != null) {
			
			String name = dataEntry.getName();
			if (name.startsWith(File.separator)) {
				name = name.substring(1);
			}
			extractEntry(zipDataInput, toDir + File.separator + name,
					dataEntry.isDirectory());
		}
	}

	private static void extractEntry(ZipInputStream zip, String destFileName,
			boolean isDir) throws IOException {
		BufferedOutputStream out = null;
		try {
			File destFile = new File(destFileName);
			logger.info("Writing file: " + destFile.getCanonicalPath());
			if (isDir) {
				FileUtil.mkdirs(destFile);
			} else {
				FileUtil.mkdirs(destFile.getParentFile());
				out = new BufferedOutputStream(new FileOutputStream(destFile));

				byte[] buf = new byte[8192];
				int numRead;
				while ((numRead = zip.read(buf, 0, buf.length)) != -1) {
					
					out.write(buf, 0, numRead);
				}
				out.close();
			}
		} finally {
			if (FileUtil.closeAgain(out) != null)
				logger.info("extractEntry: Error closing output stream\n");
		}
	}
}
