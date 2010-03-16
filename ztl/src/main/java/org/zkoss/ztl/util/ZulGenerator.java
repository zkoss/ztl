/* ZtlGenerator.java

{{IS_NOTE
	Purpose:

	Description:

	History:
		Mar 3, 2010 7:06:20 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
 */
package org.zkoss.ztl.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * The ZUL generator that generates the ztl from zul file . help us to build ztl
 * faster from other zul
 * 
 * @author TonyQ
 */
public class ZulGenerator {

	private static void log(Object... os) {
		for (Object o : os) {
			System.out.print(o + (os[os.length - 1] != o ? "," : ""));
		}
		System.out.println();
	}

	/**
	 * becuase the comment in content will break out ztl comment , so we filte
	 * it here.
	 * 
	 * @param content
	 */
	private String filterContent(String content) {
		if (content == null)
			return null;

		// here we use a DOTALL mode that make "." can matches \r\n
		return content.replaceAll("(?s)(?m)<\\!--.*?-->", "")
			//filter the pattern because it will cause service say error.
			//<?xml version=\"1.0\" encoding=\"UTF-8\"?>
			.replaceAll("<\\?(xml|taglib).*?\\?>","");

	}

	/**
	 * we will use this to generator a ztl from a zul .
	 * 
	 * @param srcFilePath
	 * @param distFilePath
	 */
	public void generator(String srcFilePath, String distFilePath, HashMap args) {

		if (args == null)
			args = new HashMap();

		
		if(distFilePath.lastIndexOf("\\")!=-1){
			new File(distFilePath.substring(0,distFilePath.lastIndexOf("\\"))).mkdirs();
		}
		
		if (!args.containsKey("fileName"))
			args.put("fileName", new File(distFilePath).getName());

		if (!args.containsKey("tags"))
			args.put("tags", new File(srcFilePath).getName());

		if (!args.containsKey("case"))
			args.put("case", "ztl");

		try {
			System.out.println(srcFilePath);
			System.out.println(distFilePath);
			String content = FileUtils.readFile(srcFilePath);
			args.put("content", filterContent(content));

			VelocityContext context = new VelocityContext();
			context.put("zul", args);
			Template template = null;
			try {
				fillTemplate(distFilePath, context, "zul.vm");
			} catch (ResourceNotFoundException rnfe) {
				log("Example : error : cannot find template ");
			} catch (ParseErrorException pee) {
				log("Example : Syntax error in template:" + pee);
			} catch (Exception pee) {
				log("Error when reading template:" + pee);
			}

		} catch (FileNotFoundException ex) {
			log("ZulGenterator:file not found:" + srcFilePath + ",skip it.");
		}

	}

	/**
	 * fill the context to template , and output to outputFile.
	 * 
	 * @param outputFile
	 * @param context
	 * @param templateName
	 * @throws ResourceNotFoundException
	 * @throws ParseErrorException
	 * @throws FileNotFoundException
	 * @throws Exception
	 */
	public static void fillTemplate(String outputFile, VelocityContext context, String templateName) throws Exception {
		Properties prop = null;
		InputStream in = null;
		try {
			in = ClassLoader.getSystemResourceAsStream("velocity.properties");
			prop = new Properties();
			prop.load(in);
		} finally {
			if (in != null) {
				in.close();
			}
		}
		Velocity.init(prop);
		Template template = Velocity.getTemplate(templateName);


		SimpleDateFormat sdf = new SimpleDateFormat(
				"MM, d, yyyy HH:mm:ss");
		context.put("CreateTime", sdf.format(new Date()));
		sdf = new SimpleDateFormat("yyyy");
		context.put("Year", sdf.format(new Date()));
		
		
		FileWriter writer = new FileWriter(outputFile);

		if (template != null)
			template.merge(context, writer);

		writer.flush();
		writer.close();
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		if (args == null) {
			printUsage();
			return;
		}

		String zulFileName = args[0];
		String ztlFileName = "";
		if (args.length == 1) {
			ztlFileName = zulFileName.replaceAll("\\.zul$", "") + ".ztl";
		} else {
			ztlFileName = args[1];
			File f = new File(ztlFileName);
			if (f.isDirectory()) { // I do some trick for place ztl to a folder
									// , but use zul's name. lol
				ztlFileName +="\\" + new File(zulFileName).getName().replaceAll("\\.zul$", "") + ".ztl";
			}
		}

		ZulGenerator zulgen = new ZulGenerator();
		zulgen.generator(zulFileName, ztlFileName, null);
	}

	private static void printUsage() {
		System.out.println("====usages======");
		System.out.println("<srcZulFileName> <ztlFileName>");
		System.out.println("<srcZulFileName> <existZtlFileFolder>");
		System.out.println("<srcZulFileName>");
	}
}