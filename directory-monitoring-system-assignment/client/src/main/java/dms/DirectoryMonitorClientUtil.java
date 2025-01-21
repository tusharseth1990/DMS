package dms;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.Properties;
import java.util.logging.Logger;

public class DirectoryMonitorClientUtil {
    private static final Logger logger = Logger.getLogger(DirectoryMonitorClientUtil.class.getName());

    public static Properties loadConfig(String configFilePath) throws IOException {
        Properties config = new Properties();
        try (FileInputStream fis = new FileInputStream(configFilePath)) {
            config.load(fis);
        }
        return config;
    }

    public static void validateConfig(String directoryPath, String keyPattern, String serverAddress) {
        if (directoryPath == null || keyPattern == null || serverAddress == null) {
           logger.severe("Missing configuration properties.");
            System.exit(1);
        }
    }

    public static void validateDirectory(Path dir) {
        if (!Files.isDirectory(dir)) {
           logger.severe("Invalid directory path: " + dir);
            System.exit(1);
        }
    }

    public static void closeWatchService(WatchService watchService) {
        try {
            watchService.close();
        } catch (IOException e) {
            logger.severe("Error closing watch service: " + e.getMessage());
        }
    }
}
