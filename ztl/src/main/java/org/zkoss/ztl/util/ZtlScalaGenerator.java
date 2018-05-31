package org.zkoss.ztl.util;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.zkoss.ztl.parser.SAXParser;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * The ZTL generator that generates the scala class from ZTL file to Selenium test case.
 * <p>
 * Usage:
 * <ol>
 * <li>
 * Generate the scala class. <br>
 * java org.zkoss.ztl.util.ZtlScalaGenerator -src ./test -dest ./codegen
 * </li>
 *</ol>
 * 
 * @author jameschu
 */
public class ZtlScalaGenerator {
	private static void log(Object... os) {
		for (Object o : os) {
			System.out.print(o + (os[os.length - 1] != o ? "," : ""));
		}
		System.out.println();
	}

	private void run(Test t, String dist) {
		if (t == null) {
			log("Test cannot be null!");
			return;
		}
		try {
			final String destdir = dist.replace("/", File.separator);
			InputStream in = null;
			Properties prop = null;
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

			String PackageName = t.getPackage();
			String FileName = t.getFileName();
			String path = destdir + File.separator + PackageName.replace(".", File.separator) + File.separator
					+ FileName + ".scala"; //java

			File dir = new File(path);
			dir.getParentFile().mkdirs();
			if (dir.isFile()) {
				if (dir.lastModified() > t.lastModified())
					return;
			} else {
				dir.createNewFile();
			}

			VelocityContext context = new VelocityContext();
			context.put("ztl", t);

			SimpleDateFormat sdf = new SimpleDateFormat("MMM, d, yyyy HH:mm:ss a");
			context.put("CreateTime", sdf.format(new Date()));
			sdf = new SimpleDateFormat("yyyy");
			context.put("Year", sdf.format(new Date()));

			Template template = null;

			try {
				template = Velocity.getTemplate("ztlscala.vm"); //ztlscala.vm
			} catch (ResourceNotFoundException rnfe) {
				log("Example : error : cannot find template ");
			} catch (ParseErrorException pee) {
				log("Example : Syntax error in template:" + pee);
			}

			FileWriter writer = new FileWriter(dir);

			if (template != null)
				template.merge(context, writer);

			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Test load(File f, String src) {
		try {
			String fileName = f.getName();
			fileName = fileName.substring(0, fileName.indexOf("."));
			fileName = fileName.replace("-", "_");
			String pkg = f.getParent();
			int index = pkg.indexOf("org");
			pkg = pkg.substring(index).replace(File.separator, ".");
			ConfigHelper ch = ConfigHelper.getInstance();

			/** config filed ***/
			Test test;
			try {
				test = SAXParser.parser(f, true);
			} catch (Exception e) {
				System.out.println("fail to generate when parsing [" + f.getAbsolutePath() + "]");
				throw e;
			}

			// if config.properties is changed, we need to regenerate Java.
			test.setLastModified(f.lastModified() < ch.lastModified() ? ch.lastModified() : f.lastModified());
			test.setFileName(fileName);
			test.setServer(ch.getServer());
			test.setPackage(pkg);
			test.setContextPath(ch.getContextPath());
			test.setAction(ch.getAction());
			test.setDelay(ch.getDelay());
			test.setTimeout(ch.getTimeout());
			test.setBrowser(ch.getBrowser());

			return test;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		String src = null, dist = null;
		int i = 0;
		for (String s : args) {
			if ("-src".equalsIgnoreCase(s)) {
				src = args[i + 1];
				log("src=" + src);
			} else if ("-dist".equalsIgnoreCase(s)) {
				dist = args[i + 1];
				log("dist=" + dist);
			}
			i++;
		}

		if (src == null && dist == null) {
			log("\n");
			log("Usage:\n");
			log("Generate the ztl scala class\n");
			log("	java org.zkoss.ztl.util.ZtlGenerator -src ./test -dist ./codegen");
			log("\n");
			System.exit(-1);
		}
		ZtlScalaGenerator t = new ZtlScalaGenerator();
		File dir = new File(src);
		for (File f : getFiles(dir, new ArrayList<File>(30), ".ztl")) {
			// ignore test case
			if (ConfigHelper.getInstance().isAllIgnoreCase(f.getName())) {
				System.out.println("ignore: " + f.getName());
				continue;
			}
//			if (f.delete()) {
//				System.out.println(f.getName() + " is deleted!");
//			} else {
//				System.out.println("Delete operation is failed.");
//			}
			Test test = t.load(f, dir.getPath());
			t.run(test, dist);
		}
	}
}