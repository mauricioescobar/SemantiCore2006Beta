package semanticore.domain.control;

import java.util.Vector;

import semanticore.domain.Environment;
import semanticore.domain.SemantiCore;
import semanticore.domain.model.SemanticMessage;
import semanticore.domain.model.Service;
import semanticore.domain.services.platform.ServiceFactory;

public class DataBridge extends Service {
    protected Environment environment;

    public DataBridge(Environment env, String address, String port) {
	super(address, port);

	this.environment = env;

	factoriesSetup();

	listener.start();
    }

    private void factoriesSetup() {
	ServiceFactory sFact = new ServiceFactory(this);

	super.setServiceRequester(sFact.getServiceRequester());
	super.setServiceListener(sFact.getServiceListener());
    }

    public void run() {
	Vector messages;
	SemanticMessage sm;

	while (true) {
	    messages = listener.getMessages();

	    for (int i = 0; i < messages.size(); i++) {
		sm = (SemanticMessage) messages.get(i);

		(new DataBridgeServiceThread(sm, this)).start();
	    }

	    try {
		Thread.sleep(80);
	    } catch (InterruptedException ex) {
		SemantiCore.notification
			.print(">>>> DataBridge | Error : (run) : "
				+ ex.getMessage());
	    }
	}
    }
}
