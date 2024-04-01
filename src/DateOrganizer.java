import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class responsible for organizing files based on their date.
 * Implements the IFileOrganizer interface.
 */
public class DateOrganizer implements IFileOrganizer {

    // Logger for logging information and error messages
    private static final Logger logger = LogManager.getLogger(DateOrganizer.class);

    // Pattern for matching date format in file names (YYYY-MM-DD Description)
    private static final Pattern pattern = Pattern.compile("([0-9]{4})-([0-9]{2})-([0-9]{2}) ([,'a-zA-Z0-9 ]*)");
    public static final int MATCHGROUP_YEAR = 1;
    public static final int MATCHGROUP_MONTH = 2;
    public static final int MATCHGROUP_DAY = 3;
    public static final int MATCHGROUP_DESCRIPTION = 4;

    // Destination directory for organized files
    private final String destinationDirectory;

    // Input directory containing files to be organized
    private final String inputDirectory;

    /**
     * Constructor for the DateOrganizer class.
     *
     * @param inputDirectory Directory containing files to be organized.
     * @param destinationDirectory Directory where the organized files will be placed.
     */
    public DateOrganizer(String inputDirectory, String destinationDirectory) {
        this.inputDirectory = inputDirectory;
        this.destinationDirectory = destinationDirectory;
    }

    /**
     * Organizes files based on their date.
     *
     * @throws IOException if an I/O error occurs during the file organization.
     */
    @Override
    public void organizeFiles() throws IOException {

        // Convert directory name to a path object
        var dir = Paths.get(this.inputDirectory);

        // Iterate over each file in the input directory
        // and determine where to copy it
        Files.walk(dir).forEach(path -> handleFile(path.toFile()));

    }

    /**
     * Handles each file in the directory. If the file is not a directory, it determines the output path for the file.
     *
     * @param file The file to be handled.
     */
    private void handleFile(File file) {

        if (!file.isDirectory()) {
            logger.info("Determining output path for file: {}", file.getPath());

            // First check if the path matches the expected date format,
            // otherwise use the file modification date
            var path = Paths.get(file.getPath()).getParent().toString();

            String outputDir;
            var matcher = pattern.matcher(path);
            if (matcher.find()) {
                outputDir = handleDateFormatMatch(file, matcher);
            } else {
                // Path didn't work, look at the file modification date
                logger.info("Using file modification date");
                outputDir = handleFileModificationDate(file);
            }


            // Copy the file to the output directory
            createDirectories(outputDir);

            // Add the file name to the output directory
            var finalFinalPath = outputDir + File.separator + file.getName();

            if ((new File(finalFinalPath)).exists()) {

                logger.info("File already exists: {}", finalFinalPath);

                finalFinalPath = handleExistingFile(file, outputDir);
            }

            logger.info("Copying file {} to: {}", file.getName(), outputDir);

            try {
                Files.copy(file.toPath(), Path.of(finalFinalPath));
            } catch (IOException e) {
                logger.error("Failed to copy: {} to {}: {}", file.getPath(), finalFinalPath, e.getMessage());
            }
        }
    }

    /**
     * Handles the case when the file already exists in the destination directory.
     * Adds an epoch timestamp to the front of the filename.
     *
     * @param file The file to be handled.
     * @param outputDir The output directory.
     * @return The final path for the file.
     */
    private String handleExistingFile(File file, String outputDir) {
        String finalFinalPath;
        // If the file already exists, add an epoch timestamp to the front of the filename
        finalFinalPath = outputDir + File.separator + System.currentTimeMillis() + "-" + file.getName();
        return finalFinalPath;
    }

    /**
     * Creates the necessary directories for the output path.
     *
     * @param finalPath The final output path where the file will be copied to.
     */
    private void createDirectories(String finalPath) {
        try {
            Files.createDirectories(Path.of(finalPath));
        } catch (IOException e) {
        }
    }

    /**
     * Handles the case when the file path matches the date format.
     * Constructs the output directory based on the matched date.
     *
     * @param f The file to be handled.
     * @param matcher The matcher for the date format.
     * @return The output directory for the file.
     */
    private String handleDateFormatMatch(File f, Matcher matcher) {

        logger.info("Using matching date format to make output path");
        String year = matcher.group(MATCHGROUP_YEAR);
        String month = matcher.group(MATCHGROUP_MONTH);
        String day = matcher.group(MATCHGROUP_DAY);
        String description = matcher.group(MATCHGROUP_DESCRIPTION);

        // Create subfolder in output directory named Year-Month-Day Description
        // and copy file
        StringBuilder folderName = new StringBuilder();
        folderName.append(destinationDirectory);
        folderName.append(File.separator);
        folderName.append(year);
        folderName.append("-");
        folderName.append(month);
        folderName.append("-");
        folderName.append(day);
        folderName.append(" ");
        folderName.append(description);

        logger.info("Output: {}{}{}", folderName, File.separator, f.getName());

        return folderName.toString();
    }

    /**
     * Handles the case when the file path does not match the date format.
     * Constructs the output directory based on the file modification date.
     *
     * @param f The file to be handled.
     * @return The output directory for the file.
     */
    private String handleFileModificationDate(File f) {

        logger.info("Using modification time to make output path");

        long modified = f.lastModified();
        var date = LocalDateTime.ofEpochSecond(modified/1000, 0, ZoneOffset.UTC);

        var folderName = new StringBuilder();
        folderName.append(destinationDirectory);
        folderName.append(File.separator);
        folderName.append(date.getYear());
        folderName.append("-");
        folderName.append(String.format("%02d", date.getMonthValue()));
        folderName.append("-");
        folderName.append(String.format("%02d", date.getDayOfMonth()));

        logger.info("Output: {}{}{}", folderName, File.separator, f.getName());

        return folderName.toString();
    }
}