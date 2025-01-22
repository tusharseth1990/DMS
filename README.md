# DMS

# Directory Monitor System
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
1. Please install Java Development Kit (JDK) (version 8 or higher). Try v17 for better compatibility.
2. Please make sure you have Apache Maven installed.
3. Please ensure you have the necessary permissions to create and delete files and directories.
Note: In some version,you might face issues then try commenting test files, 

# How to clone
Clone the repository:  
git clone https://github.com/tusharseth1990/DMS.git
or download the zip file.

# Building the Application
Open a terminal and navigate to the root directory of the project.
Run the following command to build the project using Maven:

mvn clean install

# Running the Server

Navigate to the server/target directory:
cd server/target

Run the server with the configuration file path as an argument:

java -jar directory-monitor-server.jar <path-to-config-file>
Replace <path-to-config-file> with the actual path to your configuration file.

Configuration File For Server
Ensure your configuration file (e.g., config.properties) contains the necessary properties:

outputDirectory=/path/to/output/directory
serverPort=portno

Example: 
output.directory=dms
server.port=12345

Note: make sure to give write permission to this directory i.e. 
$ chmod 755 dms


# Running the Client
1. Ensure the server is running.
2. Compile and run the client:
javac DirectoryMonitorClient.java
Example: java DirectoryMonitorClient src/main/resources/client.properties

Configuration File for the client: 
Needs directory.path value 

Example: In the current project, I have used
directory.path = src/main/resources

So when we run both server and client, we need to upload a single/ multiple java properties file in the directory i.e. example.properties in src/main/resources folder to see the live working of the application.

# Sample Example Properties File (example.properties)
config.name=example
config.version=1.0
debug.enabled=true
other.unrelated.key=shouldNotAppear

Kindly upload it to the directory.path folder location. For understanding, I am uploading the sample example.properties as per the sample.

Also while running the client make sure the command line argument is passed.
Attaching local config screenshots
<img width="1034" alt="image" src="https://github.com/user-attachments/assets/b79336eb-e4af-4c4d-a13f-04c5bd181e62" />

<img width="1033" alt="image" src="https://github.com/user-attachments/assets/7600502d-469d-4c77-9ccd-cde373db65c5" />

