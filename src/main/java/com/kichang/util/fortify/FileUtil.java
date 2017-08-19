/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package com.kichang.util.fortify;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

public final class FileUtil {
	static Logger logger = Logger.getLogger(FileUtil.class);
	public static final List<String> WIN32_EXE_ORDER = Arrays
			.asList(new String[] { "com", "exe", "bat", "cmd" });
	public static final List<String> CLASS_EXT_LIST = Arrays
			.asList(new String[] { "class" });

	private static final Map caseInsensitiveFileCache = new HashMap();

	public static final Pattern filenameSafetyPattern = Pattern
			.compile("[^a-zA-Z0-9_\\-.]");

	private static final Pattern FwdSlashSeq = Pattern.compile("//+");

	public static String getExtension(File f) {
		return getExtension(f.getName());
	}

	public static String getExtension(String s) {
		String ext = null;
		int i = s.lastIndexOf(46);

		if ((i > 0) && (i < s.length() - 1)) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	public static String getDirectory(String fullPath) {
		if (fullPath == null) {
			return null;
		}
		return new File(fullPath).getParent();
	}

	public static String changeExtension(String file, String extension) {
		int dotLocation = file.lastIndexOf(".");
		return file.substring(0, dotLocation) + extension;
	}

	public static String loadFileAsString(String filename)
			throws FileNotFoundException, IOException {
		return loadFileAsString(new FileInputStream(filename));
	}

	public static String loadFileAsString(File file)
			throws FileNotFoundException, IOException {
		return loadFileAsString(new FileInputStream(file));
	}

	public static String loadFileAsString(InputStream input) throws IOException {
		return loadFileAsString(new InputStreamReader(input, "UTF-8"));
	}

	public static String loadFileAsString(Reader input) throws IOException {
		return loadFileAsStringBuffer(input).toString();
	}

	
	public static StringBuffer loadFileAsStringBuffer(Reader input)
			throws IOException {
		StringBuffer data = new StringBuffer();
		BufferedReader reader = new BufferedReader(input);
		try {
			int chr;
			while ((chr = reader.read()) != -1) {
				data.append((char) chr);
			}
		} finally {
			reader.close();
		}
		return data;
	}

	public static boolean deleteDirectoryStructure(File directory) {
		if (directory.isDirectory()) {
			return deleteDirStructHelper(directory, new HashSet(), null);
		}
		return false;
	}

	public static boolean deleteDirectoryStructure(File directory,
			Pattern pattern) {
		if (directory.isDirectory()) {
			return deleteDirStructHelper(directory, new HashSet(), pattern);
		}
		return false;
	}

	private static boolean deleteDirStructHelper(File dir,
			HashSet<String> dirList, Pattern pattern) {
		try {
			String dirName = dir.getCanonicalPath();
			if (dirList.contains(dirName)) {
				return false;
			}
			dirList.add(dirName);
		} catch (Exception e) {
		}

		File[] files = dir.listFiles();

		if (files == null) {
			return false;
		}

		for (int i = 0; i < files.length; ++i) {
			if (files[i].isDirectory())
				deleteDirStructHelper(files[i], dirList, pattern);
			else if ((pattern == null)
					|| (pattern.matcher(files[i].getName()).matches())) {
				files[i].delete();
			}

		}

		if (dir.listFiles().length == 0) {
			return dir.delete();
		}
		return false;
	}

	public static String makeFilenameSafeString(String input) {
		Matcher m = filenameSafetyPattern.matcher(input);
		StringBuffer result = new StringBuffer();
		if (input.startsWith(".")) {
			result.append("_");
		}
		while (m.find()) {
			m.appendReplacement(result, "_");
		}
		m.appendTail(result);
		return result.toString();
	}

	public static File getCommonRoot(Collection<File> files) {
		if (files.size() == 0) {
			return null;
		}
		Iterator fileIterator = files.iterator();
		File root = ((File) fileIterator.next()).getAbsoluteFile()
				.getParentFile();

		if (fileIterator.hasNext()) {
			File sourceFile = ((File) fileIterator.next()).getAbsoluteFile();
			while (root != null) {
				File sourceDir = sourceFile.getParentFile();
				while (sourceDir != null) {
					if (!(sourceDir.equals(root)))
						;
					sourceDir = sourceDir.getParentFile();
				}

				root = root.getParentFile();
			}
		}

		return root;
	}

	public static boolean isParent(File parent, File child) {
		File currentFile = child;
		while (currentFile != null) {
			if (currentFile.equals(parent)) {
				return true;
			}
			currentFile = currentFile.getParentFile();
		}

		return false;
	}

	public static File getFirstExistingDirectory(File file) {
		if (file == null)
			return null;
		if (isExistingDirectory(file)) {
			return file;
		}
		return getFirstExistingDirectory(file.getParentFile());
	}

	public static String getRelativePath(File base, File fullPath) {
		LinkedList pathElts = new LinkedList();
		pathElts.add(fullPath.getName());

		File dir = fullPath.getAbsoluteFile().getParentFile();
		while ((dir != null) && (!(dir.equals(base)))) {
			if (dir.getParentFile() != null) {
				pathElts.addFirst(dir.getName());
			} else {
				pathElts.addFirst(dir.getAbsolutePath());
			}
			dir = dir.getParentFile();
		}

		String result = new File(StringUtil.join(
				(String[]) pathElts.toArray(new String[pathElts.size()]), "/"))
				.getPath();
		if (SystemUtil.isWindows()) {
			result = result.replace('\\', '/');
		}
		return result;
	}

	public static int canReadFile(File f) {
		if (!(f.exists())) {
			return 224;
		}
		if (f.isDirectory()) {
			return 225;
		}
		if (!(f.isFile())) {
			return 226;
		}
		if (!(f.canRead())) {
			return 227;
		}
		return 0;
	}

	public static String truncateFilename(String filename, int length) {
		if (filename == null) {
			return null;
		}
		int lengthdiff = filename.length() - length;
		if (lengthdiff > 0) {
			int index = filename.indexOf(47, lengthdiff + 3);
			if (index < 0) {
				index = filename.indexOf(92, lengthdiff + 3);
			}

			if (index < 0) {
				index = lengthdiff + 3;
			}
			return "..." + filename.substring(index);
		}
		return filename;
	}

	public static String path(String a) {
		return a;
	}

	public static String path(String a, String b) {
		return a + File.separatorChar + b;
	}

	public static String path(String a, String b, String c) {
		return a + File.separatorChar + b + File.separatorChar + c;
	}

	public static String path(String a, String b, String c, String d) {
		return a + File.separatorChar + b + File.separatorChar + c
				+ File.separatorChar + d;
	}

	public static String path(String a, String b, String c, String d, String e) {
		return a + File.separatorChar + b + File.separatorChar + c
				+ File.separatorChar + d + File.separatorChar + e;
	}

	public static boolean isWindowsExecutable(File target) {
		return WIN32_EXE_ORDER.contains(getExtension(target));
	}

	public static void copyZipArchive(ZipFile source, ZipOutputStream dest,
			Pattern includedEntries, Pattern excludedEntries)
			throws IOException {
		Enumeration entries = source.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();

			String entryName = entry.getName();
			if ((excludedEntries != null)
					&& (excludedEntries.matcher(entryName).find())) {
				continue;
			}
			if ((includedEntries == null)
					|| (includedEntries.matcher(entryName).find())) {
				InputStream sourceStream = source.getInputStream(entry);

				copyZipEntry(entryName, dest, sourceStream);
			}
		}
	}

	private static void copyZipEntry(String entryName, ZipOutputStream dest,
			InputStream source) throws IOException {
		dest.putNextEntry(new ZipEntry(entryName));
		copy(source, dest, false);
		source.close();
		dest.closeEntry();
	}

	public static void mergeArchives(File source1, File source2, File out)
			throws IOException {
		File temp = null;
		ZipOutputStream destStream = null;
		ZipFile sourceZip = null;
		ZipFile tempZip = null;
		try {
			temp = File.createTempFile("place", null);
			temp.deleteOnExit();

			copy(source1, temp);

			destStream = new ZipOutputStream(new FileOutputStream(out));
			tempZip = new ZipFile(temp);
			copyZipArchive(tempZip, destStream, null, null);

			sourceZip = new ZipFile(source2);
			copyZipArchive(sourceZip, destStream, null, null);

			destStream.flush();
			destStream.close();
		} finally {
			if (temp != null) {
				temp.delete();
			}
			closeAgain(destStream);
			close(sourceZip);
			close(tempZip);
		}
	}

	public static String normalizeDirectorySeperators(String path) {
		if (path == null) {
			return null;
		}

		String norm = path.replace('\\', '/');
		if (norm.contains("//")) {
			norm = FwdSlashSeq.matcher(norm).replaceAll("/");
		}
		return norm;
	}

	public static IOException close(InputStream in) {
		if (in == null) {
			return null;
		}
		try {
			in.close();
		} catch (IOException ex) {
			return ex;
		}
		return null;
	}

	public static IOException close(Reader in) {
		if (in == null) {
			return null;
		}
		try {
			in.close();
		} catch (IOException ex) {
			return ex;
		}
		return null;
	}

	public static IOException closeAgain(OutputStream out) {
		if (out == null) {
			return null;
		}
		try {
			out.close();
		} catch (IOException ex) {
			return ex;
		}
		return null;
	}

	public static IOException closeAgain(InputStream out) {
		if (out == null) {
			return null;
		}
		try {
			out.close();
		} catch (IOException ex) {
			return ex;
		}
		return null;
	}

	public static IOException close(Writer writer) {
		if (writer == null) {
			return null;
		}
		try {
			writer.close();
		} catch (IOException ex) {
			return ex;
		}
		return null;
	}

	public static IOException closeAgain(Writer out) {
		if (out == null) {
			return null;
		}
		try {
			out.close();
		} catch (IOException ex) {
			return ex;
		}
		return null;
	}

	public static IOException closeAgain(ZipFile out) {
		if (out == null) {
			return null;
		}
		try {
			out.close();
		} catch (IOException ex) {
			return ex;
		}
		return null;
	}

	public static IOException closeAgain(FileChannel out) {
		if (out == null) {
			return null;
		}
		try {
			out.close();
		} catch (IOException ex) {
			return ex;
		}
		return null;
	}

	@Deprecated
	public static IOException close(OutputStream out) {
		return closeAgain(out);
	}

	public static void copyAuditInfo(ZipFile zipFile,
			ZipOutputStream zipOutputStream) throws IOException {
		copyAuditInfo(zipFile, zipOutputStream, new String[] { "audit.xml",
				"VERSION", "audit.properties" });
	}

	public static void copyAuditInfo(ZipFile zipFile,
			ZipOutputStream zipOutputStream, String[] excludeFiles)
			throws IOException {
		List excludeFilesList = new LinkedList();
		if (excludeFiles != null) {
			for (int i = 0; i < excludeFiles.length; ++i) {
				excludeFilesList.add(excludeFiles[i].toUpperCase());
			}
		}
		Enumeration entries = zipFile.entries();

		while (entries.hasMoreElements()) {
			ZipEntry temp = (ZipEntry) entries.nextElement();
			String entryName = temp.getName();
			if (!(excludeFilesList.contains(entryName.toUpperCase()))) {
				copyZipEntry(zipFile, zipOutputStream, temp, entryName);
			}

		}

		zipOutputStream.flush();
	}

	public static void copy(InputStream src, OutputStream dest)
			throws IOException {
		copy(src, dest, true);
	}

	public static void copy(InputStream src, OutputStream dest,
			boolean closeStreams) throws IOException {
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try {
			in = new BufferedInputStream(src);
			out = new BufferedOutputStream(dest);

			byte[] buf = new byte[8192];
			int numRead;
			while ((numRead = in.read(buf, 0, buf.length)) != -1) {
				
				out.write(buf, 0, numRead);
			}
		} finally {
			if ((in != null) && (closeStreams)) {
				in.close();
			}
			if (out != null) {
				out.flush();
				if (closeStreams)
					out.close();
			}
		}
	}

	public static void copyDirectoryStructure(File srcDirectory,
			File dstDirectory) throws IOException {
		if (!(srcDirectory.isDirectory()))
			throw new IllegalArgumentException(srcDirectory.getAbsolutePath()
					+ " is not a directory");
		if ((dstDirectory.exists()) && (!(dstDirectory.isDirectory()))) {
			throw new IllegalArgumentException(dstDirectory.getAbsolutePath()
					+ " is not a directory");
		}

		mkdirs(dstDirectory);
		File[] fromFiles = srcDirectory.listFiles();
		for (int i = 0; i < fromFiles.length; ++i) {
			File src = fromFiles[i];
			File dst = new File(dstDirectory, src.getName());
			if (src.isDirectory())
				copyDirectoryStructure(src, dst);
			else if (src.isFile())
				copy(src, dst);
		}
	}

	public static void makeReadOnly(File fileOrDir) {
		if (!(fileOrDir.exists())) {
			throw new IllegalArgumentException(fileOrDir.getAbsolutePath()
					+ " does not exist");
		}
		if (fileOrDir.isDirectory()) {
			File[] contents = fileOrDir.listFiles();
			for (int i = 0; i < contents.length; ++i)
				try {
					makeReadOnly(contents[i]);
				} catch (Exception e) {
				}
		} else {
			fileOrDir.setReadOnly();
		}
	}

	public static File makeTempDirectory(String prefix, String suffix)
			throws IOException {
		return makeTempDirectory(prefix, suffix, null);
	}

	public static File makeTempDirectory(String prefix, String suffix, File dir)
			throws IOException {
		File f;
		do
			f = File.createTempFile(prefix, suffix, dir);
		while ((!(f.delete())) || (!(f.mkdir())));

		return f;
	}

	public static boolean isZipFile(String fileName) {
		return isZipFile(new File(fileName));
	}

	public static boolean isZipFile(File file) {
		ZipInputStream zip = null;
		FileInputStream stream = null;
		boolean i;
		try {
			stream = new FileInputStream(file);
			zip = new ZipInputStream(stream);
			ZipEntry zipEntry = zip.getNextEntry();
			i = (zipEntry != null) ? true : false;

			return i;
		} catch (IOException e) {
			return false;
		} finally {
			close(zip);
			close(stream);
		}
	}

	public static void copyZipEntry(File zipFile, String name, OutputStream dest)
			throws IOException {
		FileInputStream is = null;
		try {
			is = new FileInputStream(zipFile);
			ZipInputStream zis = new ZipInputStream(is);
			ZipEntry ze = zis.getNextEntry();
			while (ze != null) {
				if (ze.getName().equals(name)) {
					break;
				}
				ze = zis.getNextEntry();
			}
			if (ze == null) {
				throw new FileNotFoundException(zipFile.getPath() + ":" + name);
			}
			copy(zis, dest, false);
		} finally {
			close(is);
		}
	}

	public static void copyZipEntry(ZipFile zipFile,
			ZipOutputStream zipOutputStream, ZipEntry temp, String entryName)
			throws IOException {
		InputStream in = zipFile.getInputStream(temp);

		writeZipEntry(zipOutputStream, entryName, in);
		in.close();
	}

	public static void writeZipEntry(ZipOutputStream zipOutputStream,
			String entryName, InputStream in) throws IOException {
		zipOutputStream.putNextEntry(new ZipEntry(entryName));
		copy(in, zipOutputStream, false);
		zipOutputStream.closeEntry();
	}

	public static void copy(File src, File dest) throws IOException {
		if (src.isDirectory()) {
			if ((dest.exists()) && (!(dest.isDirectory()))) {
				throw new IllegalArgumentException(
						"Cannot copy directory to non-directory");
			}
			mkdirs(dest);
			File[] srcFiles = src.listFiles();
			for (int i = 0; i < srcFiles.length; ++i) {
				File file = srcFiles[i];
				copy(file, new File(dest, file.getName()));
			}
		} else {
			if (dest.isDirectory()) {
				dest = new File(dest, src.getName());
			}
			if (dest.getParentFile() != null) {
				mkdirs(dest.getParentFile());
			}
			FileInputStream srcStream = null;
			FileOutputStream destStream = null;
			try {
				srcStream = new FileInputStream(src);
				destStream = new FileOutputStream(dest);
				copy(srcStream, destStream);
				destStream.close();
			} finally {
				close(srcStream);
				closeAgain(destStream);
			}
		}
	}

	public static boolean readFully(InputStream in, byte[] data, int offset,
			int length) throws IOException {
		int pos = offset;
		int remaining = length;

		while (remaining > 0) {
			int readAmount = in.read(data, pos, remaining);
			if (readAmount <= 0) {
				return false;
			}

			pos += readAmount;
			remaining -= readAmount;
		}

		return true;
	}

	public static boolean readFully(InputStream in, byte[] data)
			throws IOException {
		return readFully(in, data, 0, data.length);
	}

	public static byte[] loadFileAsBytes(File file)
			throws FileNotFoundException, IOException {
		return loadFileAsBytes(new FileInputStream(file));
	}

	public static byte[] loadFileAsBytes(InputStream input) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try {
			byte[] data = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(data)) >= 0) {
				
				bytes.write(data, 0, bytesRead);
			}
			byte[] arrayOfByte1 = bytes.toByteArray();

			return arrayOfByte1;
		} finally {
			input.close();
			bytes.close();
		}
	}

	public static int findLastIndex(String searchString, String sourceString) {
		if ((searchString == null) || (sourceString == null)) {
			return -1;
		}
		return findLastIndexHelper(searchString, sourceString, 0);
	}

	private static int findLastIndexHelper(String searchString,
			String sourceString, int startIndex) {
		int index = sourceString.indexOf(searchString, startIndex);
		if (index == -1) {
			return index;
		}
		int lastIndexMaybe = findLastIndexHelper(searchString, sourceString,
				index + 1);
		if (lastIndexMaybe == -1) {
			return index;
		}
		return lastIndexMaybe;
	}

	public static IOException close(ZipFile zip) {
		if (zip == null) {
			return null;
		}
		try {
			zip.close();
		} catch (IOException ex) {
			return ex;
		}
		return null;
	}

	public static String getShortFileName(String fullPath) {
		if (fullPath == null) {
			return null;
		}

		String normalizedPath = normalizeDirectorySeperators(fullPath);

		int lastSlash = normalizedPath.lastIndexOf("/");
		if (lastSlash == -1) {
			return fullPath;
		}

		return normalizedPath.substring(lastSlash + 1);
	}

	public static String removeExtension(String shortName) {
		int lastDot = shortName.lastIndexOf(46);
		if (lastDot > 0) {
			shortName = shortName.substring(0, lastDot);
		}
		return shortName;
	}

	public static boolean mkdirs(File file) {
		if (file.exists()) {
			return file.isDirectory();
		}
		if (file.mkdir())
			return true;
		File canon;
		try {
			canon = file.getCanonicalFile();
		} catch (IOException e) {
			return false;
		}
		File parent = canon.getParentFile();
		mkdirs(parent);
		if (parent.isDirectory()) {
			canon.mkdir();
		}
		return file.isDirectory();
	}

	public static void ensurePathExists(String path) {
		File workDir = new File(path);

		if (workDir.exists())
			return;
		try {
			if (!(mkdirs(workDir)))
				logger.error("Error creating path");
		} catch (SecurityException e) {
			logger.error("Error creating path", e);
		}
	}

	public static PrintWriter makeUTF8Writer(OutputStream os) {
		try {
			return new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("UTF-8 not supported!");
		}
	}

	public static File getCaseInsensitiveFile(File f) {
		Stack parents = new Stack();
		File p = new File(f.getAbsolutePath());
		do {
			File fromCache = (File) caseInsensitiveFileCache.get(p
					.getAbsolutePath().toLowerCase());
			if (fromCache != null) {
				p = fromCache;
			}

			parents.push(p);
			p = p.getParentFile();
		} while (!(p.exists()));

		if ((p != null) && (p.isDirectory()) && (!(parents.empty()))) {
			File child = (File) parents.pop();
			File[] files = p.listFiles();
			for (int i = 0; i < files.length; ++i) {
				File s = files[i];
				if (child.getName().equalsIgnoreCase(s.getName())) {
					p = s;
					caseInsensitiveFileCache.put(p.getAbsolutePath()
							.toLowerCase(), p);
				}

			}

			return null;
		}

		return p;
	}

	public static boolean exists(File file) {
		return ((file != null) && (file.exists()));
	}

	public static boolean isExistingFile(String path) {
		return ((path != null) && (isExistingFile(new File(path))));
	}

	public static boolean isExistingFile(File file) {
		return ((exists(file)) && (file.isFile()));
	}

	public static boolean isExistingDirectory(String path) {
		return ((path != null) && (isExistingDirectory(new File(path))));
	}

	public static boolean isExistingDirectory(File file) {
		return ((exists(file)) && (file.isDirectory()));
	}

	public static boolean isRoot(File file) {
		if (!(exists(file))) {
			return false;
		}

		File[] roots = File.listRoots();
		for (File root : roots) {
			if (root.equals(file)) {
				return true;
			}
		}
		return false;
	}

	public static class ExtensionComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			if ((o1 instanceof String) && (o2 instanceof String)) {
				return compare((String) o1, (String) o2);
			}
			if ((o1 instanceof File) && (o2 instanceof File)) {
				return compare(((File) o1).getName(), ((File) o2).getName());
			}

			throw new IllegalArgumentException("Cannot compare extensions of "
					+ o1.getClass() + ", " + o2.getClass());
		}

		private int compare(String s1, String s2) {
			String e1 = FileUtil.getExtension(s1);
			String e2 = FileUtil.getExtension(s2);
			int i1 = FileUtil.WIN32_EXE_ORDER.indexOf(e1);
			int i2 = FileUtil.WIN32_EXE_ORDER.indexOf(e2);
			if (i1 == -1) {
				i1 = FileUtil.WIN32_EXE_ORDER.size();
			}
			if (i2 == -1) {
				i2 = FileUtil.WIN32_EXE_ORDER.size();
			}
			if (i1 != i2) {
				return ((i1 < i2) ? -1 : 1);
			}
			return s1.compareTo(s2);
		}
	}

	public static String relativePath(String base, String abstractPath) {
		
		String relative = new File(base).toURI().relativize(new File(abstractPath).toURI()).getPath();
		
		return relative;
	}
}