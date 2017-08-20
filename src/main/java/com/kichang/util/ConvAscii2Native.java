package com.kichang.util;

import java.io.* ;

public class ConvAscii2Native {

	public static void main(String[] args) throws IOException {
		String fname = "D:\\WSFinder\\eclipse\\WS-Finder-Server\\src\\Messages.properties";
		String fname2 = "D:\\WSFinder\\eclipse\\WS-Finder-Server\\src\\Messages.properties_native";
		File f = new File(fname) ;
		File f2 = new File(fname2);
		String target = "";
		if ( f.exists() && f.isFile() ) {

			BufferedReader br = null ;
			String line ;
	
			try {
				br = new BufferedReader(
						new InputStreamReader(
							new FileInputStream(f), "JISAutoDetect")) ;
	
				while ((line = br.readLine()) != null) {
					target+=CharConversion.ascii2native(line);
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
			System.out.print(CharConversion.ascii2native(fname)) ;
		}
		
	}
	

}
