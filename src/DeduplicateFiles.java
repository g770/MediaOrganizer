import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public class DeduplicateFiles {

    private final String outputDir;

    private static final Logger logger = LogManager.getLogger(DeduplicateFiles.class);

    public DeduplicateFiles(String outputDir) {
        this.outputDir = outputDir;
    }

    public void copyAndDeduplicateFiles(Map<String, List<AbstractMap.SimpleEntry<String, File>>> checksumMap) {

        // Iterate over each set of identical files
        for (Map.Entry<String, List<AbstractMap.SimpleEntry<String, File>>> k : checksumMap.entrySet()) {
            if (!k.getValue().isEmpty()) {

                // Copy over the first file from the list, ignore the rest
                var filePair = k.getValue().get(0);
                var inputDir = filePair.getKey();   // This was the original input directory this file came from
                var file = filePair.getValue();     // The actual file to copy

                // Remove the first item so we can log the files
                // we skipped
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

                // Ensure the directory tree exists before copying the file
                createDirectories(finalPath);

                logger.info("Copying src:dest {} : {}", path, finalPath);
                try {
                    Files.copy(Path.of(path), Path.of(finalPath));
                } catch (IOException e) {
                    logger.error("Failed to copy: " + path + " to " + finalPath + ": " + e.getMessage());
                }
            }
        }
    }

    private void logSkippedFiles(List<AbstractMap.SimpleEntry<String, File>> files) {

        for(var f : files) {
            logger.info("Skipping file: {}", f.getValue().getPath());
        }

    }

    private void createDirectories(String finalPath) {
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
