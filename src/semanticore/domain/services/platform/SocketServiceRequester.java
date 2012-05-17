package semanticore.domain.services.platform;

import semanticore.domain.SemantiCore;
import semanticore.domain.model.*;

import java.net.*;
import java.io.*;

public class SocketServiceRequester extends ServiceRequester {

    public SocketServiceRequester(Service s) {
	super(s);
    }

    @Override
    public void request(Message sm, String address, String port) {
	sendMessage(sm, address, port);
    }

    private void sendMessage(Message m, String address, String port) {
	try {
	    Socket socket = new Socket(address, Integer.parseInt(port));

	    String machine = socket.getLocalAddress().getHostAddress();

	    m.setTime();
	    m.setMachine(machine);

	    ObjectOutputStream out = new ObjectOutputStream(
		    socket.getOutputStream());

	    out.writeObject(m);
	    out.close();
	    socket.close();
	} catch (IOException ex) {
	    SemantiCore.notification.print("[ E ] SocketServiceRequester : "
		    + ex.getMessage());
	}
    }
}
