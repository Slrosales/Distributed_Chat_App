package src;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;

public class HelloClient implements Info_itf{
	private String name;

	public HelloClient(String name) {
		this.name = name;
	}

	public String getName() throws RemoteException {
		return this.name;
	}
  public static void main(String [] args) {
	
	try {
	  if (args.length < 2) {
	   System.out.println("Usage: java HelloClient <rmiregistry host> <rmiregistry port>");
	   return;}

	String host = args[0];
	int port = Integer.parseInt(args[1]);

	Registry registry = LocateRegistry.getRegistry(host, port); 
	Hello h = (Hello) registry.lookup("HelloService");

	// Remote method invocation
	HelloClient client = new HelloClient("Laura");
	Info_itf stub = (Info_itf) UnicastRemoteObject.exportObject(client, 0);
	String res = h.sayHello(stub);
	System.out.println(res);

	} catch (Exception e)  {
		System.err.println("Error on client: " + e);
	}
  }
}
