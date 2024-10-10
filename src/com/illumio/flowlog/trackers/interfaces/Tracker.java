package com.illumio.flowlog.trackers.interfaces;

import com.illumio.flowlog.exceptions.InvalidProtocolNumberException;
import java.util.List;

/**
 * The Tracker interface defines the structure for tracking and managing counts of generic key-value
 * pairs.
 *
 * <p>
 * Implementing classes are responsible for providing mechanisms to add entries and retrieve the
 * tracked data.
 * </p>
 *
 * @param <K> the type of keys to be tracked (e.g., port or protocol)
 * @param <V> the type of values to be returned (e.g., tag or count)
 */
public interface Tracker<K, V> {

  /**
   * Retrieves the current list of tracked values.
   *
   * <p>
   * This method should return a list of values that have been tracked and managed by the
   * implementing class.
   * </p>
   *
   * @return a list of tracked values of type V
   */
  List<V> get();

  /**
   * Adds an entry to the tracker.
   *
   * <p>
   * This method takes two parameters of type K, processes them, and adds them to the tracker.
   * </p>
   *
   * @param a the first key to be tracked (e.g., port)
   * @param b the second key to be tracked (e.g., protocol)
   * @throws InvalidProtocolNumberException if the key pair is invalid
   */
  void add(K a, K b) throws InvalidProtocolNumberException;
}
