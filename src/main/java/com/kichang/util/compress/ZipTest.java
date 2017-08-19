package com.kichang.util.compress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ZipTest {
	public static void main(String[] args) throws IOException, DecompressException {
		//File f = new File("/home/kcjang/srczip/errorfile.zip");
		File f = new File("/home/kcjang/srczip/eightball.zip");
		ZipStreamDecompress zip = new ZipStreamDecompress(
				new FileInputStream(f),"/home/kcjang/srczip","errorfile"
				);
		zip.decompress();

		
	}


}
