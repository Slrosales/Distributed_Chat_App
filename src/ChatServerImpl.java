package src;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServerImpl implements ChatServer_itf {

	public ChatServerImpl() {
		loadHistory();
	}

	private Map<String, ChatClient_itf> clients = new ConcurrentHashMap<>();
	// Stores connected clients: username → remote reference

	private List<Message> history = new ArrayList<>(); // Stores chat history

	private volatile boolean shuttingDown = false; // true when server exits with ctrl+c

	private static final String HISTORY_FILE = "history.bin"; // File to persist chat history

	public void setShuttingDown(boolean value) {
		this.shuttingDown = value;
	}

	public synchronized void join(String username, ChatClient_itf client) throws RemoteException {

		// Check for duplicate username
		if (clients.containsKey(username)) {
			boolean isAlive = true;
			try {
				ChatClient_itf oldClient = clients.get(username);
				oldClient.receiveMessage(new Message("Server", "Checking if username is taken..."));

			} catch (RemoteException e) {
				isAlive = false;
			}

			if (isAlive){
				throw new RemoteException("Username already taken.");

			} else {
				clients.remove(username);
			}
		}

		clients.put(username, client);
		// Register the client in the server

		Message systemMessage = new Message("Server", username + " has joined the chat.");
		// history.add(systemMessage);

		broadcast(systemMessage, null);
		// Notify all connected clients
	}

	public synchronized void leave(String username) throws RemoteException {

		clients.remove(username);
		// Remove client from the server list

		Message systemMessage = new Message("Server", username + " has left the chat.");

		broadcast(systemMessage, null);

		// Notify remaining clients
	}

	public synchronized void sendMessage(String username, String text) throws RemoteException {
		if (text == null || text.trim().isEmpty()) {
			return; // Ignore empty messages
		}

		Message message = new Message(username, text);
		history.add(message);
		saveHistory();
		broadcast(message, message.getSender());
		// Broadcast user's message
	}

	// Return a copy of the history to avoid concurrent modification issues
	public synchronized List<Message> getHistory() throws RemoteException {
		return new ArrayList<>(history);
	}

	void broadcast(Message message, String excludeUsername) {

		for (Map.Entry<String, ChatClient_itf> entry : clients.entrySet()) {
			String username = entry.getKey(); // Get username
			ChatClient_itf client = entry.getValue(); // Get client reference

			// Skip sender
			// if (excludeUsername != null && username.equals(excludeUsername)) {
			// continue;
			// }

			try {
				client.receiveMessage(message);
				// Remote call: message delivered to client JVM

			} catch (RemoteException e) {
				if (!shuttingDown) { // if server shutdown with ctrl+c, condition is false
					System.out.println("Client " + username + " disconnected unexpectedly.");
				}

				clients.remove(username);
				// Remove crashed client from server
			}
		}
	}

	public synchronized void saveHistory() {
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(HISTORY_FILE))) {
			// Write the entire history list to the file
			out.writeObject(this.history);
			System.out.println("Chat history saved");
		} catch (IOException e) {
			System.out.println("Failed to save chat history: " + e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public void loadHistory() {
		File file = new File(HISTORY_FILE);
		if (!file.exists())
			return; // No history to load

		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
			this.history = (List<Message>) in.readObject();
			System.out.println("Chat history loaded");
		} catch (Exception e) {
			System.err.println("Could not load history: " + e.getMessage());
		}
	}
}
