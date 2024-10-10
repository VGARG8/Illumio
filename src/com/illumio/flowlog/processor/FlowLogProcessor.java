package com.illumio.flowlog.processor;

import com.illumio.flowlog.exceptions.InvalidProtocolNumberException;
import com.illumio.flowlog.exceptions.MissingMandatoryFileException;
import com.illumio.flowlog.loggers.ErrorLogger;
import com.illumio.flowlog.orchestrate.CountingOrchestrate;
import com.illumio.flowlog.utilities.Constants;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * FlowLogProcessor is responsible for processing flow log files, extracting port and protocol
 * information, and generating output files with tag counts and port-protocol counts.
 */
public class FlowLogProcessor {

  ErrorLogger errorLogger = ErrorLogger.getInstance();
  Logger logger = Logger.getLogger(getClass().getName());

  CountingOrchestrate countingOrchestrate;

  /**
   * Constructs a FlowLogProcessor instance and processes the flow log file.
   *
   * @param flowLogPath the path to the flow log file
   * @param countingOrchestrate the CountingOrchestrate instance for processing lines
   * @throws MissingMandatoryFileException if the flow log file does not exist
   */
  public FlowLogProcessor(String flowLogPath, CountingOrchestrate countingOrchestrate)
      throws MissingMandatoryFileException {
    this.countingOrchestrate = countingOrchestrate;
    processFlowLog(flowLogPath);
  }

  /**
   * Processes the flow log file line by line.
   *
   * @param flowLogPath the path to the flow log file
   * @throws MissingMandatoryFileException if the flow log file does not exist
   */
  private void processFlowLog(String flowLogPath) throws MissingMandatoryFileException {
    logger.info("Starting flow log file processing");
    try (Stream<String> stream = Files.lines(Paths.get(flowLogPath))) {
      stream.filter(line -> !line.trim().isEmpty()).forEach(line -> {
        String[] flowLog = line.split(" ");
        try {
          int port = Integer.parseInt(flowLog[6]);
          int protocol = Integer.parseInt(flowLog[7]);
          countingOrchestrate.processLine(port, protocol);
        } catch (NumberFormatException ex) {
          errorLogger.logError(
              "Skipping Line because of port or protocol are not integer || " + line + "||");
        } catch (InvalidProtocolNumberException e) {
          errorLogger.logError(
              "Skipping Line because protocol number is not in range [0-255] || " + line + " || Number: "+flowLog[7]);
        } catch (ArrayIndexOutOfBoundsException ex) {
          errorLogger.logError(
              "Skipping Line because flow log is not in correct format || " + line + " ||");
        }
      });
      logger.info("Flow log parsing successfully complete");
    } catch (IOException e) {
      errorLogger.logError("Flow Path file does not exist. Stopping system");
      throw new MissingMandatoryFileException("Can not read flowLogPath");
    }
  }

  /**
   * Generates an output file containing tag counts and port-protocol counts.
   *
   * @param outPutPath the path to the output file
   */
  public void generateOutput(String outPutPath) throws MissingMandatoryFileException {
    logger.info("Requesting output from Counting Orchestrator");
    Map<String, List<String>> trackerCounts = countingOrchestrate.getOutput();
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outPutPath))) {
      // Write tag counts if available
      if (trackerCounts.containsKey(Constants.TAGGING)) {
        List<String> tagCounts = trackerCounts.get(Constants.TAGGING);
        if (!tagCounts.isEmpty()) {
          writer.write(Constants.TAG_COUNT_HEADER);
          tagCounts.stream().forEach(line -> {
            try {
              writer.write(line + "\n");
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          });
        }
      }
      // Write port-protocol counts if available
      List<String> portProtocolCounts = trackerCounts.get(Constants.PORT_PROTOCOL_COUNT);
      if (!portProtocolCounts.isEmpty()) {
        writer.write(Constants.PORT_PROTOCOL_HEADER);
        portProtocolCounts.forEach(line -> {
          try {
            writer.write(line + "\n");
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });
      }
      logger.info("Output added successfully to output file, Check path "+outPutPath);
    } catch (IOException e) {
      errorLogger.logError("Not able to add data to output file");
      throw new MissingMandatoryFileException("Missing output file or incorrect path");
    }
  }
}
