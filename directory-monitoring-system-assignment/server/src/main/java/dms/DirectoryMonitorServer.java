package dms;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class DirectoryMonitorServer {
    private static final Logger logger = Logger.getLogger(DirectoryMonitorServer.class.getName());

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            logger.severe("Usage: java DirectoryMonitorServer <config file path>");
            System.exit(1);
        }

        Properties config = DirectoryMonitorServerUtil.loadConfig(args[0]);
        String outputDirectory = config.getProperty(DirectoryMonitorServerConstant.OUTPUT_DIRECTORY);
        int port = Integer.parseInt(config.getProperty(DirectoryMonitorServerConstant.SERVER_PORT));

        DirectoryMonitorServerUtil.validateConfig(outputDirectory);

        Path outputDir = DirectoryMonitorServerUtil.createOutputDirectory(outputDirectory);
        ExecutorService executorService = Executors.newCachedThreadPool();

        addShutdownHook(executorService);
        startServer(port, outputDir, executorService);
    }

    private static void addShutdownHook(ExecutorService executorService) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executorService.shutdown();
            logger.info("Server shutting down.");
        }));
    }

    private static void startServer(int port, Path outputDir, ExecutorService executorService) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Server listening on port: " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientSocket.setKeepAlive(true);
                logger.info("Client connected: " + clientSocket);

                executorService.submit(() -> handleClient(clientSocket, outputDir));
            }
        }
    }

    static void handleClient(Socket clientSocket, Path outputDir) {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
            Map<String, String> filteredMap = readFilteredMap(in);
            String filename = (String) in.readObject();
            writeToFile(filteredMap, filename, outputDir);
        } catch (EOFException e) {
            logger.warning("Client closed the connection (EOF).");
        } catch (IOException | ClassNotFoundException e) {
            logger.severe("Error handling client: " + e.getMessage());
        }
    }

    private static Map<String, String> readFilteredMap(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Object received = in.readObject();
        if (received instanceof Map) {
            return new HashMap<>((Map<String, String>) received);
        } else {
            throw new IOException("Unexpected object type received.");
        }
    }

    private static void writeToFile(Map<String, String> filteredMap, String filename, Path outputDir) throws IOException {
        Path outputPath = outputDir.resolve(filename);
        try (FileOutputStream fos = new FileOutputStream(outputPath.toFile())) {
            Properties properties = new Properties();
            properties.putAll(filteredMap);
            properties.store(fos, null);
            logger.info("Processed file written to: " + outputPath);
        }
    }
}