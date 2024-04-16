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

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CommandLineArgumentsTest {

    @Test
    void testValidArguments() {
        String[] args = {"-a", "deduplicate", "-i", "inputDir1", "-o", "outputDir", "-d", "YYYYMMDD", "-p"};
        Optional<CommandLineArguments> cmdArgs = CommandLineArguments.parse(args);
        assertTrue(cmdArgs.isPresent());
        assertEquals("deduplicate", cmdArgs.get().getAction());
        assertEquals("inputDir1", cmdArgs.get().getInputDirs().get(0));
        assertEquals("outputDir", cmdArgs.get().getOutputDir());
        assertEquals(DateOrganizer.DateFormat.YYYY_MM_DD, cmdArgs.get().getDateFormat());
        assertTrue(cmdArgs.get().isPreview());
    }

    @Test
    void testInvalidAction() {
        String[] args = {"-a", "invalidAction", "-i", "inputDir1", "-o", "outputDir", "-d", "YYYYMMDD", "-p"};
        Optional<CommandLineArguments> cmdArgs = CommandLineArguments.parse(args);
        assertTrue(cmdArgs.isPresent());
        assertFalse(cmdArgs.get().isValid());
    }

    @Test
    void testMissingInputDir() {
        String[] args = {"-a", "deduplicate", "-o", "outputDir", "-d", "YYYYMMDD", "-p"};
        Optional<CommandLineArguments> cmdArgs = CommandLineArguments.parse(args);
        assertTrue(cmdArgs.isPresent());
        assertFalse(cmdArgs.get().isValid());
    }

    @Test
    void testMissingOutputDir() {
        String[] args = {"-a", "deduplicate", "-i", "inputDir1", "-d", "YYYYMMDD", "-p"};
        Optional<CommandLineArguments> cmdArgs = CommandLineArguments.parse(args);
        assertTrue(cmdArgs.isPresent());
        assertFalse(cmdArgs.get().isValid());
    }


    @Test
    void testEmptyArguments() {
        String[] args = {};
        Optional<CommandLineArguments> cmdArgs = CommandLineArguments.parse(args);
        assertFalse(cmdArgs.isPresent());
    }

    @Test
    void testNullArguments() {
        String[] args = null;
        Optional<CommandLineArguments> cmdArgs = CommandLineArguments.parse(args);
        assertFalse(cmdArgs.isPresent());
    }

    @Test
    void testValidOrganizeActionWithValidDateFormat() {
        String[] args = {"-a", "organize", "-i", "inputDir1", "-o", "outputDir", "-d", "YYYYMMDD", "-p"};
        Optional<CommandLineArguments> cmdArgs = CommandLineArguments.parse(args);
        assertTrue(cmdArgs.isPresent());
        assertEquals("organize", cmdArgs.get().getAction());
        assertEquals("inputDir1", cmdArgs.get().getInputDirs().get(0));
        assertEquals("outputDir", cmdArgs.get().getOutputDir());
        assertEquals(DateOrganizer.DateFormat.YYYY_MM_DD, cmdArgs.get().getDateFormat());
    }

    @Test
    void testValidOrganizeActionWithInvalidDateFormat() {
        String[] args = {"-a", "organize", "-i", "inputDir1", "-o", "outputDir", "-d", "invalidDateFormat", "-p"};
        Optional<CommandLineArguments> cmdArgs = CommandLineArguments.parse(args);
        assertTrue(cmdArgs.isPresent());
    }

    @Test
    void testValidOrganizeActionWithDifferentDateFormat() {
        String[] args = {"-a", "organize", "-i", "inputDir1", "-o", "outputDir", "-d", "DDMMYYYY", "-p"};
        Optional<CommandLineArguments> cmdArgs = CommandLineArguments.parse(args);
        assertTrue(cmdArgs.isPresent());
        assertEquals("organize", cmdArgs.get().getAction());
        assertEquals("inputDir1", cmdArgs.get().getInputDirs().get(0));
        assertEquals("outputDir", cmdArgs.get().getOutputDir());
        assertEquals(DateOrganizer.DateFormat.DD_MM_YYYY, cmdArgs.get().getDateFormat());
        assertTrue(cmdArgs.get().isPreview());
    }
}