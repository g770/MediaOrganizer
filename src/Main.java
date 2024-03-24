import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    private static final String outputDir = "e:\\orgtestdata_output";

    private static final List<String> inputDirs = new ArrayList<>(List.of("e:\\orgtestdata", "e:\\orgtestdata2"));
    public static void main(String[] args) {

        var organizer = new Organizer("e:\\orgtestdata_output", "e:\\organized_output");

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

}