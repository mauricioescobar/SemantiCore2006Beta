package semanticore.domain.services.platform;

import java.lang.reflect.Constructor;

import semanticore.domain.SemantiCore;
import semanticore.domain.model.*;

public class ServiceFactory {
    ServiceRequester sReq;

    ServiceListener sList;

    Service service;

    public ServiceFactory(Service s) {
	super();

	service = s;

	Class[] args = new Class[1];

	// Lê arquivo de configuração

	try {
	    Class clName = Class.forName(getServiceRequesterClass());
	    args[0] = Class.forName("semanticore.domain.model.Service");
	    Constructor cons = clName.getDeclaredConstructor(args);
	    Object[] arg = new Object[1];
	    arg[0] = s;
	    Object o = cons.newInstance(arg);
	    sReq = (ServiceRequester) o;

	    clName = Class.forName(getServiceListenerClass());
	    cons = clName.getDeclaredConstructor(args);
	    o = cons.newInstance(arg);
	    sList = (ServiceListener) o;
	} catch (Exception e) {
	    SemantiCore.notification.print(">>>> Service Factory : Error : "
		    + e.getMessage());
	    e.printStackTrace();
	}

    }

    private String getServiceRequesterClass() {
	return "semanticore.domain.services.platform.SocketServiceRequester";
    }

    private String getServiceListenerClass() {
	return "semanticore.domain.services.platform.SocketServiceListener";
    }

    public ServiceRequester getServiceRequester() {
	return sReq;
    }

    public ServiceListener getServiceListener() {
	return sList;
    }

}
