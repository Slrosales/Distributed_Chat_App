package src;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatClient_itf extends Remote {

    void receiveMessage(Message message) throws RemoteException;
    // Called by the server to deliver a chat message to this client
}
