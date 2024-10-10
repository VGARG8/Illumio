package com.illumio.flowlog.loggers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * The ErrorLogger class is a singleton utility for logging error messages to a specified file.
 *
 * <p>This class provides a thread-safe mechanism to log error messages, ensuring that
 * all logs are appended to the same file. The logger can be initialized with a file path
 * and will create the file if it does not exist.</p>
 */

public class ErrorLogger {

  private static ErrorLogger instance;
  private static String errorFilePath;

  /**
   * Private constructor to initialize the ErrorLogger with the specified file path.
   *
   * @param errorFilePath the file path where error messages will be logged.
   */

  private ErrorLogger(String errorFilePath){
    this.errorFilePath = errorFilePath;
  }

  /**
   * Gets the singleton instance of the ErrorLogger, initializing it with the given file path
   * if it hasn't been initialized yet.
   *
   * @param errorFilePath the file path to log error messages.
   * @return the singleton instance of ErrorLogger.
   */
  public static ErrorLogger getInstance(String errorFilePath){
    if(instance == null){
      instance =  new ErrorLogger(errorFilePath);
    }
    return instance;
  }

  /**
   * Gets the existing instance of the ErrorLogger.
   *
   * @return the singleton instance of ErrorLogger.
   * @throws IllegalStateException if the ErrorLogger has not been initialized.
   */
  public static ErrorLogger getInstance() {
    if (instance == null) {
      throw new IllegalStateException("ErrorLogger has not been initialized.");
    }
    return instance;
  }

  /**
   * Logs an error message to the specified file.
   *
   * @param message the error message to be logged.
   */
  public void logError(String message){
    try {
      File logFile = new File(errorFilePath);
      if(!logFile.exists()){
        logFile.createNewFile();
      }
      try (Writer writer = new FileWriter(errorFilePath, true)) {
        PrintWriter printWriter = new PrintWriter(writer);
        printWriter.println(message);
      }
    }catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


}
