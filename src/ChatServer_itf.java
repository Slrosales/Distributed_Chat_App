package src;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatServer_itf extends Remote {

    void join(String username, ChatClient_itf client) throws RemoteException;
    // Client joins the chat and sends a remote reference to itself

    void leave(String username) throws RemoteException;
    // Client leaves the chat

    void sendMessage(String username, String text) throws RemoteException;
    // Client sends a message to be broadcast to all participants
}
