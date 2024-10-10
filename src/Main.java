
import com.illumio.flowlog.exceptions.MissingFileException;
import com.illumio.flowlog.exceptions.MissingMandatoryFileException;
import com.illumio.flowlog.loggers.ErrorLogger;
import com.illumio.flowlog.orchestrate.CountingOrchestrate;
import com.illumio.flowlog.processor.FlowLogProcessor;
import com.illumio.flowlog.setup.CustomProperties;
import com.illumio.flowlog.utilities.Constants;
import com.illumio.flowlog.fileloaders.LookupTableLoader;
import com.illumio.flowlog.fileloaders.ProtocolNumberLoader;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * The Main class is the entry point for the flow log processing application.
 * It initializes all required resources like loggers, protocol loaders, and orchestrates the log processing workflow.
 */

public class Main {

  static Logger logger = Logger.getLogger(Main.class.getName());

  /** A Properties object holding the configuration settings for the application. */
  static Properties properties;
  /**
   * The main method initializes and executes the flow log processing workflow.
   * It loads properties, creates required components, and orchestrates the log processing.
   *
   * @param args Command-line arguments (not used in this application).
   * @throws MissingMandatoryFileException Thrown if a required mandatory file like flow file is missing
   * and programme is stopped.
   */
  public static void main(String[] args) throws MissingMandatoryFileException {
    logger.info("Loading Properties from properties file");
    properties = new CustomProperties();
    logger.info("Setting up Global Error Logging");
    ErrorLogger errorLogger = ErrorLogger.getInstance(properties.getProperty(Constants.ERROR_FILE_PATH));
    logger.info("Loading protocol numbers and name information");
    ProtocolNumberLoader protocolNumberLoader = ProtocolNumberLoader.getInstance(properties.getProperty(Constants.PROTOCOL_NUMBER_PATH));
    LookupTableLoader lookupTableLoader = null;
    try {
      logger.info("Loading Lookup Table");
      lookupTableLoader = LookupTableLoader.getInstance(properties.getProperty(Constants.LOOKUP_TABLE_PATH));
    } catch (MissingFileException e) {
      errorLogger.logError("Missing Lookup table, can calculate counts of port protocol combinations");
    }
    logger.info("Setting up orchestrator");
    CountingOrchestrate countingOrchestrate = new CountingOrchestrate(lookupTableLoader,protocolNumberLoader);
    logger.info("Calling flow log processor");
    FlowLogProcessor flowLogProcessor = new FlowLogProcessor(properties.getProperty(Constants.FLOW_LOG_PATH),countingOrchestrate);
    logger.info("Requesting for output");
    flowLogProcessor.generateOutput(properties.getProperty(Constants.OUTPUT_FILE_PATH));
  }

}