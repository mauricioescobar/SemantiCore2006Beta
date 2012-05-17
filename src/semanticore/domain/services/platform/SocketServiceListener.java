package semanticore.domain.services.platform;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import semanticore.domain.model.Service;

public class SocketServiceListener extends ServiceListener {
    ServerSocket serverSocket = null;

    public SocketServiceListener(Service s) {
	super(s);

	int portnumber = Integer.parseInt(s.getPort());
	int counter = 0;

	while ((counter < 5000) && (serverSocket == null)) {
	    try {
		serverSocket = new ServerSocket(portnumber);
		s.setPort(portnumber);
	    } catch (IOException ex) {
		counter++;
	    }
	}
    }

    public Vector getMessages() {
	Vector result = (Vector) messageBuffer.clone();
	messageBuffer.clear();
	return result;
    }

    public void run() {
	// SemanticMessage msg;

	while (true) {
	    try {
		Socket socket = serverSocket.accept();

		(new SocketServiceThread(socket, messageBuffer)).start();
	    } catch (IOException ex) {
		System.err.println("Listener ERRO:" + ex.getMessage());
	    }
	}
    }

    public static void main(String args[]) {
	// SocketServiceListener list = new SocketServiceListener();
	// list.start();
    }

}
