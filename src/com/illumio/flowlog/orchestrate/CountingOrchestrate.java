package com.illumio.flowlog.orchestrate;

import com.illumio.flowlog.exceptions.InvalidProtocolNumberException;
import com.illumio.flowlog.fileloaders.LookupTableLoader;
import com.illumio.flowlog.fileloaders.ProtocolNumberLoader;
import com.illumio.flowlog.trackers.PortProtocolTracker;
import com.illumio.flowlog.trackers.TaggingTracker;
import com.illumio.flowlog.trackers.interfaces.Tracker;
import com.illumio.flowlog.utilities.Constants;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The CountingOrchestrate class is responsible for orchestrating the tracking of protocol counts
 * and tagging information from flow logs.
 *
 * <p>This class processes lines from flow logs, updates trackers for port and
 * protocol counts, and manages output based on provided lookup tables and protocol number
 * loaders.</p>
 */
public class CountingOrchestrate {
  Logger logger = Logger.getLogger(CountingOrchestrate.class.getName());
  private Tracker<Integer, String> taggingTracker;
  private Tracker<Integer, String> portProtocolTracker;

  private Map<String, List<String>> trackerCounts;
  private static boolean isLookupTable;

  /**
   * Constructs a CountingOrchestrate object.
   *
   * @param lookupTableLoader    a LookupTableLoader used to load tagging information. If null,
   *                             tagging functionality will not be available.
   * @param protocolNumberLoader a ProtocolNumberLoader used to map protocol numbers to names.
   */
  public CountingOrchestrate(LookupTableLoader lookupTableLoader,
      ProtocolNumberLoader protocolNumberLoader) {
    //parse the file
    isLookupTable = false;
    trackerCounts = new HashMap<>();
    if (lookupTableLoader != null) {
      isLookupTable = true;
      taggingTracker = new TaggingTracker(lookupTableLoader, protocolNumberLoader);
    }
    portProtocolTracker = new PortProtocolTracker(protocolNumberLoader);
  }

  /**
   * Processes a line from the flow log, adding the port and protocol information to the respective
   * trackers.
   *
   * @param port     the port number to be processed.
   * @param protocol the protocol number associated with the port.
   * @throws InvalidProtocolNumberException if the protocol number is invalid.
   */
  public void processLine(int port, int protocol) throws InvalidProtocolNumberException {
    if (isLookupTable) {
      taggingTracker.add(port, protocol);
    }
    portProtocolTracker.add(port, protocol);
  }

  /**
   * Retrieves the output of the tracking process, including counts for tagging and port/protocol
   * combinations.
   *
   * @return a map containing the counts of tagging and port/protocol information.
   */
  public Map<String, List<String>> getOutput() {
    logger.info("Received request to return output");
    if (isLookupTable) {
      trackerCounts.put(Constants.TAGGING, taggingTracker.get());
    }
    trackerCounts.put(Constants.PORT_PROTOCOL_COUNT, portProtocolTracker.get());
    return trackerCounts;
  }
}
