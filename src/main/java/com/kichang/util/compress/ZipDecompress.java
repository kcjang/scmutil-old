package com.kichang.util.compress;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ZipDecompress {
	Log logger = LogFactory.getLog(ZipDecompress.class);
	private File zipFile;
	private String targetDir = "";
	public static int CACHESIZ = 1024 * 1024 * 100; // 100 Mega Character
	
	
	public ZipDecompress(File zipFile, String extraceDir) {
		this(zipFile,extraceDir,true);
	}

	public ZipDecompress(File zipFile, String extraceDir, boolean createFileNameFolder) {
		this.zipFile = zipFile;
		String fileName = zipFile.getName();
		targetDir = extraceDir;
		if (createFileNameFolder)
		targetDir += File.separator + fileName.substring(0, fileName.lastIndexOf("."));
		
		File froot = new File(targetDir);
		if (!froot.exists())
			froot.mkdir();
	}

	@SuppressWarnings("rawtypes")
	public void decompress(String target) throws ZipException, IOException {

			ZipFile zf = new ZipFile(zipFile);
			Enumeration e = zf.entries();

			while (e.hasMoreElements()) {
				ZipEntry ze = (ZipEntry) e.nextElement();
				String entry = ze.getName();
				if (entry.equalsIgnoreCase(target)) {
					logger.debug("압축해제 시작 : " + target);
					InputStream is = zf.getInputStream(zf.getEntry(entry));
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte[] byteBuffer = new byte[1024];
					byte[] byteData = null;
					FileOutputStream fos = new FileOutputStream(targetDir + File.separator + entry);
					int nLength = 0;
					int nWrite = 0;
					long totalWrite = 0;
					
					while ((nLength = is.read(byteBuffer)) > 0) {
						baos.write(byteBuffer, 0, nLength);
						nWrite += nLength;
						totalWrite += nLength;
						if (nWrite > CACHESIZ) {
							
							byteData = baos.toByteArray();
							fos.write(byteData);
							baos.reset();
							nWrite = 0;
						}
					}
					{
						byteData = baos.toByteArray();
						fos.write(byteData);
					}
					is.close();
					baos.close();
					fos.close();
					logger.debug("압축해제 완료 : " + NumberFormat.getInstance().format(totalWrite) + " bytes");
				}
			}
			zf.close();

	}
	

	public void decompressAll() throws IOException, DecompressException {

			ZipFile zf = new ZipFile(zipFile);
			Enumeration e = zf.entries();
			String lastFile = "";
			while (e.hasMoreElements()) {
				ZipEntry ze = null;
				try {
					ze = (ZipEntry) e.nextElement();
					
				} catch (IllegalArgumentException de) {
					throw new DecompressException(lastFile + " 다음 파일");
				}
				lastFile = ze.getName();
				String entry = ze.getName();
				int startIndex = 0;
				int endIndex = 0;
				boolean bDirectory = false;
				String directory = "";

				while (true) {
					endIndex = entry.indexOf('/', startIndex);
					if (endIndex != -1) {
						directory = entry.substring(0, endIndex);
						File fileDirectory = new File(targetDir + File.separator
								+ directory);
						if (fileDirectory.exists() == false) {
							fileDirectory.mkdir();
						}
						startIndex = endIndex + 1;
					} else {
						break;
					}
					if (endIndex + 1 == entry.length()) {
						bDirectory = true;
					}
				}

				if (bDirectory == false) {
					InputStream is = zf.getInputStream(zf.getEntry(entry));
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte[] byteBuffer = new byte[1024];
					byte[] byteData = null;
					FileOutputStream fos = null;
					int nLength = 0;

					while ((nLength = is.read(byteBuffer)) > 0) {
						baos.write(byteBuffer, 0, nLength);
					}
					is.close();
					byteData = baos.toByteArray();
					fos = new FileOutputStream(targetDir + File.separator + entry);
					fos.write(byteData);
					fos.close();
				}
			}
			zf.close();

	}

	public String getTargetDir() {
		return targetDir;
	}
	


}
