

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) {

        var builder = new ChecksumBuilder(List.of("e:\\orgtestdata"));

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

            logger.info("Organizing files...");

            var org = new Organizer(checksumMap, "");
            org.organizeFiles();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}