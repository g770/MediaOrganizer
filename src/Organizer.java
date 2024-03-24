import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Organizer {

    private static final Logger logger = LogManager.getLogger(Organizer.class);

    private static final Pattern pattern = Pattern.compile("([0-9]{4})-([0-9]{2})-([0-9]{2}) ([,'a-zA-Z0-9 ]*)");
    public static final int MATCHGROUP_YEAR = 1;
    public static final int MATCHGROUP_MONTH = 2;
    public static final int MATCHGROUP_DAY = 3;
    public static final int MATCHGROUP_DESCRIPTION = 4;

    private final String destinationDirectory;

    private final String inputDirectory;

    public Organizer(String inputDirectory, String destinationDirectory) {
        this.inputDirectory = inputDirectory;
        this.destinationDirectory = destinationDirectory;
    }

    public void organizeFiles() throws IOException {

        // Convert directory name to a path object
        var dir = Paths.get(this.inputDirectory);

        // Iterate over each file in the input directory
        // and determine where to copy it
        Files.walk(dir).forEach(path -> handleFile(path.toFile()));

    }

    private void handleFile(File file) {

        if (!file.isDirectory()) {
            logger.info("Determining output path for file: " + file.getPath());

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

            logger.info("Copying file {} to: {}", file.getName(), outputDir);

            // Copy the file to the output directory
            createDirectories(outputDir);

            // Add the file name to the output directory
            outputDir += File.separator + file.getName();

            try {
                Files.copy(file.toPath(), Path.of(outputDir));
            } catch (IOException e) {
                logger.error("Failed to copy: " + file.getPath() + " to " + outputDir + ": " + e.getMessage());
            }
        }
    }

    private void createDirectories(String finalPath) {
        try {
            Files.createDirectories(Path.of(finalPath));
        } catch (IOException e) {
        }
    }

    private String handleFileModificationDate(File f) {

        logger.info("Using modification time to make output path");

        long modified = f.lastModified();
        var date = LocalDateTime.ofEpochSecond(modified/1000, 0, ZoneOffset.UTC);

        var dateString = new StringBuilder();
        dateString.append(date.getYear());
        dateString.append("-");
        dateString.append(String.format("%02d", date.getMonthValue()));
        dateString.append("-");
        dateString.append(String.format("%02d", date.getDayOfMonth()));

        logger.info("Output: " + dateString + "/" + f.getName());

        return dateString.toString();
    }

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

        logger.info("Output: " + folderName + File.separator + f.getName());

        return folderName.toString();
    }
}
