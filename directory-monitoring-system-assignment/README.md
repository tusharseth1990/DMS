# Directory Monitor Server
## Overview
The Directory Monitor Server is a Java-based application that monitors a directory for incoming java properties files and processes them. It uses Java 8 features and is modularized for better readability and maintainability.

# Client / Server App
When a new Java properties file appears in the monitored directory, it should process it as follows:
1. Read the file into a Map
2. Apply a regular expression pattern filter for the keys (i.e., remove key/value mappings where keys do not match a configurable regular expression pattern).
3. Relay the filtered mappings to a server program
4. Delete the file

The client program’s main method should accept an argument specifying a config file path. The client config file should contain values defining:
1. the directory path that will be monitored
2. the key filtering pattern that will be applied
3. the address of the corresponding server program
4. any other value(s) you think should be configurable

Also, write a corresponding server program that accepts messages from clients. It should be capable of handling messages sent by multiple clients simultaneously.
Upon receipt of a message from a client, the server should use the message to reconstruct a filtered properties file and write it to disk, using the original filename.

The server program’s main method should accept an argument specifying a config file path.The server config file should contain values defining:
1. the location of the directory to which to write the files
2. what port to listen on
3. any other value(s) you think should be configurable


## Prerequisites
- Java 8 or higher
- Maven

## Configuration
The server requires a configuration file to run. The configuration file should be in the following format:

# Configuration file example
output.directory=/path/to/output/directory
server.port=8080

# How to Run
Clone the repository:  
git clone https://github.com/tusharseth1990/your-repo-name.git
cd your-repo-name/server

# Build the project using Maven:  
mvn clean install
Run the server:  
java -cp target/server-1.0-SNAPSHOT.jar dms.DirectoryMonitorServer /path/to/config.properties
Replace /path/to/config.properties with the path to your configuration file. 