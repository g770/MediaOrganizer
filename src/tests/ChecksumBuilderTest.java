import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.*;
import java.security.SecureRandom;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ChecksumBuilderTest {

    private ChecksumBuilder checksumBuilderNoDuplicates;
    private ChecksumBuilder checksumBuilderWithDuplicates;
    private List<String> directories;
    private List<Path> files;

    private static final int fileToCreate = 50;
    private static final int directoriesToCreate = 2;

    @BeforeEach
    void setUp() throws IOException {
        createNoDuplicatesChecksumBuilder();
        createChecksumBuilderWithDuplicates();
    }


    private void createChecksumBuilderWithDuplicates() throws IOException {
        directories = new ArrayList<>();
        files = new ArrayList<>();

        // Create test directories and files
            Path dir = Files.createTempDirectory("checksumBuilderDuplicatesTestDirectory");
            directories.add(dir.toString());

            // All the files will be same
        var rnd = new SecureRandom();
        var fileContents = new byte[1024 * 1024];
        rnd.nextBytes(fileContents);
        for (int j = 1; j <= fileToCreate; j++) {
            Path file = Files.createTempFile(dir, "file" + j, ".txt");
            Files.write(file, fileContents);
            files.add(file);
        }

        checksumBuilderWithDuplicates = new ChecksumBuilder(directories);
        checksumBuilderWithDuplicates.calculateChecksums();
    }

    private void createNoDuplicatesChecksumBuilder() throws IOException {
        directories = new ArrayList<>();
        files = new ArrayList<>();

        // Create test directories and files
        for (int i = 1; i <= directoriesToCreate; i++) {
            Path dir = Files.createTempDirectory("checksumBuilderTestDirectory" + i);
            directories.add(dir.toString());

            for (int j = 1; j <= fileToCreate; j++) {
                Path file = Files.createTempFile(dir, "file" + j, ".txt");
                var rnd = new SecureRandom();
                var fileContents = new byte[i * 1024];
                rnd.nextBytes(fileContents);
                Files.write(file, fileContents); // Create files of varying sizes
                files.add(file);
            }
        }

        checksumBuilderNoDuplicates = new ChecksumBuilder(directories);
        checksumBuilderNoDuplicates.calculateChecksums();
    }

    @AfterEach
    void tearDown() throws IOException {
        // Delete test directories and files
        for (Path file : files) {
            Files.deleteIfExists(file);
        }
        for (String dir : directories) {
            Files.deleteIfExists(Paths.get(dir));
        }
    }

    @Test
    void getChecksumMapNotNull() {
        assertNotNull(checksumBuilderNoDuplicates.getChecksumMap());
        assertNotNull(checksumBuilderWithDuplicates.getChecksumMap());
    }


    @Test
    void getChecksumNoDuplicatesMapSize() {
        var map = checksumBuilderNoDuplicates.getChecksumMap();
        assertEquals(fileToCreate * directoriesToCreate, map.entrySet().size());
    }

    @Test
    void getChecksumWithDuplicatesMapSize() {
        var map = checksumBuilderWithDuplicates.getChecksumMap();
        assertEquals(1, map.entrySet().size());
    }

    @Test
    void getChecksumNoDuplicates() {

        // There should be no duplicates in checksumBuilder1
        var map = checksumBuilderNoDuplicates.getChecksumMap();

        // Iterate over each entry in the map and assert the size of the value is 1
        for (var kvp : map.entrySet()) {
            assertEquals(1, kvp.getValue().size());
        }
    }

    @Test
    void getChecksumWithDuplicates() {

        // There should be no duplicates in checksumBuilder1
        var map = checksumBuilderWithDuplicates.getChecksumMap();

        // Iterate over each entry in the map and assert the size of the value is 1
        for (var kvp : map.entrySet()) {
            assertEquals(fileToCreate, kvp.getValue().size());
        }
    }

}