package sujoo.test;

import java.io.File;

import sujoo.util.FileReaderUtil;

public class FileReaderUtilTest {

	public static void main(String[] args) {
		File file =  new File("C:\\Users\\mbcusick\\Documents\\Data\\TestSets\\test.txt");
		try {
			FileReaderUtil reader = new FileReaderUtil(file, ",");
			
			while (reader.hasNext()) {
				String[] line = reader.next();
				System.out.println("0: " + line[0] + "\t" + "1: " + line[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
