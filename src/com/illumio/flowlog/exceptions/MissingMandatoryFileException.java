package com.illumio.flowlog.exceptions;

/**
 * Exception thrown when a mandatory file is missing or cannot be accessed.
 */
public class MissingMandatoryFileException extends Throwable {

  public MissingMandatoryFileException(String s) {
    super(s);
  }
}
