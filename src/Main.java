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



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {


    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        var cmdArgsReturn = CommandLineArguments.parse(args);
        if (cmdArgsReturn.isEmpty() || !cmdArgsReturn.get().isValid()) {
            CommandLineArguments.printHelp();
            return;
        }

        var cmdArgs = cmdArgsReturn.get();
        try {
            if (cmdArgs.isPreview()) {
                logger.info("Running in preview mode. No files will be modified.");
            }

            if (cmdArgs.getAction().equals("deduplicate")) {
                var checksumBuilder = new ChecksumBuilder(cmdArgs.getInputDirs(), null);
                checksumBuilder.calculateChecksums();
                var checksumMap = checksumBuilder.getChecksumMap();
                var deduplicator = new DeduplicateFiles(cmdArgs.getOutputDir(), cmdArgs.isPreview());
                deduplicator.copyAndDeduplicateFiles(checksumMap);

            } else if (cmdArgs.getAction().equals("organize")) {
                for (String inputDir : cmdArgs.getInputDirs()) {
                    var dateOrganizer = new DateOrganizer(inputDir, cmdArgs.getOutputDir(), cmdArgs.getDateFormat(), cmdArgs.isPreview());
                    dateOrganizer.organizeFiles();
                }
            }
        } catch (IOException e) {
            logger.error("An error occurred while processing files: {}", e.getMessage());
        }
    }
}