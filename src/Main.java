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
        private final boolean preview;

        public CommandLineArguments(String action, List<String> inputDirs, String outputDir, boolean preview) {
            this.action = action;
            this.inputDirs = inputDirs;
            this.outputDir = outputDir;
            this.preview = preview;
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

        public boolean isPreview() {
            return preview;
        }
        public boolean isValid() {
            if (action == null || (!action.equals("deduplicate") && !action.equals("organize"))) {
                return false;
            }
            if (inputDirs == null || inputDirs.isEmpty()) {
                return false;
            }
            if (outputDir == null || outputDir.isEmpty()) {
                return false;
            }
            return true;
        }
    }
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        var cmdArgs = processArgs(args);
        if (cmdArgs == null || !cmdArgs.isValid()) {
            printHelp();

        }
        else {
            try {
                if (cmdArgs.isPreview()) {
                    logger.info("Running in preview mode. No files will be modified.");
                }

                if (cmdArgs.getAction().equals("deduplicate")) {
                    var checksumBuilder = new ChecksumBuilder(cmdArgs.getInputDirs());
                    checksumBuilder.calculateChecksums();
                    var checksumMap = checksumBuilder.getChecksumMap();
                    var deduplicator = new DeduplicateFiles(cmdArgs.getOutputDir(), cmdArgs.isPreview());
                    deduplicator.copyAndDeduplicateFiles(checksumMap);

                } else if (cmdArgs.getAction().equals("organize")) {
                    for (String inputDir : cmdArgs.getInputDirs()) {
                        var dateOrganizer = new DateOrganizer(inputDir, cmdArgs.getOutputDir(), cmdArgs.isPreview());
                        dateOrganizer.organizeFiles();
                    }
                }
            } catch (IOException e) {
                logger.error("An error occurred while processing files: {}", e.getMessage());
            }
        }


    }

    public static CommandLineArguments processArgs(String[] args) {
        String action = null;
        List<String> inputDirs = new ArrayList<>();
        String outputDir = null;
        boolean preview = false;

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
                case "-p":
                    preview = true;
                    break;
                case "-h":
                    printHelp();
                    return null;
                default:
                    System.out.println("Unexpected argument: " + args[i]);
            }
        }

        return new CommandLineArguments(action, inputDirs, outputDir, preview);
    }

    public static void printHelp() {
        logger.info("Usage: java -j PhotoOrganizer.jar -a <action> -o <outputDir> -i <inputDir1> -i <inputDir2> ...");
        logger.info("Options:");
        logger.info("\t-a <action>\t\tThe action to perform. Can be either 'organize' or 'deduplicate'.");
        logger.info("\t-o <outputDir>\t\tThe output directory.");
        logger.info("\t-i <inputDir>\t\tThe input directory. This option can be specified multiple times for multiple input directories.");
        logger.info("\t-p\t\t\tPreview mode. Do not perform any file operations, only print what would be done.");
        logger.info("\t-h\t\t\tPrint this help message.");
    }
}