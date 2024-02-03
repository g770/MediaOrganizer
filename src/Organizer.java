import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
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

    private final Map<String, List<File>> checksumMap;
    private final String destinationDirectory;


    public Organizer(Map<String, List<File>> checksumMap, String destinationDirectory) {
        this.checksumMap = checksumMap;
        this.destinationDirectory = destinationDirectory;
    }

    public void organizeFiles() {

        for (Map.Entry<String, List<File>> k : checksumMap.entrySet()) {

            // Look at each file in the list to see if the path or name looks like a date
            for (File f : k.getValue()) {

                logger.info("Determining output path for file: " + f.getPath());

                // If the file name has a date and name info, use that
                // Try the path
                var path = Paths.get(f.getPath()).getParent().toString();

                var matcher = pattern.matcher(path);
                if (matcher.find()) {
                    handleDateFormatMatch(f, matcher);
                }
                else {
                    // Path didn't work, look at the file modification date
                    logger.info("Using file modification date");
                    handleFileModificationDate(f);
                }
            }

        }
    }

    private void handleFileModificationDate(File f) {

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
    }

    private static void handleDateFormatMatch(File f, Matcher matcher) {

        logger.info("Using matching date format to make output path");
        String year = matcher.group(MATCHGROUP_YEAR);
        String month = matcher.group(MATCHGROUP_MONTH);
        String day = matcher.group(MATCHGROUP_DAY);
        String description = matcher.group(MATCHGROUP_DESCRIPTION);

        // Create subfolder in output directory named Year-Month-Day Description
        // and copy file
        StringBuilder folderName = new StringBuilder();
        folderName.append(year);
        folderName.append("-");
        folderName.append(month);
        folderName.append("-");
        folderName.append(day);
        folderName.append(" ");
        folderName.append(description);

        logger.info("Output: " + folderName + "/" + f.getName());
    }
}
