package com.automation.config;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.Properties;
 /**
 * Configuration Manager for managing application properties.
 * Implements singleton pattern for single instance.
 * 
 * @author api-automation-framework
 * @version 1.0.0
 */
 public class ConfigurationManager {
 private static ConfigurationManager instance;
 private Properties properties;
 private ConfigurationManager() {
 this.properties = new Properties();
 loadProperties();
 }
 public static synchronized ConfigurationManager getInstance() {
 if (instance == null) {
 instance = new ConfigurationManager();
 }
 return instance;
 }
 private void loadProperties() {
 try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
 if (input != null) {
 properties.load(input);
 }
 } catch (IOException e) {
 e.printStackTrace();
 }
 }
 public String getProperty(String key) {
 return properties.getProperty(key);
 }
 public String getProperty(String key, String defaultValue) {
 return properties.getProperty(key, defaultValue);
 }
 }