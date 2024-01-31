import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ChecksumBuilder {

    private final List<String> directories;

    public Map<String, List<File>> getChecksumMap() {
        return checksumMap;
    }

    private final Map<String, List<File>> checksumMap = new HashMap<>();

    public ChecksumBuilder(List<String> directories) {
        this.directories = directories;
    }

    public void calculateChecksums() throws IOException {

        for (String dirName : directories) {
            System.out.println("Directory: " + dirName);

            var dir = Paths.get(dirName);
            Files.walk(dir).forEach(path -> handleFile(path.toFile()));
        }
    }
    private void handleFile(File f)  {
        if (!f.isDirectory()) {
            System.out.println("File: " + f.getAbsolutePath());
            var checksumBytes = checksum(f);

            if (checksumBytes != null) {
                var checksum = toHexString(checksumBytes);
                System.out.println("  Checksum: " + checksum);

                if (!checksumMap.containsKey(checksum)) {
                    checksumMap.put(checksum, new ArrayList<>());
                }

                checksumMap.get(checksum).add(f);
            }
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
            var buffer = new byte[1024];
            int nread;
            while ((nread = fis.read(buffer)) != -1) {
                md.update(buffer, 0, nread);
            }

            return md.digest();

        } catch (Exception e) {
            System.out.println("Caught exception: " + e);
            return null;
        }
    }


}
