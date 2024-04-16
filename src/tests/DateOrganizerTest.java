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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class DateOrganizerTest {


    private Path outputDir;
    private Path inputDir;

    @BeforeEach
    void setUp() throws IOException {

        // Create the input directory
        this.inputDir = Files.createTempDirectory("organizertest-inputdir");

        // Create the output directory
        this.outputDir = Files.createTempDirectory("organizertest-outputdir");
    }


    @AfterEach
    void tearDown() throws IOException {

        Files.walk(inputDir)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);

        Files.walk(outputDir)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }


    @Test
    void testDateFormat1_YYYY_MM_DD() {

        // Create a file with a date format in its path
        Path inputFile = null;
        try {
            Files.createDirectories(Paths.get(inputDir + File.separator + "2024-01-10 Description"));
            inputFile = Files.createFile(Path.of(inputDir + File.separator + "2024-01-10 Description" + File.separator + "TestFile.txt"));


        var rnd = new SecureRandom();
        var fileContents = new byte[1024 * 1024];
        rnd.nextBytes(fileContents);
        Files.write(inputFile, fileContents);

        var organizer = new DateOrganizer(inputDir.toString(), outputDir.toString(), DateOrganizer.DateFormat.YYYY_MM_DD, false);

            organizer.organizeFiles();

            // Check if the file was moved to the correct directory
            var expectedFile = Path.of(outputDir + File.separator + "2024-01-10 Description" + File.separator + inputFile.getFileName());
            var result = Files.exists(expectedFile);
            Assertions.assertTrue(result);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    void testDateFormat2_YYYY_MM_DD() {

        // Create a file with a date format in its path, without the description part
        Path inputFile = null;
        try {
            Files.createDirectories(Paths.get(inputDir + File.separator + "2024-01-10"));
            inputFile = Files.createFile(Path.of(inputDir + File.separator + "2024-01-10" + File.separator + "TestFile.txt"));


            var rnd = new SecureRandom();
            var fileContents = new byte[1024 * 1024];
            rnd.nextBytes(fileContents);
            Files.write(inputFile, fileContents);

            var organizer = new DateOrganizer(inputDir.toString(), outputDir.toString(), DateOrganizer.DateFormat.YYYY_MM_DD, false);

            organizer.organizeFiles();

            // Check if the file was moved to the correct directory
            var expectedFile = Path.of(outputDir + File.separator + "2024-01-10" + File.separator + inputFile.getFileName());
            var result = Files.exists(expectedFile);

            // This should be false, in this case the pattern would not match and modification date would be used
            Assertions.assertFalse(result);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    void testDateFormat3_YYYY_MM_DD() {

        // Create a file in a directory tree, but put the date format in the file name
        Path inputFile = null;
        try {
            Files.createDirectories(Paths.get(inputDir + File.separator + "My Nifty Photos"));
            inputFile = Files.createFile(Path.of(inputDir + File.separator + "My Nifty Photos" + File.separator + "2023-03-10 Vacation.jpg"));


            var rnd = new SecureRandom();
            var fileContents = new byte[1024 * 1024];
            rnd.nextBytes(fileContents);
            Files.write(inputFile, fileContents);

            var organizer = new DateOrganizer(inputDir.toString(), outputDir.toString(), DateOrganizer.DateFormat.YYYY_MM_DD, false);

            organizer.organizeFiles();

            // Check if the file was moved to the correct directory
            var expectedFile = Path.of(outputDir + File.separator + "2023-03-10" + File.separator + inputFile.getFileName());
            var result = Files.exists(expectedFile);

            // This should be false, in this case the pattern would not match and modification date would be used
            Assertions.assertFalse(result);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testDateFormat4_YYYY_MM_DD() {

        // Create a file in a directory tree, but put the date format in the file name
        Path inputFile = null;
        try {
            Files.createDirectories(Paths.get(inputDir + File.separator + "My Nifty Photos"));
            inputFile = Files.createFile(Path.of(inputDir + File.separator + "My Nifty Photos" + File.separator + "2023-03-10 Vacation.jpg"));


            var rnd = new SecureRandom();
            var fileContents = new byte[1024 * 1024];
            rnd.nextBytes(fileContents);
            Files.write(inputFile, fileContents);

            var organizer = new DateOrganizer(inputDir.toString(), outputDir.toString(), DateOrganizer.DateFormat.YYYY_MM_DD, false);

            organizer.organizeFiles();

            // This should be organized by modification date
            // Get the current date, the code grabs the modification date in utc
            var currentDate = LocalDateTime.now(ZoneOffset.UTC);
            var expectedFile = Path.of(outputDir + File.separator + currentDate.getYear() + "-" + String.format("%02d", currentDate.getMonthValue()) + "-" + String.format("%02d", currentDate.getDayOfMonth()) + File.separator + inputFile.getFileName());

            var result = Files.exists(expectedFile);

            Assertions.assertTrue(result);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testDateFormat5_YYYY_MM_DD() {

        // Create a file with a date format in its path
        Path inputFile = null;
        try {
            Files.createDirectories(Paths.get(inputDir + File.separator + "1982-03-15 Vacation Photos"));
            inputFile = Files.createFile(Path.of(inputDir + File.separator + "1982-03-15 Vacation Photos" + File.separator + "beach1.jpg"));


            var rnd = new SecureRandom();
            var fileContents = new byte[1024 * 1024];
            rnd.nextBytes(fileContents);
            Files.write(inputFile, fileContents);

            var organizer = new DateOrganizer(inputDir.toString(), outputDir.toString(), DateOrganizer.DateFormat.YYYY_MM_DD, false);

            organizer.organizeFiles();

            // Check if the file was moved to the correct directory
            var expectedFile = Path.of(outputDir + File.separator + "1982-03-15 Vacation Photos" + File.separator + inputFile.getFileName());
            var result = Files.exists(expectedFile);
            Assertions.assertTrue(result);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    void testDateFormat6_YYYY_MM_DD() {

        // Create a file with a date format in its path
        Path inputFile = null;
        try {
            Files.createDirectories(Paths.get(inputDir + File.separator + "1982-03-15 Vacation Photos"));

            List<Path> files = new ArrayList<>();

            for (int i = 1; i <= 100; i++) {
                inputFile = Files.createFile(Path.of(inputDir + File.separator + "1982-03-15 Vacation Photos" + File.separator + "beach" + i + ".jpg"));
                var rnd = new SecureRandom();
                var fileContents = new byte[1024 * 1024];
                rnd.nextBytes(fileContents);
                Files.write(inputFile, fileContents);

                // Add file to a list of files that were created
                files.add(inputFile);

            }

            var organizer = new DateOrganizer(inputDir.toString(), outputDir.toString(), DateOrganizer.DateFormat.YYYY_MM_DD, false);

            organizer.organizeFiles();


            for (var f : files) {
                // Check if the file was moved to the correct directory
                var expectedFile = Path.of(outputDir + File.separator + "1982-03-15 Vacation Photos" + File.separator + f.getFileName());
                var result = Files.exists(expectedFile);
                Assertions.assertTrue(result);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    void testDateFormat6PreviewMode_YYYY_MM_DD() {

        // Create a file with a date format in its path
        Path inputFile = null;
        try {
            Files.createDirectories(Paths.get(inputDir + File.separator + "1982-03-15 Vacation Photos"));

            List<Path> files = new ArrayList<>();

            for (int i = 1; i <= 100; i++) {
                inputFile = Files.createFile(Path.of(inputDir + File.separator + "1982-03-15 Vacation Photos" + File.separator + "beach" + i + ".jpg"));
                var rnd = new SecureRandom();
                var fileContents = new byte[1024 * 1024];
                rnd.nextBytes(fileContents);
                Files.write(inputFile, fileContents);

                // Add file to a list of files that were created
                files.add(inputFile);

            }

            var organizer = new DateOrganizer(inputDir.toString(), outputDir.toString(), DateOrganizer.DateFormat.YYYY_MM_DD, true);

            organizer.organizeFiles();

            for (var f : files) {
                // Check if the file was moved to the correct directory
                var expectedFile = Path.of(outputDir + File.separator + "1982-03-15 Vacation Photos" + File.separator + f.getFileName());
                var result = Files.exists(expectedFile);
                Assertions.assertFalse(result);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testConstructorWithNullInputDirectory() {
        assertThrows(IllegalArgumentException.class, () -> {
            new DateOrganizer(null, "destinationDirectory", DateOrganizer.DateFormat.YYYY_MM_DD, false);
        });
    }

    @Test
    void testConstructorWithEmptyInputDirectory() {
        assertThrows(IllegalArgumentException.class, () -> {
            new DateOrganizer("", "destinationDirectory", DateOrganizer.DateFormat.YYYY_MM_DD, false);
        });
    }

    @Test
    void testConstructorWithNullDestinationDirectory() {
        assertThrows(IllegalArgumentException.class, () -> {
            new DateOrganizer("inputDirectory", null, DateOrganizer.DateFormat.YYYY_MM_DD, false);
        });
    }

    @Test
    void testConstructorWithEmptyDestinationDirectory() {
        assertThrows(IllegalArgumentException.class, () -> {
            new DateOrganizer("inputDirectory", "", DateOrganizer.DateFormat.YYYY_MM_DD, false);
        });
    }



    @Test
    void testDateFormat1_DD_MM_YYYY() {

        // Create a file with a date format in its path
        Path inputFile = null;
        try {
            Files.createDirectories(Paths.get(inputDir + File.separator + "10-01-2024 Description"));
            inputFile = Files.createFile(Path.of(inputDir + File.separator + "10-01-2024 Description" + File.separator + "TestFile.txt"));


            var rnd = new SecureRandom();
            var fileContents = new byte[1024 * 1024];
            rnd.nextBytes(fileContents);
            Files.write(inputFile, fileContents);

            var organizer = new DateOrganizer(inputDir.toString(), outputDir.toString(), DateOrganizer.DateFormat.DD_MM_YYYY, false);

            organizer.organizeFiles();

            // Check if the file was moved to the correct directory
            var expectedFile = Path.of(outputDir + File.separator + "10-01-2024 Description" + File.separator + inputFile.getFileName());
            var result = Files.exists(expectedFile);
            Assertions.assertTrue(result);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    void testDateFormat2_DD_MM_YYYY() {

        // Create a file with a date format in its path, without the description part
        Path inputFile = null;
        try {
            Files.createDirectories(Paths.get(inputDir + File.separator + "10-01-2024"));
            inputFile = Files.createFile(Path.of(inputDir + File.separator + "10-01-2024" + File.separator + "TestFile.txt"));


            var rnd = new SecureRandom();
            var fileContents = new byte[1024 * 1024];
            rnd.nextBytes(fileContents);
            Files.write(inputFile, fileContents);

            var organizer = new DateOrganizer(inputDir.toString(), outputDir.toString(), DateOrganizer.DateFormat.DD_MM_YYYY, false);

            organizer.organizeFiles();

            // Check if the file was moved to the correct directory
            var expectedFile = Path.of(outputDir + File.separator + "10-01-2024" + File.separator + inputFile.getFileName());
            var result = Files.exists(expectedFile);

            // This should be false, in this case the pattern would not match and modification date would be used
            Assertions.assertFalse(result);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    void testDateFormat3_DD_MM_YYYY() {

        // Create a file in a directory tree, but put the date format in the file name
        Path inputFile = null;
        try {
            Files.createDirectories(Paths.get(inputDir + File.separator + "My Nifty Photos"));
            inputFile = Files.createFile(Path.of(inputDir + File.separator + "My Nifty Photos" + File.separator + "10-03-2023 Vacation.jpg"));


            var rnd = new SecureRandom();
            var fileContents = new byte[1024 * 1024];
            rnd.nextBytes(fileContents);
            Files.write(inputFile, fileContents);

            var organizer = new DateOrganizer(inputDir.toString(), outputDir.toString(), DateOrganizer.DateFormat.DD_MM_YYYY, false);

            organizer.organizeFiles();

            // Check if the file was moved to the correct directory
            var expectedFile = Path.of(outputDir + File.separator + "10-03-2023" + File.separator + inputFile.getFileName());
            var result = Files.exists(expectedFile);

            // This should be false, in this case the pattern would not match and modification date would be used
            Assertions.assertFalse(result);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    void testDateFormat4_DD_MM_YYYY() {

        // Create a file with a date format in its path
        Path inputFile = null;
        try {
            Files.createDirectories(Paths.get(inputDir + File.separator + "15-03-1982 Vacation Photos"));
            inputFile = Files.createFile(Path.of(inputDir + File.separator + "15-03-1982 Vacation Photos" + File.separator + "beach1.jpg"));


            var rnd = new SecureRandom();
            var fileContents = new byte[1024 * 1024];
            rnd.nextBytes(fileContents);
            Files.write(inputFile, fileContents);

            var organizer = new DateOrganizer(inputDir.toString(), outputDir.toString(), DateOrganizer.DateFormat.DD_MM_YYYY, false);

            organizer.organizeFiles();

            // Check if the file was moved to the correct directory
            var expectedFile = Path.of(outputDir + File.separator + "15-03-1982 Vacation Photos" + File.separator + inputFile.getFileName());
            var result = Files.exists(expectedFile);
            Assertions.assertTrue(result);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    void testDateFormat6_DD_MM_YYYY() {

        // Create a file with a date format in its path
        Path inputFile = null;
        try {
            Files.createDirectories(Paths.get(inputDir + File.separator + "15-03-1982 Vacation Photos"));

            List<Path> files = new ArrayList<>();

            for (int i = 1; i <= 100; i++) {
                inputFile = Files.createFile(Path.of(inputDir + File.separator + "15-03-1982 Vacation Photos" + File.separator + "beach" + i + ".jpg"));
                var rnd = new SecureRandom();
                var fileContents = new byte[1024 * 1024];
                rnd.nextBytes(fileContents);
                Files.write(inputFile, fileContents);

                // Add file to a list of files that were created
                files.add(inputFile);

            }

            var organizer = new DateOrganizer(inputDir.toString(), outputDir.toString(), DateOrganizer.DateFormat.DD_MM_YYYY, false);

            organizer.organizeFiles();


            for (var f : files) {
                // Check if the file was moved to the correct directory
                var expectedFile = Path.of(outputDir + File.separator + "15-03-1982 Vacation Photos" + File.separator + f.getFileName());
                var result = Files.exists(expectedFile);
                Assertions.assertTrue(result);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    void testDateFormat6PreviewMode_DD_MM_YYYY() {

        // Create a file with a date format in its path
        Path inputFile = null;
        try {
            Files.createDirectories(Paths.get(inputDir + File.separator + "15-03-1982 Vacation Photos"));

            List<Path> files = new ArrayList<>();

            for (int i = 1; i <= 100; i++) {
                inputFile = Files.createFile(Path.of(inputDir + File.separator + "15-03-1982 Vacation Photos" + File.separator + "beach" + i + ".jpg"));
                var rnd = new SecureRandom();
                var fileContents = new byte[1024 * 1024];
                rnd.nextBytes(fileContents);
                Files.write(inputFile, fileContents);

                // Add file to a list of files that were created
                files.add(inputFile);

            }

            var organizer = new DateOrganizer(inputDir.toString(), outputDir.toString(), DateOrganizer.DateFormat.DD_MM_YYYY, true);

            organizer.organizeFiles();

            for (var f : files) {
                // Check if the file was moved to the correct directory
                var expectedFile = Path.of(outputDir + File.separator + "15-03-1982 Vacation Photos" + File.separator + f.getFileName());
                var result = Files.exists(expectedFile);
                Assertions.assertFalse(result);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}