/* ConfigHelper.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep 16 12:49:43 TST 2009, Created by sam

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
 */
package org.zkoss.ztl.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.android.AndroidDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.iphone.IPhoneDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.zkoss.ztl.webdriver.ZKRemoteWebDriver;
import org.zkoss.ztl.webdriver.ZKWebDriverCommandProcessor;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.opera.core.systems.OperaDriver;
import com.thoughtworks.selenium.Selenium;

/**
 * ZTL configuration helper.
 * 
 * @author sam
 * @author jumperchen
 * 
 */
public class ConfigHelper {

	private final static String[] SUPPORTED_BROWSER = { "firefox", "chrome",
			"opera", "ie", "htmlunit", "iexplore", "ff", "iphone", "android",
			"safari" };

	private final static String ALL_BROWSERS = "all";

	private static List<String> _allBrowsers = new LinkedList<String>();

	private String _client;

	private String _server;

	private String _contextPath;

	private String _action;

	private String _timeout;

	private String _delay;

	private String _browser;

	private Properties _prop;

	private long _lastModified;
	
	private boolean _openonce = false;
	
	private boolean _debuggable;
	
	// 2011-03-02. Edited by Phoenix.
	// Add properties for image comparing.
	private String _imgsrc;
	private String _imgdest;
	private boolean _comparable;
	private int _granularity, _leniency;

	private static ConfigHelper ch = new ConfigHelper();

	private HashMap<String, String> _driverSetting;

	private HashMap<String, String> _browserRemote;
	
	private HashMap<String, List<String>> _ignoreMap;
	
	private volatile boolean _inited;

	/**
	 * key : Firefox, IE ... value : Selenium browser
	 */
	private HashMap<String, Selenium> _browserHolder = new HashMap<String, Selenium>();

	private ConfigHelper() {
	}

	public static ConfigHelper getInstance() {
		try {
			if (!ch._inited) {
				ch.init();
				ch._inited = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ch;
	}

	public boolean isDebuggable() {
		return _debuggable;
	}
	
	public String getClient() {
		return _client;
	}

	public String getServer() {
		return _server;
	}

	public String getContextPath() {
		return _contextPath;
	}

	public String getAction() {
		return _action;
	}

	public String getDelay() {
		return _delay;
	}

	public String getTimeout() {
		return _timeout;
	}

	public String getBrowser() {
		return _browser;
	}

	public long lastModified() {
		return _lastModified;
	}
	
	public boolean isAllIgnoreCase(String fileName) {
		List<String> ignoreList = _ignoreMap.get(ALL_BROWSERS);
		if (ignoreList == null)
			return false;
		return ignoreList.contains(fileName);
	}
	public boolean isIgnoreCase(String key, String fileName) {
		if (isAllIgnoreCase(fileName))
			return true;
		
		for (Map.Entry<String, List<String>> me : _ignoreMap.entrySet()) {
			String key2 = me.getKey();
			if (key.matches("^" + key2 + "$")) {
				List<String> ignoreList = me.getValue();
				if (ignoreList != null) {
					if (ignoreList.contains(fileName)) {
						System.out.println("runtime-ignore: " + key + "=" + key2);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private WebDriver getWebDriver(String key, String remotePath) {
		try {
			if (remotePath != null) {
				if ("firefoxdriver".equalsIgnoreCase(key)) {
					return new ZKRemoteWebDriver(new URL(remotePath),
							DesiredCapabilities.firefox());
				} else if ("chromedriver".equalsIgnoreCase(key)) {
					return new ZKRemoteWebDriver(new URL(remotePath),
							DesiredCapabilities.chrome());
				} else if ("internetexplorerdriver".equalsIgnoreCase(key)) {
					return new ZKRemoteWebDriver(new URL(remotePath),
							DesiredCapabilities.internetExplorer());
				} else if ("operadriver".equalsIgnoreCase(key)) {
					return new ZKRemoteWebDriver(new URL(remotePath),
							DesiredCapabilities.opera());
				} else if ("androiddriver".equalsIgnoreCase(key)) {
					return new ZKRemoteWebDriver(new URL(remotePath),
							DesiredCapabilities.android());
				} else if ("iphonedriver".equalsIgnoreCase(key)) {
					return new ZKRemoteWebDriver(new URL(remotePath),
							DesiredCapabilities.iphone());
				}
			} else {
				if ("firefoxdriver".equalsIgnoreCase(key)) {
					return new FirefoxDriver();
				} else if ("chromedriver".equalsIgnoreCase(key)) {
					return new ChromeDriver();
				} else if ("internetexplorerdriver".equalsIgnoreCase(key)) {
					return new InternetExplorerDriver();
				} else if ("operadriver".equalsIgnoreCase(key)) {
					return new OperaDriver();
				} else if ("androiddriver".equalsIgnoreCase(key)) {
					return new AndroidDriver();
				} else if ("iphonedriver".equalsIgnoreCase(key)) {
					return new IPhoneDriver();
				} else if ("htmlunitdriver".equalsIgnoreCase(key)) {
					return new HtmlUnitDriver(true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException("Unsopported arguments [" + key + "]");
	}
	
	/**
	 * 
	 * @param key
	 * @return value :
	 */
	private Selenium getBrowserFromHolder(String key) {
		key = key.toLowerCase();
		System.out.println("connecting "+key);
		if (_driverSetting.get(key) == null)
			throw new NullPointerException("Null Browser Type String");

		WebDriver driver = getWebDriver( _driverSetting.get(key), _browserRemote.get(key));
		// TODO : SB : Replace URL with one from configuration
		Selenium browser = new ZKSelenium(new ZKWebDriverCommandProcessor(_contextPath, driver), key,_openonce);
		browser.setSpeed(getDelay());
		return browser;
	}
	public List<Selenium> getBrowsersForLazy(String keys, String caseName) {
		final List<String> browser = new ArrayList<String>(Arrays.asList(keys.split(","))); //$NON-NLS-1$
		if (browser.contains(ALL_BROWSERS)) {
			browser.remove(ALL_BROWSERS);
			browser.addAll(_allBrowsers);
		}
		for (Iterator<String> it = browser.iterator(); it.hasNext();) {
			if (isIgnoreCase(it.next(), caseName))
				it.remove();
		}
		List<Selenium> list = new AbstractSequentialList<Selenium>() {
			@Override
			public ListIterator<Selenium> listIterator(int index) {
				return new ItemIter(index, browser);
			}

			@Override
			public int size() {
				// TODO Auto-generated method stub
				return browser.size();
			}
		};
		return list;
	}
	private class ItemIter implements ListIterator<Selenium> {
		private int _j;
		private List<String> _browser;

		private ItemIter(int index, List<String> browser) {
			_j = index;
			_browser = browser;
		}

		@Override
		public boolean hasNext() {
			return _j < _browser.size();
		}

		@Override
		public Selenium next() {
			if (!hasNext())
				throw new NoSuchElementException();
			return getBrowserFromHolder(_browser.get(_j++));
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Readonly!");
		}

		@Override
		public boolean hasPrevious() {
			return _j > 0;
		}

		@Override
		public Selenium previous() {
			if (!hasPrevious())
				throw new NoSuchElementException();
			return getBrowserFromHolder(_browser.get(--_j));
		}

		@Override
		public int nextIndex() {
			return _j;
		}

		@Override
		public int previousIndex() {
			return _j - 1;
		}

		@Override
		public void set(Selenium e) {
			throw new UnsupportedOperationException("Readonly!");
		}

		@Override
		public void add(Selenium e) {
			throw new UnsupportedOperationException("Readonly!");
		}
	}
	/**
	 * @deprecated as of release ZTL 2.0.0 version
	 * @see #getBrowsersForLazy
	 */
	@Deprecated
	public List<Selenium> getBrowsers(String keys) {
		return getBrowsersForLazy(keys, null);
	}

	private void init() throws IOException, Exception {
		if (_driverSetting == null) {
			_driverSetting = new HashMap<String, String>();
		}
		if (_browserRemote == null) {
			_browserRemote = new HashMap<String, String>();
		}
		if (_ignoreMap == null) {
			_ignoreMap = new HashMap<String, List<String>>();
		}
		initProperty();
		initIgnoreList();
	}

	private void initIgnoreList() {
		DataInputStream in = null;
		try {
			String executionPath = System.getProperty("user.dir");
			File ignore = new File(executionPath + File.separator
					+ "ztl.ignore");
			FileInputStream fstream = new FileInputStream(ignore);
			in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(
					new InputStreamReader(in));
			String strLine;
			
			String key = null;
			List<String> list = new ArrayList<String>();
			boolean commented = false;
			while ((strLine = br.readLine()) != null) {
				strLine = strLine.trim();
				if (commented) {
					if (strLine.startsWith("*/")) {
						commented = false;
						continue;
					}
					continue;
				}
				if (strLine.startsWith("/*")) {
					commented = true;
					continue;
				}
				if (strLine.isEmpty() || strLine.startsWith("#") || strLine.startsWith("//"))
					continue;
				
				int keyIndex = strLine.indexOf("={");
				if (keyIndex > -1) {
					if (key != null) {
						throw new IllegalArgumentException("The file format is wrong! No end '}' was found! ["+ key+"]");
					}
					key = strLine.substring(0, keyIndex);
					continue;
				}
				
				if (strLine.startsWith("}")) {
					if (key == null) {
						throw new IllegalArgumentException("The file format is wrong! No key was found!");
					}
					String[] keys = key.split(",");
					for (String k: keys) {
						k = k.replaceAll("\\*", ".*");
						//System.err.println("put: " + k +"=" + list);
						if (_ignoreMap.containsKey(k)) {
							_ignoreMap.get(k).addAll(list);
						} else
							_ignoreMap.put(k, list);
					}
					list = new ArrayList<String>();
					key = null;
					continue;
				} else {
					//System.err.println("ignore: " + key +"=" + strLine);
					list.add(strLine);
				}
			}
		} catch (Exception e) {
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	private void addBrowserNameSetting(String browserName, String browserPath) {
		browserName = browserName.toLowerCase();
		String setting = browserPath;
		
		// for remote driver
		if (browserPath.indexOf(";") != -1) {
			String[] tokens = browserPath.split(";");
			_browserRemote.put(browserName, tokens[0]);
			if(tokens.length >1){
				browserPath = tokens[1];
			}else{
				browserPath = "";
			}
			setting = browserPath;
		}
		_driverSetting.put(browserName, setting);
	}

	@SuppressWarnings("unchecked")
	private void initProperty() throws Exception, IOException {
		InputStream in = null;
		if (_prop == null) {
			try {
				String executionPath = System.getProperty("user.dir");
				File f = new File(executionPath + File.separator + "config.properties");
				if (!f.isFile()) {
					in = ClassLoader.getSystemResourceAsStream("config.properties");
					if (in == null)
						throw new FileNotFoundException(f.toString());
					f = new File(ClassLoader.getSystemResource("config.properties").getFile());
				} else
					in = new FileInputStream(f);
					
				_prop = new Properties();
				_prop.load(in);
//				_openonce = Boolean.parseBoolean(_prop.getProperty("openonce","false"));
//				System.out.println("openonce="+_openonce);
				_lastModified = f.lastModified();
//				_client = _prop.getProperty("client");
				_debuggable = Boolean.parseBoolean(_prop.getProperty("debuggable"));
				_server = _prop.getProperty("server");
				_contextPath = _prop.getProperty("context-path");
				_action = _prop.getProperty("action");
				_delay = _prop.getProperty("delay");
				_browser = _prop.getProperty("browser");
				_timeout = _prop.getProperty("timeout");
				_imgsrc = _prop.getProperty("imgsrc");
				_imgdest = _prop.getProperty("imgdest");
				_comparable = Boolean.parseBoolean(_prop.getProperty("comparable", "false"));
				_granularity = Integer.parseInt(_prop.getProperty("granularity"));
				_leniency = Integer.parseInt(_prop.getProperty("leniency"));
				for (Iterator iter = _prop.entrySet().iterator(); iter.hasNext();) {
					final Map.Entry setting = (Map.Entry) iter.next();
					String strKey = (String) setting.getKey();
					if (isBrowserSetting(strKey)) {
						addBrowserNameSetting(strKey, (String) setting.getValue());
						continue;
					}
				}

				String[] allBrowsers = _prop.getProperty(ALL_BROWSERS).split(",");
				for (String browser : allBrowsers) {
					String browserKey = browser.trim();
					if (_driverSetting.containsKey(browserKey)) {
						_allBrowsers.add(browserKey);
					}
				}
				
				// System Properties
				String sysprop = _prop.getProperty("systemproperties");
				if (sysprop != null) {
					String[] keys = sysprop.split(";");
					for (String key : keys) {
						int start = key.indexOf(":");
						if (start < 0) {
							System.err.println("The syntax of the system property is wrong! [" + key + "]");
							continue;
						}	
						System.setProperty(key.substring(0, start), key.substring(start+1));
					}
				}
			} finally {
				if (in != null) {
					in.close();
				}
			}
		}
	}

	private boolean isBrowserSetting(String str) {
		for (String browserStr : SUPPORTED_BROWSER) {
			if (str.toLowerCase().startsWith(browserStr))
				return true;
		}
		return false;
	}

	/**
	 * TODO Logging untested yet
	 */
	protected static final String RESULT_FILE_ENCODING = "UTF-8";

	protected static final String SCREENSHOT_PATH = "screenshots";

	protected static final String RESULTS_BASE_PATH = "Log";

	protected String screenshotsResultsPath;

    /**
     * Returns the path of the image source directory.
     * <p>
     * Property name in config.properties: <b>imgsrc</b>, which means the source
     * directory of the base image.
     */
	public String getImageSrc() {
        return _imgsrc;
    }

    /**
     * Returns the path of the image destination directory.
     * <p>
     * Property name in config.properties: <b>imgdest</b>, which means the destination
     * directory of the compared result, if fails.
     */
	public String getImageDest() {
        return _imgdest;
    }

    /**
     * Returns whether in a comparable mode.
     * <p>default: false.
     * Property name in config.properties: <b>comparable</b>, which means the image
     * comparison is in a comparable mode, if true specified. Otherwise, the image
     * is stored to the imgsrc directory as the base images.
     * 
     */
	public boolean isComparable() {
        return _comparable;
    }
	
	/**
	 * Returns the granularity for each comparing section.
	 * <p>
     * Property name in config.properties: <b>granularity</b>
	 * <p> It is better to have 1~15, less is a precise comparison, but performance
	 * is slow. Don't specify too high, it may compare without any different.
	 */
	public int getGranularity() {
		return _granularity;
	}
	
	/**
	 * Returns the leniency for each comparing section.
	 * <p>
     * Property name in config.properties: <b>leniency</b>
	 * <p> It is better to have 1~10, less is a precise comparison.
	 */
	public int getLeniency() {
		return _leniency;
	}

}
