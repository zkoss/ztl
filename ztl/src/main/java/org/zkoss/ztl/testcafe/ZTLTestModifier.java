/* ZTLTest.java
	Purpose:

	Description:

	History:
		Tue Jun 05 15:51:12 CST 2018, Created by jameschu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.ztl.testcafe;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.testng.reporters.Files;
import org.zkoss.ztl.grammar.ZTLScalaDefaultListener;
import org.zkoss.ztl.grammar.ZTLScalaLexer;
import org.zkoss.ztl.grammar.ZTLScalaParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author jameschu
 */
public class ZTLTestModifier {
	private static void log(Object... os) {
		for (Object o : os) {
			System.out.print(o + (os[os.length - 1] != o ? "," : ""));
		}
		System.out.println();
	}

	public static void main(String[] args) {
		String src = null;
		int i = 0;
		for (String s : args) {
			if ("-src".equalsIgnoreCase(s)) {
				src = args[i + 1];
				log("src=" + src);
			}
			i++;
		}
		File dir = new File(src);
		for (File f : getFiles(dir, new ArrayList<File>(30), ".scala")) {
			if (f.getName().contains("TestCafe")) // skip xxx$cafe.scala
				continue;
			String content = "";
			try {
				content = new String(Files.readFile(f));
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (content.contains("@SeleniumOnly"))
				continue;
			content = modifyTestCode(content);

			try {
				//write file
				String path = f.getPath().replace(".scala", "Cafe.scala");
				File destDir = new File(path);
				destDir.getParentFile().mkdirs();
				if (!destDir.isFile()) {
					destDir.createNewFile();
				}
				FileWriter writer = new FileWriter(destDir);
				writer.write(content);
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void generate(File f, String src, String targetDir) {
		if (f.getName().contains("TestCafe")) {
			System.out.println("Skip *TestCafe.scala : " + f.getPath());
			return;
		}
		String content = "";
		try {
			content = new String(Files.readFile(f));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (content.contains("@SeleniumOnly")) {
			System.out.println("Selenium-only case skipped (" + f.getPath() + ")");
			return;
		}
		System.out.println("Modifying: " + f.getPath());
		content = modifyTestCode(content);

		try {
			String pkg = f.getParent();
			int index = pkg.indexOf(src);
			if (index == 0)
				pkg = pkg.substring(src.length());
			if (pkg.length() > 0)
				pkg = pkg.replace(File.separator, ".").substring(1);
			//write file
			String path = targetDir + File.separator
					+ pkg.replace(".", File.separator) + File.separator + f.getName().replace(".scala", "Cafe.scala");
			File destDir = new File(path);
			destDir.getParentFile().mkdirs();
			if (!destDir.isFile()) {
				destDir.createNewFile();
			}
			FileWriter writer = new FileWriter(destDir);
			writer.write(content);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String modifyTestCode(String code) {
		StringBuilder codeStrBuilder = new StringBuilder();
		String clzStartString = "\nclass ";
		int index = code.indexOf(clzStartString);
		if (index == -1) {
			log("invalid test code");
			System.exit(-1);
		}
		String pre = code.substring(0, index);
		codeStrBuilder.append(pre);
		code = code.substring(index);

		// equals, contains
		InputStream stream = new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8));
		Lexer lexer = null;
		try {
			lexer = new ZTLScalaLexer(CharStreams.fromStream(stream));
			ZTLScalaParser parser = new ZTLScalaParser(new CommonTokenStream(lexer));
			ZTLScalaDefaultListener listener = new ZTLScalaDefaultListener();
			parser.addParseListener(listener);
			//Parsing
			ZTLScalaParser.StatementsContext statementContext = parser.statements();
			int count = statementContext.getChildCount();
			Map<String, String> preReplaceMap = listener.getPreCodeReplacements();
			for (Map.Entry<String, String> entry : preReplaceMap.entrySet()) {
				code = code.replace(entry.getKey(), entry.getValue());
			}
			List<String[]> replacements = listener.getCodeReplacements();
			StringBuilder codeContent = new StringBuilder();
			int replaceIndex = -1;
			String[] replacement = null;
			for (String line: code.split("\n")) {
				if (replacement == null) {
					replaceIndex++;
					replacement = replacements.get(replaceIndex);
				}
				String oldLine = line;
				line = line.replace(replacement[0], replacement[1]);
				if (!oldLine.equals(line)) {
					replacement = null;
				}
				codeContent.append(line).append("\n");
			}
			code = codeContent.toString();
			System.out.println("Done.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pre + code;
	}

	private static List<File> getFiles(File dir, List<File> list, String fileType) {
		if (dir.isDirectory()) {
			for (File f : dir.listFiles()) {
				getFiles(f, list, fileType);
			}
		} else if (dir.getName().endsWith(fileType) && !dir.getName().equals(fileType))
			list.add(dir);
		return list;
	}
}
