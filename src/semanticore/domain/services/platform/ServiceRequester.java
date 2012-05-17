package semanticore.domain.services.platform;

import semanticore.domain.model.*;

public abstract class ServiceRequester {

    public ServiceRequester(Service s) {
	super();

    }

    public abstract void request(Message sm, String address, String port);

}
