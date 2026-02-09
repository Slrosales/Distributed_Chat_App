package src;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Info_itf extends Remote {
    String getName() throws RemoteException; 
}
