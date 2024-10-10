package com.illumio.flowlog.trackers;

import com.illumio.flowlog.exceptions.InvalidProtocolNumberException;
import com.illumio.flowlog.trackers.interfaces.Tracker;
import com.illumio.flowlog.fileloaders.ProtocolNumberLoader;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The PortProtocolTracker class is responsible for keeping track of counts associated with
 * port and protocol combinations.
 *
 * <p>
 * This service allows for adding entries to the tracker and retrieving the overall counts
 * in the format of port, protocol, and count.
 * </p>
 */
public class PortProtocolTracker implements Tracker<Integer,String> {

  ProtocolNumberLoader protocolNumberLoader;
  Map<Map.Entry<Integer, String>, Long> portProtocolCount;


  /**
   * Constructs a PortProtocolTracker instance with the provided ProtocolNumberLoader.
   *
   * @param protocolNumberLoader the loader responsible for translating protocol numbers
   */
  public PortProtocolTracker(ProtocolNumberLoader protocolNumberLoader) {
    this.protocolNumberLoader = protocolNumberLoader;
    portProtocolCount = new HashMap<>();
  }

  /**
   * Adds a port and protocol entry to the tracker.
   *
   * <p>
   * This method parses the provided port and protocol, checks if the protocol is valid,
   * and updates the count for the corresponding port and protocol combination.
   * </p>
   *
   * @param port the port number to track
   * @param protocol the protocol number to track
   * @throws InvalidProtocolNumberException if the port number is invalid
   */
  public void add(Integer port, Integer protocol) throws InvalidProtocolNumberException {
    Map.Entry<Integer, String> key = new SimpleEntry<>(port,
        protocolNumberLoader.getProtocol(protocol).isEmpty() ? String.valueOf(protocol)
            : protocolNumberLoader.getProtocol(protocol));
    portProtocolCount.computeIfPresent(key, (k, v) -> v + 1);
    portProtocolCount.computeIfAbsent(key, (v) -> 1l);
  }

  /**
   * Retrieves the current list of port and protocol counts.
   *
   * <p>
   * This method returns a list of strings, each containing a port, protocol, and the associated count,
   * separated by commas.
   * </p>
   *
   * @return a list of port, protocol, and count in the format "port,protocol,count"
   */
  public List<String> get() {
    List<String> portProtocolCounts = new ArrayList<>();
      Iterator<Map.Entry<Integer,String>> portProtocolIterator = portProtocolCount.keySet().iterator();
      while(portProtocolIterator.hasNext()){
        Map.Entry<Integer,String> portProtocol = portProtocolIterator.next();
        int port = portProtocol.getKey();
        String protocol = portProtocol.getValue();
        Long count = portProtocolCount.get(portProtocol);
        portProtocolCounts.add(port+","+protocol+","+count);
      }
      return portProtocolCounts;

  }
}
