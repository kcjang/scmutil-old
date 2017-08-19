package com.kichang.util;

import java.io.* ;

/**
 * Ascii2Native - \\uxxxx convert Native code
 * @version 1.1
 * @Author so-miya@mma.gr.jp
 */
public class ConvNative2Ascii {
	/**
	 * TestMain
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String fname = "D:\\WSFinder\\eclipse\\WS-Finder-Server\\src\\Messages.properties_native";
		String fname2 = "D:\\WSFinder\\eclipse\\WS-Finder-Server\\src\\Messages.properties";
		File f = new File(fname) ;
		File f2 = new File(fname2);
		String target = "";
		if ( f.exists() && f.isFile() ) {
			// convert param-file
			BufferedReader br = null ;
			String line ;
	
			try {
				br = new BufferedReader(
						new InputStreamReader(
							new FileInputStream(f), "JISAutoDetect")) ;
	
				while ((line = br.readLine()) != null) {
					target+=CharConversion.native2Ascii(line);
					target+="\n";
				}
			} catch (FileNotFoundException e) {
				System.err.println("file not found - " + fname) ;
			} catch (IOException e) {
				System.err.println("read error - " + fname) ;
			} finally {
				try {
					if (br != null) br.close();
				} catch (Exception e) {}
			}
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(f2))) ;
			bw.write(target);
			bw.close();
		} else {
			// convert param-data
			System.out.print(CharConversion.native2Ascii(fname)) ;
		}
		
	}
	

}
