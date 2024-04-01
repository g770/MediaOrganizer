import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    private static class CommandLineArguments {
        private final String action;
        private final List<String> inputDirs;
        private final String outputDir;

        public CommandLineArguments(String action, List<String> inputDirs, String outputDir) {
            this.action = action;
            this.inputDirs = inputDirs;
            this.outputDir = outputDir;
        }

        public String getAction() {
            return action;
        }

        public List<String> getInputDirs() {
            return inputDirs;
        }

        public String getOutputDir() {
            return outputDir;
        }
    }
    private static final Logger logger = LogManager.getLogger(Main.class);

    private static final String outputDir = "e:\\orgtestdata_output";

    private static final List<String> inputDirs = new ArrayList<>(List.of("e:\\orgtestdata", "e:\\orgtestdata2"));
    public static void main(String[] args) {

        var cmdArgs = processArgs(args);

        var organizer = new DateOrganizer("e:\\orgtestdata_output", "e:\\organized_output");

        try {
            organizer.organizeFiles();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /*
        var builder = new ChecksumBuilder(inputDirs);

        logger.info("Starting...");
        try {
            builder.calculateChecksums();

            var checksumMap = builder.getChecksumMap();
            logger.info("Scan complete");

            var dedup = new DeduplicateFiles(outputDir);
            dedup.copyAndDeduplicateFiles(checksumMap);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
*/
    }

    public static CommandLineArguments processArgs(String[] args) {
        String action = null;
        List<String> inputDirs = new ArrayList<>();
        String outputDir = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-a":
                    if (i + 1 < args.length) {
                        action = args[++i];
                    }
                    break;
                case "-i":
                    if (i + 1 < args.length) {
                        inputDirs.add(args[++i]);
                    }
                    break;
                case "-o":
                    if (i + 1 < args.length) {
                        outputDir = args[++i];
                    }
                    break;
                case "-h":
                    printHelp();
                    return null;
                default:
                    System.out.println("Unexpected argument: " + args[i]);
            }
        }

        return new CommandLineArguments(action, inputDirs, outputDir);
    }

    public static void printHelp() {
        logger.info("Usage: java fileOrganizer -a <action> -o <outputDir> -i <inputDir1> -i <inputDir2> ...");
        logger.info("Options:");
        logger.info("\t-a <action>\t\tThe action to perform. Can be either 'organize' or 'deduplicate'.");
        logger.info("\t-o <outputDir>\t\tThe output directory.");
        logger.info("\t-i <inputDir>\t\tThe input directory. This option can be specified multiple times for multiple input directories.");
        logger.info("\t-h\t\t\tPrint this help message.");
    }
}