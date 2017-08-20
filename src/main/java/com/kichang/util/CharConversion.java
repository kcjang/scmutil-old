/*
 * Created on 2005. 7. 15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.kichang.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * @author edutec
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CharConversion {

	/**
	 * 
	 */
	public CharConversion() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static String convert(String value, String encFrom, String encTo) 
		throws UnsupportedEncodingException	{
		return new String(value.getBytes(encFrom),encTo);
	}
	public static String native2Ascii(String text) {
        StringBuffer buffer = new StringBuffer();
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] < 128) {
                buffer.append(chars[i]);
            } else if (chars[i] >= 128 && chars[i] < 256) {
                buffer.append("\\u00" + Integer.toString(chars[i], 16));
            } else {
                buffer.append("\\u" + Integer.toString(chars[i], 16));
            }
        }
        return buffer.toString();
    }



	public static String ascii2native(String str) {
		String hex = "0123456789ABCDEF" ;
		StringBuffer buf = new StringBuffer() ;
		int ptn = 0;

		for(int i=0; i<str.length(); i++) {
			char c = str.charAt(i) ;
			if ( c == '\\' && i + 1 <= str.length() && str.charAt(i+1) == '\\' ) {
				buf.append("\\\\") ;
				i += 1 ;
			} else if ( c == '\\' && i + 6 <= str.length() && str.charAt(i+1) == 'u' ) {
				String sub = str.substring(i+2, i+6).toUpperCase() ;
				int i0 = hex.indexOf(sub.charAt(0)) ;
				int i1 = hex.indexOf(sub.charAt(1)) ;
				int i2 = hex.indexOf(sub.charAt(2)) ;
				int i3 = hex.indexOf(sub.charAt(3)) ;

				if ( i0 < 0 || i1 < 0 || i2 < 0 || i3 < 0 ) {
					buf.append("\\u") ;
					i += 1 ;
				} else {
					byte[] data = new byte[2] ;
					data[0] = i2b(i1 + i0 * 16) ;
					data[1] = i2b(i3 + i2 * 16) ;
					try{
						buf.append(new String(data, "UTF-16BE").toString()) ;
					} catch(Exception ex) {
						buf.append("\\u" + sub) ;
					}
					i += 5 ;
				}
			} else {
				buf.append(c) ;
			}
		}

		return buf.toString() ;
	}

	/**
	 * binary to unsigned integer
	 * <P>
	 * @param	b	binary
	 * @return	unsined integer
	 */
	private static int b2i(byte b)
	{
		return (int)( (b<0) ? 256+b : b ) ;
	}

	/**
	 * unsigned integer to binary
	 * <P>
	 * @param	i	unsigned integer
	 * @return	binary
	 */
	private static byte i2b(int i)
	{
		return (byte)( (i>127) ? i-256 : i ) ;
	}
	
	
	

	
	
}
