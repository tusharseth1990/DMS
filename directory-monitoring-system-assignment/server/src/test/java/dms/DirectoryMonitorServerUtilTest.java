package dms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class DirectoryMonitorServerUtilTest {

    @TempDir
    Path tempDir;

    @Test
    void testLoadConfig() throws IOException {
        File configFile = tempDir.resolve("config.properties").toFile();
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write("key=value\n");
        }

        Properties config = DirectoryMonitorServerUtil.loadConfig(configFile.getAbsolutePath());
        assertEquals("value", config.getProperty("key"));
    }

    @Test
    void testValidateConfig() {
        assertDoesNotThrow(() -> DirectoryMonitorServerUtil.validateConfig("outputDirectory"));
    }

    @Test
    void testCreateOutputDirectory() throws IOException {
        Path outputDir = tempDir.resolve("outputDir");
        assertDoesNotThrow(() -> DirectoryMonitorServerUtil.createOutputDirectory(outputDir.toString()));
        assertTrue(Files.exists(outputDir));
    }

    @Test
    void testCreateOutputDirectoryWhenAlreadyExists() throws IOException {
        Path outputDir = tempDir.resolve("outputDir");
        Files.createDirectory(outputDir);
        assertDoesNotThrow(() -> DirectoryMonitorServerUtil.createOutputDirectory(outputDir.toString()));
        assertTrue(Files.exists(outputDir));
    }
}