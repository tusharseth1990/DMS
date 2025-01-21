package dms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.mockito.Mockito.*;

class DirectoryMonitorServerTest {
    private DirectoryMonitorServer server;
    private Socket clientSocket;
    private Path outputDir;

    @BeforeEach
    void setUp() throws IOException {
        server = new DirectoryMonitorServer();
        clientSocket = mock(Socket.class);
        outputDir = Files.createTempDirectory("outputDir");
    }

    @Test
    void testHandleClient() throws IOException, ClassNotFoundException {
        System.out.println("Starting testHandleClient");

        // Prepare test data
        Map<String, String> filteredMap = new HashMap<>();
        filteredMap.put("config.key1", "value1");
        String filename = "test.properties";

        // Create a temporary file with the test data
        File tempFile = File.createTempFile("test", ".tmp");
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(tempFile))) {
            objectOutputStream.writeObject(filteredMap);
            objectOutputStream.writeObject(filename);
        }

        // Mock the InputStream to read from the temporary file
        InputStream inputStream = new FileInputStream(tempFile);
        when(clientSocket.getInputStream()).thenReturn(inputStream);

        // Call the handleClient method
        System.out.println("Calling handleClient");
        DirectoryMonitorServer.handleClient(clientSocket, outputDir);
        System.out.println("handleClient returned");

        // Verify that the file was written correctly
        Path outputPath = outputDir.resolve(filename);
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(outputPath.toFile())) {
            properties.load(fis);
        }

        assert properties.getProperty("config.key1").equals("value1");

        // Clean up temporary files
        Files.deleteIfExists(outputPath);
        Files.deleteIfExists(outputDir);
        tempFile.delete();

        System.out.println("testHandleClient completed");
    }
}