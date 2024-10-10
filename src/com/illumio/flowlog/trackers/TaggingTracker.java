package com.illumio.flowlog.trackers;
import com.illumio.flowlog.exceptions.InvalidProtocolNumberException;
import com.illumio.flowlog.trackers.interfaces.Tracker;
import com.illumio.flowlog.fileloaders.LookupTableLoader;
import com.illumio.flowlog.fileloaders.ProtocolNumberLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The TaggingTracker class is responsible for tagging each line of flow logs based on port and protocol
 * information, and for maintaining a count of how often each tag occurs.
 *
 * <p>
 * This service uses the lookup table and protocol number loader to retrieve the correct tags and
 * update the counts accordingly. The class also provides functionality to return the overall tag counts.
 * </p>
 */
public class TaggingTracker implements Tracker<Integer, String> {
  private static final Logger logger = Logger.getLogger(TaggingTracker.class.getName());
  private Map<String, Long> tagCounts;
  private LookupTableLoader lookupTableLoader;
  private ProtocolNumberLoader protocolNumberLoader;


  /**
   * Constructs a TaggingTracker instance with the provided LookupTableLoader and ProtocolNumberLoader.
   *
   * @param lookupTableLoader the loader responsible for providing tag information based on port and protocol
   * @param protocolNumberLoader the loader responsible for translating protocol numbers
   */
  public TaggingTracker(LookupTableLoader lookupTableLoader, ProtocolNumberLoader protocolNumberLoader) {
    this.lookupTableLoader = lookupTableLoader;
    tagCounts = new HashMap<>();
    this.protocolNumberLoader = protocolNumberLoader;
  }

  /**
   * Adds a tag count for the given port and protocol, if valid.
   *
   * <p>
   * This method parses the provided port and protocol, checks for validity, retrieves the corresponding
   * tag from the lookup table, and updates the tag count.
   * </p>
   *
   * @param port the port number to tag
   * @param protocol the protocol number to tag
   * @throws InvalidProtocolNumberException if the port number is invalid
   */
  public void add(Integer port, Integer protocol) throws InvalidProtocolNumberException {
    //get tag
    if(protocol<0 || protocol>255) return; //since we are only capturing for 0 - 255 protocols by decimal if there is incorrect protocol values do not consider
    String tag = lookupTableLoader.getTag(port,protocolNumberLoader.getProtocol(protocol));
    tagCounts.computeIfPresent(tag, (k, v) -> v + 1);
    tagCounts.computeIfAbsent(tag, (v) -> 1l);
  }

  /**
   * Retrieves the current list of tag counts.
   *
   * <p>
   * This method returns a list of strings, each containing a tag and its associated count, separated
   * by a comma.
   * </p>
   *
   * @return a list of tag counts in the format "tag,count"
   */
  public List<String> get() {
    logger.info("Received call to retrieve total counts");
    List<String> tagsCounts = new ArrayList<>();
    Iterator<String> tagCountsIterator = tagCounts.keySet().iterator();
    while(tagCountsIterator.hasNext()){
      String tag = tagCountsIterator.next();
      Long count = tagCounts.get(tag);
      tagsCounts.add(tag+","+count);
    }
    return tagsCounts;


  }

}
