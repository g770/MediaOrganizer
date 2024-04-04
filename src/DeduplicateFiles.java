import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Class responsible for copying and deduplicating files based on their checksums.
 */
public class DeduplicateFiles {

    // Directory where the deduplicated files will be copied to
    private final String outputDir;

    // Logger for logging information and error messages
    private static final Logger logger = LogManager.getLogger(DeduplicateFiles.class);
    private final Consumer<String> doCreateDirectories;
    private final BiConsumer<String, String> doFileCopy;

    /**
     * Constructor for the DeduplicateFiles class.
     *
     * @param outputDir Directory where the deduplicated files will be copied to.
     */
    public DeduplicateFiles(String outputDir, boolean previewMode) {
        this.outputDir = outputDir;

        if (previewMode) {
            logger.info("Running in preview mode");
            this.doCreateDirectories = (x) -> {};
            this.doFileCopy = (x, y) -> {};

        } else {
            this.doCreateDirectories = this::createDirectories;
            this.doFileCopy = this::copyFiles;
        }
    }

    /**
     * Copies and deduplicates files based on their checksums.
     *
     * @param checksumMap Map where the key is a checksum (String) and the value is a List of SimpleEntry objects.
     * Each SimpleEntry contains the directory name (String) and the corresponding File.
     */
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
                this.doCreateDirectories.accept(finalPath);

                logger.info("Copying src:dest {} : {}", path, finalPath);
                this.doFileCopy.accept(path, finalPath);
            }
        }
    }

    private void copyFiles(String path, String finalPath) {
        try {
            Files.copy(Path.of(path), Path.of(finalPath));
        } catch (IOException e) {
            logger.error("Failed to copy: " + path + " to " + finalPath + ": " + e.getMessage());
        }
    }

    /**
     * Logs the files that were skipped during the deduplication process.
     *
     * @param files List of files that were skipped.
     */
    private void logSkippedFiles(List<AbstractMap.SimpleEntry<String, File>> files) {

        for(var f : files) {
            logger.info("Skipping file: {}", f.getValue().getPath());
        }

    }

    /**
     * Creates the necessary directories for the output path.
     *
     * @param finalPath The final output path where the file will be copied to.
     */
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