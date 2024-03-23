import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;

public class ChecksumBuilder implements IChecksumBuilder {

    private static final Logger logger = LogManager.getLogger(ChecksumBuilder.class);
    public static final int BYTES_TO_READ = 1024;

    // The input directories to scan for files
    private final List<String> directories;

    @Override
    public Map<String, List<AbstractMap.SimpleEntry<String, File>>> getChecksumMap() {
        return this.checksumMap;
    }

    private final Map<String, List<AbstractMap.SimpleEntry<String, File>>> checksumMap = new HashMap<>();

    public ChecksumBuilder(List<String> directories) {

        this.directories = Objects.requireNonNullElseGet(directories, ArrayList::new);
    }


    @Override
    public void calculateChecksums() throws IOException {

        for (String dirName : directories) {
            logger.info("Iterating over files in directory: " + dirName);

            // Convert directory name to a path object
            var dir = Paths.get(dirName);

            // Iterate over each file in the path
            Files.walk(dir).forEach(path -> handleFile(dirName, path.toFile()));
        }
    }

    private void handleFile(String inputDirName, File f)  {
        if (!f.isDirectory()) {
            logger.info("File: " + f.getAbsolutePath());
            var checksumBytes = checksum(f);

            if (checksumBytes != null) {
                var checksum = toHexString(checksumBytes);
                //var checksum = String.valueOf((new Random()).nextInt());
                logger.info("  Checksum: " + checksum);

                if (!checksumMap.containsKey(checksum)) {
                    checksumMap.put(checksum, new ArrayList<>());
                }

                checksumMap.get(checksum).add(new SimpleEntry<String, File>(inputDirName, f));
            }
        } else {
            logger.info("Skipped because it is a directory: " + f.getAbsolutePath());
        }
    }

    private static String toHexString(byte[] bytes) {
        var result = new StringBuilder();
        for(byte b : bytes) {
            result.append(String.format("%02x", b));
        }

        return result.toString();
    }
    private static byte[] checksum(File f)  {
        try {
            var md = MessageDigest.getInstance("MD5");

            try (var fis = new FileInputStream(f)) {
                var buffer = new byte[BYTES_TO_READ];
                int nread;
                while ((nread = fis.read(buffer)) != -1) {
                    md.update(buffer, 0, nread);
                }
            }

            return md.digest();

        } catch (Exception e) {
            logger.info("Caught exception during checksum: " + e);
            return null;
        }
    }


}
