package com.example.chatapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient extends Application {
    private PrintWriter out;
    private TextArea chatArea;
    private TextField messageInput;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Chat Application NTK");
        chatArea = new TextArea();
        chatArea.setEditable(false);
        messageInput = new TextField();
        messageInput.setOnAction(e -> sendMessage());
        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage());
        VBox vBox = new VBox(chatArea, messageInput, sendButton);
        primaryStage.setScene(new Scene(vBox, 400, 300));
        primaryStage.setOnCloseRequest(event -> closeApplication());
        primaryStage.show();
        connectToServer();
    }

    private void sendMessage() {
        String message = messageInput.getText();
        if (!message.isEmpty()) {
            out.println(message);
            messageInput.clear();
        }
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket("localhost", 5555);
            out = new PrintWriter(socket.getOutputStream(), true);
            new Thread(() -> {
                try (Scanner in = new Scanner(socket.getInputStream())) {
                    while (in.hasNextLine()) {
                        String message = in.nextLine();
                        Platform.runLater(() -> chatArea.appendText(message + "\n"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeApplication() {
        if (out != null) out.close();
        Platform.exit();
        System.exit(0);
    }
}

