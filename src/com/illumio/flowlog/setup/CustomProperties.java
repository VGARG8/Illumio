package com.illumio.flowlog.setup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * CustomProperties is an extension of the  Properties class that automatically
 * loads properties from a configuration file upon instantiation.
 *
 * <p>
 * By default, the properties file path is set to "config.properties". If the file is
 * not found or cannot be loaded, a RuntimeException is thrown.
 * </p>
 */

public class CustomProperties extends Properties {
  Logger logger = Logger.getLogger(CustomProperties.class.getName());
  String path = "config.properties";
  public CustomProperties() {
    try {
      load(Files.newInputStream(Paths.get(path)));
      logger.info("Properties file loaded successfully");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
