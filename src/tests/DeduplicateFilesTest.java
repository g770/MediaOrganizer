/*
 * Copyright (c) [2024] [SonoranTech]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */



import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.*;


public class DeduplicateFilesTest {

    private ChecksumBuilder checksumBuilderNoDuplicates;
    private ChecksumBuilder checksumBuilderWithDuplicates;
    private List<String> directories;
    private List<Path> files;

    private static final int fileToCreate = 50;
    private static final int directoriesToCreate = 2;
    private Path outputDir;

    @BeforeEach
    void setUp() throws IOException {
        createNoDuplicatesChecksumBuilder();
        createChecksumBuilderWithDuplicates();

        // Create the output directory
        Path dir = Files.createTempDirectory("deduplicatetest-outputdir");
        this.outputDir = dir;
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

        checksumBuilderWithDuplicates = new ChecksumBuilder(directories, null);
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

        checksumBuilderNoDuplicates = new ChecksumBuilder(directories, null);
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

        // Remove all files in the outputDir and delete the directory
        Files.walk(outputDir)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }


    @Test
    void copyAndDeduplicateFiles1() {

        var map = checksumBuilderNoDuplicates.getChecksumMap();
        var deduplicateFiles = new DeduplicateFiles(this.outputDir.toString(), false);
        deduplicateFiles.copyAndDeduplicateFiles(map);

        // If there are no duplicates the count of files in the output directory should be the same as the map
        Assertions.assertEquals(map.entrySet().size(), countFilesInDirectory(this.outputDir));
    }

    @Test
    void copyAndDeduplicateFiles2() {

        var map = checksumBuilderWithDuplicates.getChecksumMap();
        var deduplicateFiles = new DeduplicateFiles(this.outputDir.toString(), false);
        deduplicateFiles.copyAndDeduplicateFiles(map);

        Assertions.assertEquals(1, countFilesInDirectory(this.outputDir));
    }

    @Test
    void copyAndDeduplicateFilesPreviewMode() {

        var map = checksumBuilderNoDuplicates.getChecksumMap();
        var deduplicateFiles = new DeduplicateFiles(this.outputDir.toString(), true);
        deduplicateFiles.copyAndDeduplicateFiles(map);

        // Output dir should be empty since we are in preview mode
        Assertions.assertEquals(0, countFilesInDirectory(this.outputDir));
    }

    private int countFilesInDirectory(Path outputDir) {
        return (int) Arrays.stream(outputDir.toFile().listFiles()).filter(File::isFile).count();
    }


}