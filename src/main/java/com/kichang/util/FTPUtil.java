package com.kichang.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

public class FTPUtil {
	
	
	/**
	 * Download a single file from the FTP server
	 * @param ftpClient an instance of org.apache.commons.net.ftp.FTPClient class.
	 * @param remoteFilePath path of the file on the server
	 * @param savePath path of directory where the file will be stored
	 * @return true if the file was downloaded successfully, false otherwise
	 * @throws IOException if any network or IO error occurred.
	 */
	public static boolean downloadSingleFile(FTPClient ftpClient,
	        String remoteFilePath, String savePath) throws IOException {
	    File downloadFile = new File(savePath);
	     
	    File parentDir = downloadFile.getParentFile();
	    if (!parentDir.exists()) {
	        parentDir.mkdir();
	    }
	         
	    OutputStream outputStream = new BufferedOutputStream(
	            new FileOutputStream(downloadFile));
	    try {
	        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	        return ftpClient.retrieveFile(remoteFilePath, outputStream);
	    } catch (IOException ex) {
	        throw ex;
	    } finally {
	        if (outputStream != null) {
	            outputStream.close();
	        }
	    }
	}
	/**
	 * Download a whole directory from a FTP server.
	 * @param ftpClient an instance of org.apache.commons.net.ftp.FTPClient class.
	 * @param parentDir Path of the parent directory of the current directory being
	 * downloaded.
	 * @param currentDir Path of the current directory being downloaded.
	 * @param saveDir path of directory where the whole remote directory will be
	 * downloaded and saved.
	 * @throws IOException if any network or IO error occurred.
	 */
	public static void downloadDirectory(FTPClient ftpClient, String parentDir,
	        String currentDir, String saveDir) throws IOException {
	    String dirToList = parentDir;
	    if (!currentDir.equals("")) {
	        dirToList += "/" + currentDir;
	    }
	 
	    FTPFile[] subFiles = ftpClient.listFiles(dirToList);
	 
	    if (subFiles != null && subFiles.length > 0) {
	        for (FTPFile aFile : subFiles) {
	            String currentFileName = aFile.getName();
	            if (currentFileName.equals(".") || currentFileName.equals("..")) {
	                // skip parent directory and the directory itself
	                continue;
	            }
	            String filePath = parentDir + "/" + currentDir + "/"
	                    + currentFileName;
	            if (currentDir.equals("")) {
	                filePath = parentDir + "/" + currentFileName;
	            }
	 
	            String newDirPath = saveDir + parentDir + File.separator
	                    + currentDir + File.separator + currentFileName;
	            if (currentDir.equals("")) {
	                newDirPath = saveDir + parentDir + File.separator
	                          + currentFileName;
	            }
	 
	            if (aFile.isDirectory()) {
	                // create the directory in saveDir
	                File newDir = new File(newDirPath);
	                boolean created = newDir.mkdirs();
	                if (created) {
	                    System.out.println("CREATED the directory: " + newDirPath);
	                } else {
	                    System.out.println("COULD NOT create the directory: " + newDirPath);
	                }
	 
	                // download the sub directory
	                downloadDirectory(ftpClient, dirToList, currentFileName,
	                        saveDir);
	            } else {
	                // download the file
	                boolean success = downloadSingleFile(ftpClient, filePath,
	                        newDirPath);
	                if (success) {
	                    System.out.println("DOWNLOADED the file: " + filePath);
	                } else {
	                    System.out.println("COULD NOT download the file: "
	                            + filePath);
	                }
	            }
	        }
	    }
	}
	
	public static void cleanLocal(String localroot) throws IOException {
		File f = new File(localroot);
		
		File[] subs = f.listFiles();
		for(File sub : subs) {
			if (sub.isDirectory())
				FileUtils.deleteDirectory(sub);
			else 
				sub.delete();
		}
	}
	
	public static void downloadDir(ChannelSftp sftpChannel, String sourcePath, String destPath) throws SftpException { // With subfolders and all files.
	    // Create local folders if absent.
	    try {
	        new File(destPath).mkdirs();
	    } catch (Exception e) {
	        System.out.println("Error at : " + destPath);
	    }
	    sftpChannel.lcd(destPath);

	    // Copy remote folders one by one.
	    lsFolderCopy(sftpChannel, sourcePath, destPath); // Separated because loops itself inside for subfolders.
	}

	private static void lsFolderCopy(ChannelSftp sftpChannel, String sourcePath, String destPath) throws SftpException { // List source (remote, sftp) directory and create a local copy of it - method for every single directory.
	    Vector<ChannelSftp.LsEntry> list = sftpChannel.ls(sourcePath); // List source directory structure.
	    for (ChannelSftp.LsEntry oListItem : list) { // Iterate objects in the list to get file/folder names.
	        if (!oListItem.getAttrs().isDir()) { // If it is a file (not a directory).
	            if (!(new File(destPath + "/" + oListItem.getFilename())).exists() || (oListItem.getAttrs().getMTime() > Long.valueOf(new File(destPath + "/" + oListItem.getFilename()).lastModified() / (long) 1000).intValue())) { // Download only if changed later.
	                new File(destPath + "/" + oListItem.getFilename());
	                sftpChannel.get(sourcePath + "/" + oListItem.getFilename(), destPath + "/" + oListItem.getFilename()); // Grab file from source ([source filename], [destination filename]).
	            }
	        } else if (!".".equals(oListItem.getFilename()) &&!"..".equals(oListItem.getFilename())) {
	            new File(destPath + "/" + oListItem.getFilename()).mkdirs(); // Empty folder copy.
	            lsFolderCopy(sftpChannel, sourcePath + "/" + oListItem.getFilename(), destPath + "/" + oListItem.getFilename()); // Enter found folder on server to read its contents and create locally.
	        }
	    }
	}
}
