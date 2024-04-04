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

package com.sonorantech.mediaorganizer;

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

/**
 * Class that implements the IChecksumBuilder interface to calculate checksums for files using MD5.
 */
public class ChecksumBuilder implements IChecksumBuilder {

    // Logger for logging information and error messages
    private static final Logger logger = LogManager.getLogger(ChecksumBuilder.class);

    // Constant for the number of bytes to read at a time when calculating checksums
    public static final int BYTES_TO_READ = 1024;

    // List of directories to scan for files
    private final List<String> directories;

    // Map to store the calculated checksums and their corresponding files
    private final Map<String, List<AbstractMap.SimpleEntry<String, File>>> checksumMap = new HashMap<>();

    /**
     * Constructor for the ChecksumBuilder class.
     *
     * @param directories List of directories to scan for files.
     */
    public ChecksumBuilder(List<String> directories) {
        this.directories = Objects.requireNonNullElseGet(directories, ArrayList::new);
    }

    /**
     * Retrieves a map of checksums and their corresponding files.
     *
     * @return a Map where the key is a checksum (String) and the value is a List of SimpleEntry objects.
     * Each SimpleEntry contains the directory name (String) and the corresponding File.
     */
    @Override
    public Map<String, List<AbstractMap.SimpleEntry<String, File>>> getChecksumMap() {
        return this.checksumMap;
    }

    /**
     * Calculates checksums for all files in the directories specified in the implementing class.
     *
     * @throws IOException if an I/O error occurs during the checksum calculation.
     */
    @Override
    public void calculateChecksums() throws IOException {
        for (String dirName : directories) {
            logger.info("Iterating over files in directory: {}", dirName);
            Files.walk(Paths.get(dirName)).forEach(path -> handleFile(dirName, path.toFile()));
        }
    }

    /**
     * Handles each file in the directory. If the file is not a directory, it calculates the checksum.
     *
     * @param inputDirName The name of the directory being scanned.
     * @param f The file for which the checksum is to be calculated.
     */
    private void handleFile(String inputDirName, File f)  {
        if (!f.isDirectory()) {
            logger.info("File: {}", f.getAbsolutePath());
            var checksumBytes = checksum(f);

            if (checksumBytes != null) {
                var checksum = toHexString(checksumBytes);
                logger.info("Checksum: {}", checksum);
                if (!checksumMap.containsKey(checksum)) {
                    checksumMap.put(checksum, new ArrayList<>());
                }
                checksumMap.get(checksum).add(new SimpleEntry<String, File>(inputDirName, f));
            }
        } else {
            logger.info("Skipped because it is a directory: {}", f.getAbsolutePath());
        }
    }

    /**
     * Converts a byte array to a hexadecimal string.
     *
     * @param bytes The byte array to be converted.
     * @return The hexadecimal string representation of the byte array.
     */
    private static String toHexString(byte[] bytes) {
        var result = new StringBuilder();
        for(byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    /**
     * Calculates the checksum for a file.
     *
     * @param f The file for which the checksum is to be calculated.
     * @return The calculated checksum as a byte array.
     */
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
            logger.info("Caught exception during checksum: {}", e.getMessage());
            return null;
        }
    }
}
