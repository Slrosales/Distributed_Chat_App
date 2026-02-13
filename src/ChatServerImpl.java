package src;


import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ChatServerImpl implements ChatServer_itf {

    private Map<String, ChatClient_itf> clients = new ConcurrentHashMap<>();
    // Stores connected clients: username → remote reference

	private volatile boolean shuttingDown = false; // true when server exits with ctrl+c

	public void setShuttingDown(boolean value) {
    	this.shuttingDown = value;
	}


    public synchronized void join(String username, ChatClient_itf client) throws RemoteException {

		// Check for duplicate username
		if (clients.containsKey(username)) {
			throw new RemoteException("Username already taken.");
		}


        clients.put(username, client);
        // Register the client in the server

		Message systemMessage = new Message("SYSTEM", username + " has joined the chat.");

		broadcast(systemMessage, null);

        // Notify all connected clients
    }

    public synchronized void leave(String username) throws RemoteException {

        clients.remove(username);
        // Remove client from the server list

		Message systemMessage = new Message("SYSTEM", username + " has left the chat.");

		broadcast(systemMessage, null);

        // Notify remaining clients
    }

    
    public synchronized void sendMessage(String username, String text) throws RemoteException {
		if (text == null || text.trim().isEmpty()) {
			return;  // Ignore empty messages
		}
		
		Message message = new Message (username, text);
        broadcast(message,message.getSender());
        // Broadcast user's message
    }

    void broadcast(Message message, String excludeUsername) {

        for (Map.Entry<String, ChatClient_itf> entry : clients.entrySet()) {
			String username = entry.getKey();            // Get username
			ChatClient_itf client = entry.getValue();   // Get client reference
			
			 // Skip sender
			// if (excludeUsername != null && username.equals(excludeUsername)) {
			//	continue;
		//	}

			try {
				client.receiveMessage(message);
				// Remote call: message delivered to client JVM
	
			} catch (RemoteException e) {
				if (!shuttingDown) { //if server shutdown with ctrl+c, condition is false
					System.out.println("Client " + username + " disconnected unexpectedly.");
				}

				clients.remove(username); 
				// Remove crashed client from server
			}
		}
    }
}
