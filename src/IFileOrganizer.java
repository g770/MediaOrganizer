import java.io.IOException;

/**
 * Interface for classes that organize files.
 */
public interface IFileOrganizer {

    /**
     * Organizes files based on the implementation details.
     *
     * @throws IOException if an I/O error occurs during the file organization.
     */
    void organizeFiles() throws IOException;
}