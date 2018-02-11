package com.kichang.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Calendar;
import java.util.Enumeration;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
/*
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.RenderedOp;

import com.sun.media.jai.codec.SeekableStream;
*/
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;





public class FileUtil {
	static Log logger = LogFactory.getLog(FileUtil.class);
	
	protected int tw=176;
	protected int th=144;
	protected int sharpen=100;
	protected float quality=1;
	protected String bdColor="#000000";
	protected String bgColor="#000000";
	protected boolean border=false;
	
	
	public String getBdColor() {
		return bdColor;
	}


	public String getBgColor() {
		return bgColor;
	}


	public boolean isBorder() {
		return border;
	}


	public float getQuality() {
		return quality;
	}


	public int getSharpen() {
		return sharpen;
	}


	public int getTh() {
		return th;
	}


	public int getTw() {
		return tw;
	}


	public void setBdColor(String bdColor) {
		this.bdColor = bdColor;
	}


	public void setBgColor(String bgColor) {
		this.bgColor = bgColor;
	}


	public void setBorder(boolean border) {
		this.border = border;
	}


	public void setQuality(float quality) {
		this.quality = quality;
	}


	public void setSharpen(int sharpen) {
		this.sharpen = sharpen;
	}


	public void setTh(int th) {
		this.th = th;
	}


	public void setTw(int tw) {
		this.tw = tw;
	}
	
	public static boolean isImageFile(String filename) {
		String ext = filename.substring(filename.lastIndexOf('.')+1);
		if (ext.equalsIgnoreCase("jpg") || 
				ext.equalsIgnoreCase("gif") ||
				ext.equalsIgnoreCase("jpeg"))
			return true;
		else
			return false;
	}

	public static String getFileName(String path) {
		path = path.replace('\\', '/');
		String fileName = path.substring(path.lastIndexOf('/')+1);
		
		return fileName;
	}
	
	public static String getExt(String filename) {
		return filename.substring(filename.lastIndexOf('.')+1);
	}

	public static String getFileNameBody(String path) {
		path = path.replace('\\', '/');
		String filename = path.substring(path.lastIndexOf('/')+1);
		
		return filename.substring(0, filename.lastIndexOf('.'));
	}

	

	public String saveEduFile(InputStream stream, String photoPath, String saveName) {
		
		String tempFileName = getTempName() + "_" + saveName;
		
		try {
			OutputStream os = new FileOutputStream(photoPath + "/" + tempFileName );
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = stream.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            stream.close();
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		} finally {
			
		}
		return tempFileName;
	}
	

	
	public boolean deleteFile(String path) {
		File file = new File(path);
		return file.delete();
	}	
	private String getTempName() {
		return String.valueOf(Calendar.getInstance().getTimeInMillis());
		/*
		UUID uuid = UUID.randomUUID();
		String tempFileName = uuid.toString();
		return tempFileName;
		*/
	}
	
	public static String getTimeInMillis() {
		return String.valueOf(Calendar.getInstance().getTimeInMillis());
	}
	
	public static int unzip(String zipPath,String unzipPath) throws IOException {
		Log logger = LogFactory.getLog(FileUtil.class);
		
		File unzipDir = new File(unzipPath);
		if (!unzipDir.exists()) {
			unzipDir.mkdirs();
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("========================= UNZIP START ================");
			logger.debug("arguments : zipPath =" + zipPath + ",unzipPath = " + unzipPath);
		}
		
		ZipFile zipFile = new ZipFile(zipPath);
		Enumeration e = zipFile.getEntries();
		while(e.hasMoreElements()) {
			ZipArchiveEntry zipEntry = (ZipArchiveEntry)e.nextElement();
			boolean isDirectory = zipEntry.isDirectory();

			File unzipFile = new File(unzipPath+File.separator+zipEntry.getName());

			if (logger.isDebugEnabled()) {
				logger.debug("unzipFile PATH : " + unzipFile.getPath());
			}
			
			if (isDirectory) {
				if (logger.isDebugEnabled()) {
					logger.debug("unzip is directory");
				}
				boolean mdresult = unzipFile.mkdirs();
				
				if (logger.isDebugEnabled()) {
					logger.debug("unzip.mkdirs() result : " + mdresult);
				}
			} else {
				File parent = unzipFile.getParentFile();
				boolean mdresult = parent.mkdirs();
				if (logger.isDebugEnabled()) {
					logger.debug("parent.mkdirs() result : " + mdresult);
				}
				FileOutputStream fout = new FileOutputStream(unzipFile);
				InputStream in = zipFile.getInputStream(zipEntry);
				copy(in,fout);
				in.close();
				fout.close();
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("=====================================================");
		}
		return 0;
	}
	
	public static void copy(InputStream in, OutputStream out) throws IOException{
		synchronized(in) {
			synchronized(out) {
				byte[] buffer = new byte[256];
				while(true) {
					int bytesRead = in.read(buffer);
					if(bytesRead == -1) break;
					out.write(buffer,0,bytesRead);
				}
			}
		}
		
		
	}
	public static Reader run(String cmd) throws IOException {
		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec(cmd);
		return new InputStreamReader(proc.getErrorStream());

	}
	
	private static final int TEMP_DIR_ATTEMPTS = 10000;
	public static File createTempDir() {
		  File baseDir = new File(System.getProperty("java.io.tmpdir"));
		  String baseName = System.currentTimeMillis() + "-";

		  for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
		    File tempDir = new File(baseDir, baseName + counter);
		    if (tempDir.mkdir()) {
		      return tempDir;
		    }
		  }
		  throw new IllegalStateException("Failed to create directory within "
		      + TEMP_DIR_ATTEMPTS + " attempts (tried "
		      + baseName + "0 to " + baseName + (TEMP_DIR_ATTEMPTS - 1) + ')');
		}
	
	
}
