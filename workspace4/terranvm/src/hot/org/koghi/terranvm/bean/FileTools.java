package org.koghi.terranvm.bean;

import java.io.FileInputStream;
import java.util.Date;

import org.koghi.terranvm.entity.File;
import org.koghi.terranvm.entity.PDFFile;
import org.koghi.terranvm.entity.SIGGOFile;

public class FileTools {

	public static File createSIIGOFileInstance(java.io.File file) {
		byte[] bFile = new byte[(int) file.length()];

		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			fileInputStream.read(bFile);
			fileInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		File localFile = new SIGGOFile();
		localFile.setData(bFile);
		localFile.setDate(new Date());
		return localFile;
	}
	
	public static File createPDFFileInstance(java.io.File file) {
		byte[] bFile = new byte[(int) file.length()];

		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			fileInputStream.read(bFile);
			fileInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		File localFile = new PDFFile();
		localFile.setData(bFile);
		localFile.setDate(new Date());
		return localFile;
	}
}
