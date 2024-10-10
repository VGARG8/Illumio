package com.illumio.flowlog.exceptions;

/**
 * Exception thrown when a file is missing or cannot be accessed.
 */
public class MissingFileException extends Throwable {
  public MissingFileException(String s) {
    super(s);
  }
}
