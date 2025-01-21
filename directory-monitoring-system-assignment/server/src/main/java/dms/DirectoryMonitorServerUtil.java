package dms;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Logger;

public class DirectoryMonitorServerUtil {
    private static final Logger logger = Logger.getLogger(DirectoryMonitorServerUtil.class.getName());

    public static Properties loadConfig(String configFilePath) throws IOException {
        Properties config = new Properties();
        try (FileInputStream fis = new FileInputStream(configFilePath)) {
            config.load(fis);
        }
        return config;
    }

    public static void validateConfig(String outputDirectory) {
        if (outputDirectory == null) {
            logger.severe("Missing configuration properties.");
            System.exit(1);
        }
    }

    public static Path createOutputDirectory(String outputDirectory) throws IOException {
        Path outputDir = Paths.get(outputDirectory);
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }
        return outputDir;
    }
}