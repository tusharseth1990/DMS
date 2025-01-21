package dms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchService;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class DirectoryMonitorClientUtilTest {

    @TempDir
    Path tempDir;

    @Test
    void testLoadConfig() throws IOException {
        File configFile = tempDir.resolve("config.properties").toFile();
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write("key=value\n");
        }

        Properties config = DirectoryMonitorClientUtil.loadConfig(configFile.getAbsolutePath());
        assertEquals("value", config.getProperty("key"));
    }

    @Test
    void testValidateConfig() {
        assertDoesNotThrow(() -> DirectoryMonitorClientUtil.validateConfig("path", "pattern", "address"));
    }


    @Test
    void testValidateDirectory() {
        assertDoesNotThrow(() -> DirectoryMonitorClientUtil.validateDirectory(tempDir));
    }


    @Test
    void testCloseWatchService() throws IOException {
        WatchService watchService = tempDir.getFileSystem().newWatchService();
        assertDoesNotThrow(() -> DirectoryMonitorClientUtil.closeWatchService(watchService));
    }
}