package com.kichang.util;

import java.util.Properties;

/**
 * @author kcjang
 *
 */
public interface LocalProperty {
	public void setPropName(String propName);
	public void setPropPath(String propPath);
	public Object get(String key);
	public String getTrimValue(String key);
	public Properties getHibernateProp();
}
