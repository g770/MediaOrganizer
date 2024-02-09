

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    private static final String outputDir = "e:\\orgtestdata_output";

    private static final String inputDir = "e:\\orgtestdata";
    public static void main(String[] args) {

        var builder = new ChecksumBuilder(List.of(inputDir));

        logger.info("Starting...");
        try {
            builder.calculateChecksums();

            var checksumMap = builder.getChecksumMap();

            logger.info("Scan complete");

            for (Map.Entry<String, List<File>> k : checksumMap.entrySet()) {
                if (k.getValue().size() > 1) {
                    var infoString = new StringBuilder("Duplicates detected: ");
                    k.getValue().forEach(x -> infoString.append(x).append(", "));
                    logger.info(infoString);
                }
            }

            copyAndDeduplicateFiles(checksumMap);
            //logger.info("Organizing files...");

            //var org = new Organizer(checksumMap, "");
            //org.organizeFiles();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void copyAndDeduplicateFiles(Map<String, List<File>> checksumMap) {

        // Iterate over each set of identical files
        for (Map.Entry<String, List<File>> k : checksumMap.entrySet()) {
            if (!k.getValue().isEmpty()) {

                // Copy over the first file, ignore the rest and remove the first
                // from the list
                var file = k.getValue().get(0);

                k.getValue().remove(0);
                logSkippedFiles(k.getValue());

               // Build the output path
                var path = file.getPath();

                // Find the index of the source path substring in the file path
                var idx = path.indexOf(inputDir);

                // Remove the source path so we can replace it
                var substr = path.substring(idx + inputDir.length() + 1);

                // Construct the final output path
                var finalPath = outputDir + File.separator + substr;

                createDirectories(finalPath);

                logger.info("Copying src:dest " + path + ":" + finalPath);
                try {
                    Files.copy(Path.of(path), Path.of(finalPath));
                } catch (IOException e) {
                    logger.error("Failed to copy: " + path + " to " + finalPath + ": " + e.getMessage());
                }
            }
        }
    }

    private static void logSkippedFiles(List<File> files) {

        for(var f : files) {
            logger.info("Skipping file: " + f.getPath());
        }

    }

    private static void createDirectories(String finalPath) {
        try {
            // Create the directory tree for the output path
            // Exceptions are throw if the directory exists, so just
            // catch them
            var parentDir = (new File(finalPath)).getParent();
            Files.createDirectories(Path.of(parentDir));
        } catch (IOException e) {
        }
    }
}