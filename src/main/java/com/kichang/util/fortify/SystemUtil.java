package com.kichang.util.fortify;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

public class SystemUtil {
	private static String localhostName = null;
	private static Properties properties = null;
	public static final String OS_NAME_PROPERTY = "os.name";
	public static final String LINE_SEP = System.getProperty("line.separator");
	private static final boolean isWindows;
	private static final boolean isSolaris;
	private static final boolean isMacos;
	private static final boolean isLinux;
	private static final boolean isAIX;
	private static final boolean isHPUX;
	private static Locale systemDefault;
	private static Locale fortifyLocale;

	public static String getHostName() throws UnknownHostException {
		if (localhostName == null) {
			localhostName = InetAddress.getLocalHost().getHostName();
		}
		return localhostName;
	}

	public static boolean isWindows() {
		return isWindows;
	}

	public static boolean isSolaris() {
		return isSolaris;
	}

	public static boolean isMacos() {
		return isMacos;
	}

	public static boolean isLinux() {
		return isLinux;
	}

	public static boolean isAIX() {
		return isAIX;
	}

	public static boolean isHPUX() {
		return isHPUX;
	}

	public static Locale getSystemDefaultLocale() {
		return systemDefault;
	}

	public static Locale getFortifyLocale() {
		if (fortifyLocale == null) {
			fortifyLocale = determineFortifyLocale();
		}
		return fortifyLocale;
	}

	
	/** @deprecated */
	public static void setupDefaultLocale() {
		systemDefault = Locale.getDefault();
		Locale.setDefault(determineFortifyLocale());
	}

	private static Locale determineFortifyLocale() {
		String installLocaleString = getProperty("com.fortify.locale");
		if (installLocaleString == null)
			return Locale.getDefault();
		StringTokenizer st = new StringTokenizer(installLocaleString, "_");

		String language = st.nextToken();
		Locale locale;
		if (st.hasMoreTokens()) {
			String country = st.nextToken();
			if (st.hasMoreTokens()) {
				String variant = st.nextToken();
				locale = new Locale(language, country, variant);
			} else {
				locale = new Locale(language, country);
			}
		} else {
			locale = new Locale(language, "");
		}
		return locale;
	}

	public static InputStream getLocalResourceAsStream(String resourceName) {
		return getLocalResourceAsStream(resourceName, getFortifyLocale(),
				SystemUtil.class.getClassLoader());
	}

	public static InputStream getLocalResourceAsStream(String resourceName,
			Locale locale) {
		return getLocalResourceAsStream(resourceName, locale,
				SystemUtil.class.getClassLoader());
	}

	public static InputStream getLocalResourceAsStream(String resourceName,
			Locale locale, ClassLoader cl) {
		InputStream result = cl.getResourceAsStream(resourceName
				+ getLocaleSuffix(locale));
		if (result != null) {
			return result;
		}
		return cl.getResourceAsStream(resourceName);
	}

	public static String getLocaleString(Locale locale) {
		String language = locale.getLanguage();
		String country = locale.getCountry();
		String variant = locale.getVariant();

		if ((language != null) && (language.length() > 0)) {
			if ((country != null) && (country.length() > 0)) {
				if ((variant != null) && (variant.length() > 0)) {
					return language + "_" + country + "_" + variant;
				}
				return language + "_" + country;
			}
			return language;
		}
		return "";
	}

	public static String getLocaleSuffix(Locale locale) {
		String localeString = getLocaleString(locale);
		if ("".equals(localeString)) {
			return "";
		}
		return "_" + localeString;
	}

	public static String getLocaleSuffix() {
		String localeString = getLocaleString(getFortifyLocale());
		if ("".equals(localeString)) {
			return "";
		}
		return "_" + localeString;
	}

	public static Properties getProperties() {
		if (properties == null) {
			loadCommonProperties();
		}
		return properties;
	}

	public static String getProperty(String key) {
		return getProperties().getProperty(key);
	}

	public static void setInstallRoot() {
		URL selfUrl = SystemUtil.class.getProtectionDomain().getCodeSource()
				.getLocation();
		try {
			File installDir = new File(selfUrl.toURI()).getParentFile()
					.getParentFile().getParentFile();
			System.setProperty("com.fortify.InstallRoot", installDir.getPath());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private static void loadCommonProperties() {
		properties = new Properties(System.getProperties());

		String installRoot = System.getProperty("com.fortify.InstallRoot");
		if (installRoot == null) {
			throw new IllegalStateException("com.fortify.InstallRoot not set");
		}

		String core = System.getProperty("com.fortify.Core");
		if (core == null) {
			core = new File(installRoot, "Core").getAbsolutePath();
			properties.setProperty("com.fortify.Core", core);
		}
		try {
			loadPropertiesFromFile(properties, new File(core,
					"config/fortify.properties"), false);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		File userPropFile = new File(System.getProperty("user.home"),
				(isWindows()) ? "fortify.properties" : ".fortify.properties");
		if (!(userPropFile.exists()))
			return;
		try {
			loadPropertiesFromFile(properties, userPropFile, true);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/** @deprecated */
	public static void loadProperties(Properties existing, String bundleName) {
		loadPropertiesFromResource(existing, bundleName, false);
	}

	public static void loadPropertiesFromResource(Properties existing,
			String bundleName, boolean replace) {
		ResourceBundle res = ResourceBundle.getBundle(bundleName);
		Enumeration keys = res.getKeys();

		Properties newProps = new Properties();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			newProps.setProperty(key, res.getString(key));
		}
		mergeProperties(existing, newProps, replace);
	}

	/** @deprecated */
	public static void loadProperties(Properties existing, File file)
			throws IOException {
		loadPropertiesFromFile(existing, file, true);
	}

	public static void loadPropertiesFromFile(Properties existing, File file,
			boolean replace) throws IOException {
		Properties newProps = new Properties();
		FileInputStream inStream = null;
		try {
			inStream = new FileInputStream(file);
			newProps.load(inStream);
			mergeProperties(existing, newProps, replace);
		} finally {
			if (inStream != null)
				inStream.close();
		}
	}

	private static void mergeProperties(Properties existing,
			Properties toMerge, boolean replace) {
		for (Iterator iter = toMerge.keySet().iterator(); iter.hasNext();) {
			String propKey = (String) iter.next();
			if ((replace) || (!(existing.containsKey(propKey)))) {
				String propVal = replaceVarsInProp(
						toMerge.getProperty(propKey), (replace) ? toMerge
								: existing, (replace) ? existing : toMerge);

				existing.setProperty(propKey, propVal);
			}
		}
	}

	public static String replaceVarsInProp(String prop, Properties primary,
			Properties secondary) {
		return replaceVarsInProp(prop, primary, secondary, 0);
	}

	private static String replaceVarsInProp(String prop, Properties primary,
			Properties secondary, int recurse) {
		if (recurse > 5) {
			return prop;
		}

		StringBuffer eb = new StringBuffer();
		char[] chars = prop.toCharArray();
		int seq = 0;
		int keyStart = -1;
		for (int i = 1; i < chars.length; ++i) {
			if (keyStart < 0) {
				if ((chars[(i - 1)] == '$') && (chars[i] == '{'))
					keyStart = i + 1;
			} else {
				if (chars[i] != '}')
					continue;
				eb.append(chars, seq, keyStart - 2 - seq);

				String propKey = String.valueOf(chars, keyStart, i - keyStart);
				String propVal = primary.getProperty(propKey);

				if ((propVal == null) || (propVal == prop)) {
					propVal = secondary.getProperty(propKey);
				}
				if (propVal == null) {
					int defaultIndex = propKey.indexOf(":-");
					if (defaultIndex != -1)
						eb.append(propKey.substring(defaultIndex + 2));
					else
						eb.append("${").append(propKey).append("}");
				} else {
					eb.append(replaceVarsInProp(propVal, primary, secondary,
							recurse + 1));
				}
				seq = i + 1;
				keyStart = -1;
			}
		}

		if (seq < chars.length) {
			eb.append(chars, seq, chars.length - seq);
		}
		return eb.toString();
	}


	public static void reassessVariableSubstitution() {
		for (Iterator iterator = properties.keySet().iterator(); iterator
				.hasNext();) {
			String key = (String) iterator.next();
			String oldVal = properties.getProperty(key);
			String newVal = replaceVarsInProp(oldVal, properties, properties);
			if (!(oldVal.equals(newVal)))
				properties.setProperty(key, newVal);
		}
	}

	public static String getProperty(String propertyName, String defaultValue) {
		String ret = getProperty(propertyName);
		if (ret == null)
			ret = defaultValue;
		return ret;
	}

	public static boolean getBooleanProperty(String propertyName) {
		String ret = getProperty(propertyName);
		if (ret == null)
			return false;
		return "true".equalsIgnoreCase(ret);
	}

	public static Integer getIntegerProperty(String propertyName) {
		String ret = getProperty(propertyName);
		if (ret == null)
			return null;
		try {
			return Integer.valueOf(ret);
		} catch (NumberFormatException nfe) {
		}
		return null;
	}

	public static int getMaxFileLength() {
		Integer maxLength = getIntegerProperty("max.file.path.length");
		if (maxLength == null) {
			return 255;
		}
		return maxLength.intValue();
	}

	static {
		String osname = System.getProperty("os.name").toLowerCase();
		isWindows = osname.indexOf("windows") != -1;
		isSolaris = (osname.indexOf("sunos") != -1)
				|| (osname.indexOf("solaris") != -1);
		isMacos = osname.indexOf("mac") != -1;
		isLinux = osname.indexOf("linux") != -1;
		isAIX = osname.indexOf("aix") != -1;
		isHPUX = osname.indexOf("hp-ux") != -1;

		systemDefault = Locale.getDefault();
	}
}