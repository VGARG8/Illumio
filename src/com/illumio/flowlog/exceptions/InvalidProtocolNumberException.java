package com.illumio.flowlog.exceptions;

/**
 * Exception thrown when an invalid protocol number is encountered.
 * Protocol numbers must be within a specific valid range.
 */
public class InvalidProtocolNumberException extends Exception{

  public InvalidProtocolNumberException(String s) {
    super(s);
  }
}
