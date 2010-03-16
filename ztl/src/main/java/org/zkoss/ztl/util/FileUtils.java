package org.zkoss.ztl.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;


public class FileUtils {
	public static String readFile(String file) throws FileNotFoundException{
		return readFile(new File(file));
	}
	public static String readFile(File file) throws FileNotFoundException{
		
		Scanner sc=new Scanner(new FileReader(file));
	
		StringBuffer sb= new StringBuffer();
		while(sc.hasNextLine()){
			sb.append(sc.nextLine()+System.getProperty("line.separator"));
		}
	
		sc.close();
		return sb.toString();
		
	}
}
