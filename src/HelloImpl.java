package src;

import java.lang.ProcessHandle.Info;
import java.rmi.*;

public  class HelloImpl implements Hello {

	private String message;
 
	public HelloImpl(String s) {
		message = s ;
	}

	public String sayHello(Info_itf client) throws RemoteException {
		System.out.println(client.getName() + " is calling sayHello()");
		return message + " " + client.getName();
	}
}

