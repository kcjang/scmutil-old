package com.kichang.util.compress;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kichang.util.StringUtil;

public class ZipStreamDecompress {
	Log logger = LogFactory.getLog(ZipStreamDecompress.class);
	private ZipInputStream zin;
	private String targetDir = "";
	public static int CACHESIZ = 1024 * 1024 * 100; // 100 Mega Character
	
	
	public ZipStreamDecompress(InputStream in, String extraceDir, String subdir) {
		this.zin = new ZipInputStream(in);
		targetDir = extraceDir;

		if (!StringUtil.isEmptyOrWhitespaceOnly(subdir))
			targetDir += File.separator + subdir;
		
		File froot = new File(targetDir);
		if (!froot.exists())
			froot.mkdir();
	}
	
	@SuppressWarnings("rawtypes")
	public void decompress(String target) throws ZipException, IOException {
		ZipEntry ze;
		while ( (ze = zin.getNextEntry()) != null) {
			String entry = ze.getName();
			if (entry.equalsIgnoreCase(target)) {
				logger.debug("압축해제 시작 : " + target);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] byteBuffer = new byte[1024];
				byte[] byteData = null;
				FileOutputStream fos = new FileOutputStream(targetDir + File.separator + entry);
				int nLength = 0;
				int nWrite = 0;
				long totalWrite = 0;
				
				while ((nLength = zin.read(byteBuffer)) > 0) {
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
				baos.close();
				fos.close();
				logger.debug("압축해제 완료 : " + NumberFormat.getInstance().format(totalWrite) + " bytes");
			}
		}
	}
	

	public void decompress() throws IOException, DecompressException {
		String lastFile = "";
		while (true) {
			ZipEntry ze = null;
			try {
				ze = (ZipEntry) zin.getNextEntry();
			} catch (IllegalArgumentException de) {
				throw new DecompressException(lastFile + " 다음 파일");
			}
			if (ze == null)
				break;
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
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] byteBuffer = new byte[1024];
				byte[] byteData = null;
				FileOutputStream fos = null;
				int nLength = 0;
	
				while ((nLength = zin.read(byteBuffer)) > 0) {
					baos.write(byteBuffer, 0, nLength);
				}
				byteData = baos.toByteArray();
				fos = new FileOutputStream(targetDir + File.separator + entry);
				fos.write(byteData);
				fos.close();
			}
		}

	}

	public String getTargetDir() {
		return targetDir;
	}
	
}
