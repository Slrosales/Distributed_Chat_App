package src;

import java.rmi.RemoteException;

public class ChatClientImpl implements ChatClient_itf {

    // private String username;

    public ChatClientImpl(String username) {
        // this.username = username;
        // Store the username
    }

    public void receiveMessage(Message message) throws RemoteException {
        // Hide the server's ping messages from the terminal
        if (message.getContent().contains("Checking if you are still alive")) {
            return;
        }
        System.out.println(message.toString());
        // Display message on client terminal

        if (message.getContent().equals("Server is shutting down")) {
            System.out.println("Disconnecting...");
            System.exit(0);
        }

    }
}
