package src;

import java.rmi.server.*;
import java.util.Scanner;
import java.rmi.registry.*;

public class ChatServerMain {
    private static ChatServerImpl server;

    public static void main(String[] args) {

        try {
            server = new ChatServerImpl();
            // Create the server object

            ChatServer_itf serverStub = (ChatServer_itf) UnicastRemoteObject.exportObject(server, 0);
            // Export server so it can receive remote calls

            // Register the remote object in RMI registry with a given identifier
            Registry registry = null;
            if (args.length > 0)
                registry = LocateRegistry.getRegistry(Integer.parseInt(args[0]));
            else
                registry = LocateRegistry.getRegistry();

            registry.rebind("ChatService", serverStub);
            // Bind server under the name "ChatService"

            System.out.println("Chat server is running...");

            // Handle Ctrl+C
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                shutdown();
            }));

            // Manual shutdown command
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.println("Type /exit to stop the server");
                while (scanner.hasNextLine()) {
                    String input = scanner.nextLine();
                    if (input.equalsIgnoreCase("/exit")) {
                        shutdown();
                        System.exit(0);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void shutdown() {

        try {
            System.out.println("Shutting down server...");
            server.setShuttingDown(true);
            server.saveHistory(); // Save chat history to disk
            // Notify clients
            server.broadcast(new Message("SEVER", "Server is shutting down"), null);

            Registry registry = LocateRegistry.getRegistry(3000);
            registry.unbind("ChatService");

            UnicastRemoteObject.unexportObject(server, true); // closes RMI

            System.out.println("Server shut down successfully");

        } catch (Exception e) {
            System.out.println("Shutdown already completed");
        }
    }

}
