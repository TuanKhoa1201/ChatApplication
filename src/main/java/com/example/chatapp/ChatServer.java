package com.example.chatapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChatServer {
    private static List<PrintWriter> clients = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5555)) {
            System.out.println("Server is running on port 5555");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                clients.add(writer);
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (Scanner in = new Scanner(clientSocket.getInputStream())) {
            while (in.hasNextLine()) {
                String message = in.nextLine();
                broadcast(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            removeClient(clientSocket);
        }
    }

    private static void broadcast(String message) {
        for (PrintWriter client : clients) {
            client.println(message);
        }
    }

    private static void removeClient(Socket clientSocket) {
        clients.removeIf(writer -> {
            try {
                return writer.equals(new PrintWriter(clientSocket.getOutputStream(), true));
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        });
        System.out.println("Client disconnected");
    }

}

