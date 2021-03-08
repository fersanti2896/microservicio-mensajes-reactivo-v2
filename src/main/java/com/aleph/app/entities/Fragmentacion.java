package com.aleph.app.entities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorInputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;

public class Fragmentacion {
	public static void compressLz4File(String str1, String str2) throws IOException {

		InputStream in = Files.newInputStream(Paths.get(str1));
		OutputStream fout = Files.newOutputStream(Paths.get(str2));
		BufferedOutputStream out = new BufferedOutputStream(fout);
		FramedLZ4CompressorOutputStream lzOut = new FramedLZ4CompressorOutputStream(out);
		final byte[] buffer = new byte[1024];
		int n = 0;
		while (-1 != (n = in.read(buffer))) {
			lzOut.write(buffer, 0, n);
		}

		lzOut.close();
		in.close();
	}

	public static void uncompressLz4File(String str1, String str2) {
		File f1 = new File(str1);
		File f2 = new File(str2);
		try (FileInputStream fin = new FileInputStream(f1);
				BufferedInputStream in = new BufferedInputStream(fin);
				OutputStream out = Files.newOutputStream(Paths.get(f2.getAbsolutePath()));
				FramedLZ4CompressorInputStream zIn = new FramedLZ4CompressorInputStream(in)) {
			int n;
			byte[] b = new byte[1024];
			while ((n = zIn.read(b)) > 0) {
				out.write(b, 0, n);
			}

			System.out.println("Tamaño de archivo comprimido: " + f1.length());
			System.out.println("Tamaño de archivo descomprimido: " + f2.length());
			float tasa = (float) f2.length() / f1.length();
			System.out.printf("Tasa de compresion: %f", tasa);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
