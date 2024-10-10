package com.illumio.flowlog.fileloaders;

import com.illumio.flowlog.exceptions.MissingFileException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * The LookupTableLoader class is responsible for loading and storing lookup table data
 * from a CSV file that contains port, protocol, and tag information. The data is stored
 * in a map that can be accessed by other services.
 */
public class LookupTableLoader {
  private static final Logger logger = Logger.getLogger(LookupTableLoader.class.getName());
  static final String UNTAGGED = "Untagged";
  Map<Map.Entry<Integer,String>,String> portProtocolTag;
  static LookupTableLoader instance;

  /**
   * Private constructor that loads lookup table data from a CSV file and stores it in a map.
   *
   * @param csvFilePath the path to the CSV file containing port, protocol, and tag information.
   * @throws MissingFileException if the file path is invalid or the file cannot be read.
   */
  private LookupTableLoader(String csvFilePath) throws MissingFileException {
    portProtocolTag = new HashMap<>();
    try (Stream<String> stream = Files.lines(Paths.get(csvFilePath))) {

      logger.info("Successfully loaded the lookup table into the system");
      //skipping the header line
      stream.skip(1).filter(line->!line.trim().isEmpty()).forEach((line) -> {
        String[] lookupData = line.split(",");
        try {
          int port = Integer.parseInt(lookupData[0]);
          String protocol = lookupData[1]; //As per the email the data is in
          Map.Entry<Integer,String> portProtocol = new SimpleEntry<>(port,protocol);
          String tag = lookupData[2];
          portProtocolTag.computeIfAbsent(portProtocol,(v)->tag);
        }catch (NumberFormatException ex){
          logger.finest("Skipping entry from lookup table for illegal format: "+ line );
        }

      });
      logger.info("Successfully parsed the cvs file");
    } catch (IOException e) {
      logger.severe("Facing issues with reading the file, check file path");
      throw new MissingFileException("Lookup Table file path is incorrect or file does not exist");
    }
  }

  /**
   * Returns the singleton instance of LookupTableLoader, loading the lookup table from
   * the specified CSV file if the instance has not already been initialized.
   *
   * @param path the path to the CSV file.
   * @return the singleton instance of LookupTableLoader.
   * @throws MissingFileException if the file cannot be found or read.
   */
  public static LookupTableLoader getInstance(String path) throws MissingFileException {
    if(instance == null){
      instance =  new LookupTableLoader(path);
    }
    return instance;
  }

  /**
   * Retrieves the tag for a given port and protocol combination from the loaded lookup table.
   *
   * @param port the port number to look up.
   * @param protocol the protocol to look up.
   * @return the tag associated with the port and protocol, or "Untagged" if the combination is not found.
   */
  public String getTag(int port, String protocol){
    return portProtocolTag.getOrDefault(new SimpleEntry<>(port,protocol),UNTAGGED);
  }

}
