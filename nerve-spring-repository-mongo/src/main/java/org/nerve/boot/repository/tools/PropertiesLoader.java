/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.nerve.boot.repository.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Properties;

/**
 * Properties文件载入工具类. 可载入多个properties文件, 相同的属性在最后载入的文件中的值将会覆盖之前的值.
 */
public class PropertiesLoader {

    private static Logger logger = LoggerFactory.getLogger(PropertiesLoader.class);

    private static ResourceLoader resourceLoader = new DefaultResourceLoader();

    private final Properties properties;

    public PropertiesLoader(String... resourcesPaths) {
        properties = loadProperties(resourcesPaths);
    }

    public Properties getProperties() {
        return properties;
    }

    private String getValue(String key) {
        String systemProperty = System.getProperty(key);
        if (systemProperty != null) {
            return systemProperty;
        }
        return properties.getProperty(key);
    }

    /**
     * 取出String类型的Property，但以System的Property优先.如果都為Null則返回Default值，如果内容错误则抛出异常
     * @param key               key
     * @return                  String
     */
    public String getProperty(String key) {
        String value = getValue(key);
        if (value == null) {
            throw new NoSuchElementException();
        }
        return value;
    }

    /**
     * 取出String类型的Property，但以System的Property优先.如果都為Null則返回Default值，如果内容错误则抛出异常
     * @param key               key
     * @param defaultValue      默认值
     * @return                  String
     */
    public String getProperty(String key, String defaultValue) {
        String value = getValue(key);
        return value != null ? value : defaultValue;
    }

    /**
     * 取出Integer类型的Property，但以System的Property优先.如果都為Null則返回Default值，如果内容错误则抛出异常
     * @param key               key
     * @return                  Integer
     */
    public Integer getInteger(String key) {
        String value = getValue(key);
        if (value == null) {
            throw new NoSuchElementException();
        }
        return Integer.valueOf(value);
    }

    /**
     * 取出Integer类型的Property，但以System的Property优先.如果都為Null則返回Default值，如果内容错误则抛出异常
     * @param key               key
     * @param defaultValue      默认值
     * @return                  Integer
     */
    public Integer getInteger(String key, Integer defaultValue) {
        String value = getValue(key);
        return value != null ? Integer.valueOf(value) : defaultValue;
    }

    /**
     * 取出Double类型的Property，但以System的Property优先.如果都為Null則返回Default值，如果内容错误则抛出异常
     * @param key               key
     * @return                  Double
     */
    public Double getDouble(String key) {
        String value = getValue(key);
        if (value == null) {
            throw new NoSuchElementException();
        }
        return Double.valueOf(value);
    }

    /**
     * 取出Double类型的Property，但以System的Property优先.如果都為Null則返回Default值，如果内容错误则抛出异常
     * @param key               key
     * @param defaultValue      默认值
     * @return                  Double
     */
    public Double getDouble(String key, Integer defaultValue) {
        String value = getValue(key);
        return value != null ? Double.valueOf(value) : defaultValue;
    }

    /**
     * 取出Boolean类型的Property，但以System的Property优先.如果都為Null則返回Default值,如果内容不为true/false则返回false.
     * @param key               key
     * @return                  boolean
     */
    public Boolean getBoolean(String key) {
        String value = getValue(key);
        if (value == null) {
            throw new NoSuchElementException();
        }
        return Boolean.valueOf(value);
    }

	/**
	 * 取出Boolean类型的Property，但以System的Property优先.如果都為Null則返回Default值,如果内容不为true/false则返回false.
     * @param key               key
     * @param defaultValue      默认值
     * @return                  boolean
     */
    public Boolean getBoolean(String key, boolean defaultValue) {
        String value = getValue(key);
        return value != null ? Boolean.valueOf(value) : defaultValue;
    }

    /**
     * 写入properties信息
     *
     * @param resourcesPath 配置文件路径
     * @param key           名称
     * @param value         值
     */
    public void modifyProperties(String resourcesPath, String key, String value) {
        try {
            // 从输入流中读取属性列表（键和元素对）
            properties.setProperty(key, value);
            FileOutputStream outputFile = new FileOutputStream(resourcesPath);
            properties.store(outputFile, "modify");
            outputFile.close();
            outputFile.flush();
        } catch (Exception e) {
        }
    }

	/**
	 * 载入多个properties文件, 相同的属性在最后载入的文件中的值将会覆盖之前的载入.
     * 文件路径使用Spring Resource格式, 文件编码使用UTF-8.
     * @see org.springframework.beans.factory.config.PropertyPlaceholderConfigurer
     *
     * @param resourcesPaths    文件路径，可以是多个
     * @return  Properties
     */
    private Properties loadProperties(String... resourcesPaths) {
        Properties props = new Properties();

        for (String location : resourcesPaths) {

            logger.debug("Loading properties file from:" + location);

            InputStream is = null;
            try {
                Resource resource = resourceLoader.getResource(location);
                is = resource.getInputStream();
                props.load(is);
            } catch (IOException ex) {
                logger.info("Could not load properties from path:" + location + ", " + ex.getMessage());
            } finally {
                if(is!=null)
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
        return props;
    }
}