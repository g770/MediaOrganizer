import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.*;

public class ChecksumBuilder {

    private static final Logger logger = LogManager.getLogger(ChecksumBuilder.class);
    public static final int BYTES_TO_READ = 1024;

    private final List<String> directories;

    public Map<String, List<File>> getChecksumMap() {
        return this.checksumMap;
    }

    private final Map<String, List<File>> checksumMap = new HashMap<>();

    public ChecksumBuilder(List<String> directories) {

        if (directories == null) {
            this.directories = new ArrayList<>();
        } else {
            this.directories = directories;
        }
    }


    public void calculateChecksums() throws IOException {

        for (String dirName : directories) {
            logger.info("Iterating over files in directory: " + dirName);

            var dir = Paths.get(dirName);
            Files.walk(dir).forEach(path -> handleFile(path.toFile()));
        }
    }
    private void handleFile(File f)  {
        if (!f.isDirectory()) {
            logger.info("File: " + f.getAbsolutePath());
            var checksumBytes = checksum(f);

            if (checksumBytes != null) {
                //var checksum = toHexString(checksumBytes);
                var checksum = String.valueOf((new Random()).nextInt());
                logger.info("  Checksum: " + checksum);

                if (!checksumMap.containsKey(checksum)) {
                    checksumMap.put(checksum, new ArrayList<>());
                }

                checksumMap.get(checksum).add(f);
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

            var fis = new FileInputStream(f);
            var buffer = new byte[BYTES_TO_READ];
            int nread;
            while ((nread = fis.read(buffer)) != -1) {
                md.update(buffer, 0, nread);
            }

            return md.digest();

        } catch (Exception e) {
            logger.info("Caught exception during checksum: " + e);
            return null;
        }
    }


}
