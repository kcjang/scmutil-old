package com.kichang.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class Crypto2 {
	static Log logger = LogFactory.getLog(Crypto2.class);
	public static String hashSha512(String message) {
		return getHash(message,"SHA-512");
	}
	public static String getHash(String message, String algorithm) {
		try {
			String hex = "";
			byte[] buffer = message.getBytes();
			MessageDigest md = MessageDigest.getInstance(algorithm);
			md.update(buffer);
			byte[] digest = md.digest();
			for(int i = 0 ; i < digest.length ; i++) {
				int b = digest[i];
				String toHex = Integer.toHexString(b);
				if (toHex.length() < 2) {
					toHex = "0" +toHex;
				} else {
					toHex = toHex.substring(toHex.length()-2);
				}
				hex += toHex;
			}
			return hex;
		} catch(NoSuchAlgorithmException e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}

	static String iv = "86afc43868fea6abd40fbf6d5ed50905";
	
	public static String encrypt(String data,String pass) throws Exception { 
		String key = getHash(pass,"MD5");
		
		
		byte aesKeyData[] = Hex.decodeHex(key.toCharArray());
		byte ivData[] = Hex.decodeHex(iv.toCharArray());
		
		if (data == null || data.length() == 0)
			return ""; 

		Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec secretKeySpec = new SecretKeySpec(aesKeyData, "AES");
		IvParameterSpec ivParameterSpec = new IvParameterSpec(ivData);
		aes.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
		
		byte[] enc = aes.doFinal(data.getBytes());
		//System.out.println("plain text " + data.length() + " bytes");
		//System.out.println("encoded bytes " + enc.length + " bytes");

		sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
		return encoder.encodeBuffer(enc);
	} 

		
}
