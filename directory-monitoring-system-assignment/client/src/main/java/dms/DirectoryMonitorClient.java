package dms;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.*;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DirectoryMonitorClient {
    private static final Logger logger = Logger.getLogger(DirectoryMonitorClient.class.getName());

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            logger.severe("Usage: java DirectoryMonitorClient src/main/resources");
            System.exit(1);
        }

        Properties config = DirectoryMonitorClientUtil.loadConfig(args[0]);
        String directoryPath = config.getProperty(DirectoryMonitorClientConstant.DIRECTORY_PATH);
        String keyPattern = config.getProperty(DirectoryMonitorClientConstant.KEY_PATTERN);
        String serverAddress = config.getProperty(DirectoryMonitorClientConstant.SERVER_ADDRESS);
        int serverPort = Integer.parseInt(config.getProperty(DirectoryMonitorClientConstant.SERVER_PORT));

        DirectoryMonitorClientUtil.validateConfig(directoryPath, keyPattern, serverAddress);

        Pattern pattern = Pattern.compile(keyPattern);
        Path dir = Paths.get(directoryPath);
        DirectoryMonitorClientUtil.validateDirectory(dir);

        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            logger.info("Monitoring directory: " + directoryPath);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> DirectoryMonitorClientUtil.closeWatchService(watchService)));

            processEvents(watchService, dir, pattern, serverAddress, serverPort);
        }
    }

    private static void processEvents(WatchService watchService, Path dir, Pattern pattern, String serverAddress, int serverPort) {
        while (true) {
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException | ClosedWatchServiceException e) {
                Thread.currentThread().interrupt();
                break;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    Path filePath = dir.resolve((Path) event.context());
                    if (filePath.toString().endsWith(DirectoryMonitorClientConstant.PROPERTIES)) {
                        processFile(filePath, pattern, serverAddress, serverPort);
                    }
                }
            }

            if (!key.reset()) {
                break;
            }
        }
    }

    static void processFile(Path filePath, Pattern pattern, String serverAddress, int serverPort) {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(filePath.toFile())) {
            properties.load(fis);
        } catch (IOException e) {
            logger.severe("Error reading file: " + filePath);
            return;
        }

        Map<String, String> filteredMap = properties.entrySet().stream()
                .filter(entry -> pattern.matcher(entry.getKey().toString()).matches())
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        entry -> entry.getValue().toString()
                ));

        if (filteredMap.isEmpty()) {
            logger.severe("Filtered map is empty!");
            return;
        }

        sendToServer(filteredMap, filePath.getFileName().toString(), serverAddress, serverPort);
        deleteFile(filePath);
    }

    private static void sendToServer(Map<String, String> filteredMap, String filename, String serverAddress, int serverPort) {
        try (Socket socket = new Socket(serverAddress, serverPort);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            logger.info("Connected to server: " + serverAddress + ":" + serverPort);

            out.writeObject(filteredMap);
            out.writeObject(filename);
            out.flush();

            logger.info("Data sent to server successfully.");
        } catch (IOException e) {
            logger.severe("Error sending data to server: " + e.getMessage());
        }
    }

    private static void deleteFile(Path filePath) {
        try {
            Files.delete(filePath);
            logger.info("File deleted: " + filePath);
        } catch (IOException e) {
            logger.severe("Error deleting file: " + filePath);
        }
    }
}