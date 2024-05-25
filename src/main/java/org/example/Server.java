package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {
    private BufferedReader clientReader;
    private PrintWriter writer;
    private Socket clientSocket;
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    public void run(int port) {
        try {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                logger.info("Waiting for client on port " + port);
                clientSocket = serverSocket.accept();
                logger.info("Client is connected.");

                initializeStreams();

                answerClient("Hello!");

                String clientLine = readClient();

                boolean clientValidation = checkClientsMessages(clientLine);
                if (!clientValidation) {
                    askClientAboutPalianytsia();

                    clientLine = readClient();

                    boolean palianytsiaCheck = checkClientsAnswer(clientLine);
                    if (palianytsiaCheck) {
                        answerClient("Answer is correct, local date & time: " + LocalDateTime.now());
                    } else {
                        answerClient("Answer is not correct, connection aborted.");
                    }
                } else {
                    answerClient("Connection aborted.");
                }
            } catch (IOException e) {
                logger.warning("Error: " + e.getMessage());
            } finally {
                closeResources();
            }
        } catch (IOException e) {
            logger.warning("Error: " + e.getMessage());
        }
    }

    void initializeStreams() throws IOException {
        writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
        clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    void askClientAboutPalianytsia() {
        answerClient("What is \"паляниця\" ?");
    }

    String readClient() throws IOException {
        System.out.println("Waiting for client message.");
        return clientReader.readLine();
    }

    void answerClient(String serverLine) {
        writer.println(serverLine);
    }

    boolean checkClientsMessages(String clientLine) {
        Pattern pattern = Pattern.compile("[ыэъё]");
        Matcher matcher = pattern.matcher(clientLine);
        if (matcher.find()) {
            logger.info("Wrong language.");
            return false;
        }
        logger.info("Correct language.");
        return true;
    }

    boolean checkClientsAnswer(String clientLine) {
        if (!clientLine.equalsIgnoreCase("хліб")) {
            logger.info(("Answer is incorrect, connection aborted."));
            return false;
        }
        logger.info("Answer is correct.");
        return true;
    }

    void closeResources() throws IOException {
        clientSocket.close();
        clientReader.close();
        writer.close();
    }
}
