package dms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import static org.mockito.Mockito.*;

class DirectoryMonitorClientTest {
    private DirectoryMonitorClient client;
    private Pattern pattern;
    private String serverAddress;
    private int serverPort;

    @BeforeEach
    void setUp() {
        client = new DirectoryMonitorClient();
        pattern = Pattern.compile("^config\\..*");
        serverAddress = "127.0.0.1";
        serverPort = 12345;
    }

    @Test
    void testProcessFileWithFile() throws IOException {
        // Create a temporary file with test data
        Path tempFile = Files.createTempFile("test", ".properties");
        Files.write(tempFile, "config.key1=value1\nother.key2=value2\n".getBytes());

        // Call the processFile method
        client.processFile(tempFile, pattern, serverAddress, serverPort);

        // Clean up temporary file
        Files.deleteIfExists(tempFile);

        // Add assertions or verify behavior
    }


}
