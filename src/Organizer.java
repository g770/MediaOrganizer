import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Organizer {

    private final Map<String, List<File>> checksumMap;
    private final String destinationDirectory;

    public Organizer(Map<String, List<File>> checksumMap, String destinationDirectory) {
        this.checksumMap = checksumMap;
        this.destinationDirectory = destinationDirectory;
    }

    public void organizeFiles() {

        var pattern = Pattern.compile("([0-9]{4})-([0-9]{2})-([0-9]{2}) ([A-Za-z0-9 ]*)");

        for (Map.Entry<String, List<File>> k : checksumMap.entrySet()) {

            // Look at each file in the list to see if the path or name looks like a date
            for (File f : k.getValue()) {

                // If the file name has a date and name info, use that
                var fName = f.getName();
                System.out.println("Looking at file name: " + fName);

                var matcher = pattern.matcher(fName);

                if(matcher.find()) {
                    handleDateFormatMatch(matcher);
                } else {

                    // Try the path
                    var path = f.getAbsolutePath();
                    System.out.println("Looking at path: " + fName);

                    matcher = pattern.matcher(path);
                    if (matcher.find()) {
                        handleDateFormatMatch(matcher);
                    }
                }

            }


        }
    }

    private static void handleDateFormatMatch(Matcher matcher) {
        System.out.println("Found a match");

        String year = matcher.group(1);
        String month = matcher.group(2);
        String day = matcher.group(3);
        String description = matcher.group(4);

        // Create subfolder in output directory named Year-Month-Day Description
        // and copy file
        String folderName = year + "-" + month + "-" + day + " " + description;
        System.out.println("Output folder: " + folderName);
    }
}
