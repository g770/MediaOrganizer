

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

            copyAndDeduplicateFiles(checksumMap);
            //logger.info("Organizing files...");

            //var org = new Organizer(checksumMap, "");
            //org.organizeFiles();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void copyAndDeduplicateFiles(Map<String, List<File>> checksumMap) {
        for (Map.Entry<String, List<File>> k : checksumMap.entrySet()) {
            if (!k.getValue().isEmpty()) {

                var f = k.getValue().get(0);
                var path = f.getPath().toString();

                var str = "e:\\orgtestdata";
                var idx = path.indexOf(str);
                var substr = path.substring(idx + str.length() + 1);
                var finalPath = outputDir + File.separator + substr;

                var foo = new File(finalPath);
                var fooParent = foo.getParent();


                try {
                    Files.createDirectories(Path.of(fooParent));
                } catch (IOException e) {
                }

                logger.info("Copying source: " + path + " to " + finalPath);
                try {
                    Files.copy(Path.of(path), Path.of(finalPath));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}