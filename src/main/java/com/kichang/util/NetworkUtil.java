package com.kichang.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.fileupload.FileItem;

public class NetworkUtil {
	public static byte[] getUrlFile(String urlpath) throws IOException {
		URL url = new URL(urlpath);
		URLConnection urlcon = url.openConnection();
		
		DataInputStream dis = new DataInputStream( urlcon.getInputStream() );
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		byte[] tempBuffer = new byte[1];

		while(( dis.read ( tempBuffer ) != -1 ) )  {
				baos.write( tempBuffer ) ;
		}
		baos.flush();
		
		return baos.toByteArray();
	}
	public static Map<String,String> getPostFormParameters(List<FileItem> paramList) throws IOException {
		Map<String,String> map = new HashMap<String,String>();
		Iterator<FileItem> itr = paramList.iterator();
		
	    while(itr.hasNext()) {
	    	FileItem fileItem = (FileItem)itr.next();
	    	if (fileItem.isFormField()) {
	    		InputStream is = fileItem.getInputStream();
	    		int size = (int)fileItem.getSize();
	    		byte[] buffer = new byte[size];
	    		is.read(buffer);
	    		map.put(fileItem.getFieldName(), new String(buffer));
	    		is.close();
	    	}
	    }
		return map;
	}
	
}
