package semanticore.domain.services.platform;

import java.util.*;
import semanticore.domain.model.*;

public abstract class ServiceListener extends Thread {
    protected Service service;
    protected Vector messageBuffer = new Vector();

    public ServiceListener(Service s) {
	super();
	service = s;
    }

    public void run() {

    }

    public void addMessage(SemanticMessage sm) {
	messageBuffer.add(sm);
    }

    public Vector getMessages() {
	Vector returnVec = (Vector) messageBuffer.clone();
	messageBuffer.clear();

	return returnVec;
    }

}
