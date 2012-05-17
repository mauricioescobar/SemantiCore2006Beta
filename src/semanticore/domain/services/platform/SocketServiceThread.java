package semanticore.domain.services.platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Vector;

import semanticore.domain.SemantiCore;

public class SocketServiceThread extends Thread {
    Socket socket;

    Vector mensagens;

    public SocketServiceThread(Socket s, Vector mens) {
	socket = s;
	mensagens = mens;
    }

    public void run() {
	try {
	    ObjectInputStream in = new ObjectInputStream(
		    socket.getInputStream());

	    Object o = in.readObject();

	    if (o != null)
		mensagens.add(o);

	    in.close();
	    socket.close();
	} catch (IOException ex) {
	    SemantiCore.notification.print("IO - ERROR:" + ex.getMessage());
	    ex.printStackTrace();
	} catch (ClassNotFoundException ex) {
	    SemantiCore.notification.print(">>>> SocketServiceThread - ERRO:"
		    + ex.getMessage());

	    ex.printStackTrace();
	}
    }
}
