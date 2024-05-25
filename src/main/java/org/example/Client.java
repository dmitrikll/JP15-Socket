package org.example;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Logger;

public class Client {
    private BufferedReader serverReader;
    private PrintWriter writer;
    private Scanner scanner;
    private boolean serverStopped = false;
    private static final Logger logger = Logger.getLogger(Client.class.getName());

    public void run(String host, int port) {
        try (Socket clientSocket = new Socket(host, port)) {
            writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
            serverReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            scanner = new Scanner(System.in);

            readServer();
            answerServer();
            scanner.close();

            if (!serverStopped) {
                answerServer();
                readServer();
            }
        } catch (UnknownHostException e) {
            logger.warning("Server not found.");
        } catch (IOException e) {
            logger.warning("Error: " + e.getMessage());
        } finally {
            closeResources();
        }
    }

    void readServer() throws IOException {
        System.out.println("Waiting for a notification from the server.");
        String serverLine = serverReader.readLine();
        System.out.println("Server: " + serverLine);
        if (serverLine.contains("Connection will be aborted.")) {
            serverStopped = true;
        }
    }

    void answerServer() {
        System.out.println("Write to the server.");
        String clientLine = scanner.nextLine();
        writer.println(clientLine);
    }

    void closeResources() {
        try {
            if (writer != null) {
                serverReader.close();
                writer.close();
                scanner.close();
            }
        } catch (IOException e) {
            logger.warning("Error while closing resources: " + e.getMessage());
        }
    }
}
