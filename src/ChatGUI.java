package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

public class ChatGUI extends JFrame implements ChatClient_itf {

    // GUI Components
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;

    // RMI Variables
    private ChatServer_itf server;
    private String username;

    public ChatGUI(String username, String host, int port) {
        this.username = username;

        // Window
        setTitle("Group Chat - " + username);
        setSize(600, 800);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Listener to catch the X button click
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                quitChat();
            }
        });

        setLayout(new BorderLayout());

        // Chat History
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        // Input Panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Action Listeners
        ActionListener sendAction = e -> sendMessage();
        sendButton.addActionListener(sendAction);
        inputField.addActionListener(sendAction);

        connectToServer(host, port);

        setVisible(true);
    }

    private void quitChat() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to leave the chat?", "Confirm Exit",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                server.leave(username); // Notify server we're leaving
            } catch (RemoteException e) {
                // If we can't contact the server, just exit anyway
                System.out.println("Server already down.");

            }
            System.exit(0); // Exit the application
        }
    }

    private void connectToServer(String host, int port) {
        try {
            // Export this GUI as the RMI Client Object
            ChatClient_itf clientStub = (ChatClient_itf) UnicastRemoteObject.exportObject(this, 0);

            Registry registry = LocateRegistry.getRegistry(host, port);
            server = (ChatServer_itf) registry.lookup("ChatService");

            // Join the chat
            server.join(username, clientStub);

            // Get History
            java.util.List<Message> history = server.getHistory();
            for (Message m : history) {
                chatArea.append(m.toString() + "\n");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error connecting: " + e.getMessage());
            System.exit(1);
        }
    }

    private void sendMessage() {
        String text = inputField.getText();
        if (text.trim().isEmpty())
            return;

        // Check for the exit command
        if (text.equalsIgnoreCase("/leave")) {
            quitChat(); 
            return;
        }

        try {
            server.sendMessage(username, text);
            inputField.setText("");
        } catch (RemoteException e) {
            chatArea.append("[SYSTEM] Connection lost!\n");
            JOptionPane.showMessageDialog(this, "Server connection lost\nClosign Chat App...");
            System.exit(0); // Exit the application
        }
    }

    // RMI CALLBACK from ChatClient_itf
    @Override
    public void receiveMessage(Message message) throws RemoteException {
        // Ignore "Checking if alive" pings
        if (message.getContent().contains("Checking if username is taken"))
            return;

        // Append message to the GUI
        chatArea.append(message.toString() + "\n");

        // Auto-scroll to bottom
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Remember to introduce a username and a port number as a command-line argument");
            System.out.println(
                    "Example: java -cp classes src.ChatGUI <username> <port>\nor\n./run_gui.sh <username> <port>");
            return;
        }

    String username = args[0];
    
    // DEFAULT VALUES
    String host = "localhost";
    int port = 3000; 

    // OVERRIDE if provided
    if (args.length >= 2) {
        host = args[1];
    }
    if (args.length >= 3) {
        try {
            port = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid port. Using default 3000.");
        }
    }

    final String finalHost = host;
    final int finalPort = port;

    SwingUtilities.invokeLater(() -> {
        new ChatGUI(username, finalHost, finalPort);
    });
}
}