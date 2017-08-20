package com.kichang.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import org.apache.log4j.Logger;


/** 
 *  
 */
public class HttpsClientWithoutValidation {

	private final String USER_AGENT = "Mozilla/5.0";
	Logger logger = Logger.getLogger(HttpsClientWithoutValidation.class);

	public byte[] getHttps(String urlString) throws NoSuchAlgorithmException, KeyManagementException, IOException {

		// Get HTTPS URL connection
		URL url = new URL(urlString);
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		
		conn.setConnectTimeout(3000);
		conn.setReadTimeout(1000);

		// Set Hostname verification
		conn.setHostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				// Ignore host name verification. It always returns true.
				return true;
			}

		});

		// SSL setting
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(null,
				new FakeX509TrustManager[] { new FakeX509TrustManager() }, null); // No
																					// validation
																					// for
																					// now
		conn.setSSLSocketFactory(context.getSocketFactory());

		// Connect to host
		conn.connect();
		conn.setInstanceFollowRedirects(true);

		// Print response from host
		InputStream in = conn.getInputStream();
		DataInputStream dis = new DataInputStream( in );
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		byte[] tempBuffer = new byte[1];

		while(( dis.read ( tempBuffer ) != -1 ) )  {
				baos.write( tempBuffer ) ;
		}
		baos.flush();
		
		dis.close();
		
		return baos.toByteArray();
	}

	public byte[] postData(String urlString, String postdata) throws IOException,NoSuchAlgorithmException, KeyManagementException {

		// Get HTTPS URL connection
		URL url = new URL(urlString);
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		
		conn.setConnectTimeout(3000);
		conn.setReadTimeout(1000);
		
		// Set Hostname verification
		conn.setHostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				// Ignore host name verification. It always returns true.
				return true;
			}
		
		});
		
		// SSL setting
		{
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null,
					new FakeX509TrustManager[] { new FakeX509TrustManager() }, null); // No
																						// validation
																						// for
																						// now
			conn.setSSLSocketFactory(context.getSocketFactory());
		}
		
		// add request header
		conn.setRequestMethod("POST");
		conn.setRequestProperty("User-Agent", USER_AGENT);
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
 
		// Send post request
		conn.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		String encoded = URLEncoder.encode(postdata,"UTF-8");
		
		
		logger.debug("======================= Encoded Data ===========================");
		logger.debug(encoded);
		logger.debug("=================================================================");
		
		
		wr.writeBytes(encoded);
		wr.flush();
		wr.close();
		
		// Connect to host
		conn.connect();
		conn.setInstanceFollowRedirects(true);
		
		// Print response from host
		InputStream in = conn.getInputStream();
		DataInputStream dis = new DataInputStream( in );
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		byte[] tempBuffer = new byte[1];
		
		while(( dis.read ( tempBuffer ) != -1 ) )  {
				baos.write( tempBuffer ) ;
		}
		baos.flush();
		
		dis.close();
		
		return baos.toByteArray();
	}

	
	private static void getPattern() throws KeyManagementException, NoSuchAlgorithmException, IOException {
		HttpsClientWithoutValidation test = new HttpsClientWithoutValidation();
		
		byte[] b = test.getHttps("https://118.131.241.109:8002/wui/updatefile/638fc85290c52bf0e7e84cbdedb9d39d");

		System.out.println(Crypto.getHash(b, "MD5"));		
		FileOutputStream fos = new FileOutputStream("sample.txt");
		
		fos.write(b);
		fos.close();
		
	}
}