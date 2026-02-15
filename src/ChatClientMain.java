package src;

import java.rmi.*; 
import java.rmi.server.*; 
import java.rmi.registry.*;
import java.util.Scanner;

public class ChatClientMain {

    public static void main(String[] args) {

        try {
			if (args.length < 1) {
				System.out.println("Usage: java ChatClientMain <username>");
				return;
			}	
            String username = args[0];
            // Username passed as command-line argument

            ChatClientImpl client = new ChatClientImpl(username);
            // Create client object

            ChatClient_itf clientStub = (ChatClient_itf) UnicastRemoteObject.exportObject(client, 0);
            // Export client so server can call it

            Registry registry = LocateRegistry.getRegistry("localhost", 3000);
            // Connect to the server's RMI registry

            ChatServer_itf server = (ChatServer_itf) registry.lookup("ChatService");
            // Lookup the chat server

            try {
                server.join(username, clientStub);
            } catch (RemoteException e) {
                System.out.println("ERROR: " + e.getMessage());
                System.exit(1);  // Stop client
            }

            Scanner scanner = new Scanner(System.in);

            while (true) {
               try{ //try/catch is just to check if server has disconnected
                    String text = scanner.nextLine();
                    // Read input from user

                    if (text.trim().isEmpty()) {
                        continue;  // Ignore empty input
                    }

                    if (text.equalsIgnoreCase("/leave")) {
                        server.leave(username);
                        // Notify server that client leaves
                        break;
                    }
				    //Message message = new Message(username, text);
                    server.sendMessage(username, text);
                    // Send message to server
                }

                catch (RemoteException e) {
                    System.out.println("Server disconnected. Exiting...");
                    System.exit(0);
                }
            }

            scanner.close();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
