import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;


/**
 * Interface for classes that build checksums for files.
 */
public interface IChecksumBuilder {

    /**
     * Retrieves a map of checksums and their corresponding files.
     *
     * @return a Map where the key is a checksum (String) and the value is a List of SimpleEntry objects.
     * Each SimpleEntry contains the directory name (String) and the corresponding File.
     */
    Map<String, List<AbstractMap.SimpleEntry<String, File>>> getChecksumMap();

    /**
     * Calculates checksums for all files in the directories specified in the implementing class.
     *
     * @throws IOException if an I/O error occurs during the checksum calculation.
     */
    void calculateChecksums() throws IOException;
}