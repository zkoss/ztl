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
			String content = "";
			try {
				content = new String(Files.readFile(f));
			} catch (IOException e) {
				e.printStackTrace();
			}
			content = modifyTestCode(content);

			try {
				//write file
				String path = f.getPath().replace(".scala", "$cafe.scala");
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
			//log
			log("statement count: " + count);
			for (int k = 0; k < count; k++) {
				//				log(k + " >>> " + statementContext.getChild(k).getText());
			}
			Map<String, String> replaceMap = listener.getCodeReplacements();
			for (Map.Entry<String, String> entry : replaceMap.entrySet()) {
				code = code.replace(entry.getKey(), entry.getValue());
			}

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
