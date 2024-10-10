package com.illumio.flowlog.utilities;


/**
 * The Constants class holds the string constants used throughout the program, especially in the properties
 * file. By keeping these values as constants, we prevent potential issues caused by string mismatches.
 *
 * <p>
 * These constants include property keys for file paths, headers used in CSV files, and identifiers for
 * various counters. Using final variables ensures that these values remain unchanged and consistent across
 * the application.
 * </p>
 */
public class Constants {
  public static final String TAGGING = "TAGGING_COUNT";
  public static final String PORT_PROTOCOL_COUNT = "PORT_PROTOCOL_COUNT";

  public static final String TAG_COUNT_HEADER = "tag,count"+"\n";
  public static final String PORT_PROTOCOL_HEADER = "port,protocol,count"+"\n";

  public static final String FLOW_LOG_PATH = "flowlog.path";

  public static final String LOOKUP_TABLE_PATH = "lookup.table.path";

  public static final String PROTOCOL_NUMBER_PATH= "protocol.number.path";

  public static final String OUTPUT_FILE_PATH = "output.file.path";

  public static final String ERROR_FILE_PATH = "error.file.path";
}
