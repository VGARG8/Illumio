package com.illumio.flowlog.fileloaders;

import com.illumio.flowlog.exceptions.InvalidProtocolNumberException;
import com.illumio.flowlog.exceptions.MissingMandatoryFileException;
import java.io.IOException;
import java.nio.file.Files;

import java.nio.file.Paths;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * The ProtocolNumberLoader class is a singleton that loads and manages protocol number mappings
 * from a CSV file. It allows retrieving protocol names based on protocol numbers ranging from 0 to 255.
 */
public class ProtocolNumberLoader {
  private static final Logger logger = Logger.getLogger(ProtocolNumberLoader.class.getName());
  private String[] mapper = new String[256]; //fixed no of protocols
  private static ProtocolNumberLoader instance;

  /**
   * Private constructor that loads the protocol mappings from a CSV file.
   * @param csvPath csvPath the path to the CSV file containing protocol numbers and their corresponding names.
   * @throws MissingMandatoryFileException if the file cannot be read or an IO error occurs.
   */
  private ProtocolNumberLoader(String csvPath) throws MissingMandatoryFileException {

    try(Stream<String> stream = Files.lines(Paths.get(csvPath))){
      logger.info("Successfully Loaded Protocol Number Reference File");
      stream.skip(1).forEach((line)->{
        String[] protocolNumbers = line.split(",");
        try{
          int decimal = Integer.parseInt(protocolNumbers[0]);
          mapper[decimal] = protocolNumbers[1].toLowerCase(); //making it here lower case since lookup table has protocol keyword in lower case
        }catch (NumberFormatException exception){
          //means 146-252 keep them empty since no keyword
        }
      });
      logger.info("Successfully processed Protocol Number Reference File");
    } catch (IOException e) {
      throw new MissingMandatoryFileException("CSV file containing protocol numbers and names does not exist");
    }
  }

  /**
   * Returns the singleton instance of ProtocolNumberLoader, loading the protocol mappings from
   * the specified CSV file if the instance has not already been initialized.
   *
   * @param csvPath the path to the CSV file containing protocol number mappings.
   * @return the singleton instance of ProtocolNumberLoader.
   */
  public static ProtocolNumberLoader getInstance(String csvPath)
      throws MissingMandatoryFileException {
    if(instance == null){
      instance =  new ProtocolNumberLoader(csvPath);
    }
    return instance;
  }

  /**
   * Retrieves the protocol name corresponding to the given protocol number.
   *
   * @param protocol the protocol number to look up (should be between 0 and 255).
   * @return the protocol name, or null if the protocol number is not mapped.
   * @throws InvalidProtocolNumberException if the protocol number is out of the valid range (0-255).
   */
  public String getProtocol(int protocol) throws InvalidProtocolNumberException {
    if(protocol<0 || protocol >255){
      throw new InvalidProtocolNumberException("Protocol numbers between 0 to 255 are valid");
    }
    return mapper[protocol]!=null?mapper[protocol]:String.valueOf(protocol);
  }

}
