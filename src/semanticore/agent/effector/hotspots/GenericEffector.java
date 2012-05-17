package semanticore.agent.effector.hotspots;

import semanticore.agent.effector.EffectorComponent;
import semanticore.agent.effector.Effector;
import semanticore.domain.SemantiCore;
import semanticore.domain.model.SemanticMessage;

public class GenericEffector extends Effector {
    public GenericEffector(EffectorComponent owner) {
	super("GenericEffector", owner);
    }

    public void publish(Object assertion) {
	if (assertion instanceof SemanticMessage)
	    publish((SemanticMessage) assertion);
	else
	    publish(new Object[] { "*", assertion });
    }

    public void publish(Object[] data) {
	try {
	    SemantiCore.notification.print("\n[ GenericEffector | "
		    + effectorComponent.getOwner().getName()
		    + " ] : publishing <" + data[1] + "> \n");

	    SemanticMessage message = new SemanticMessage("",
		    this.effectorComponent.getOwner().getName(),
		    (String) data[0], data[1]);
	    publish(message);
	} catch (Exception e) {
	    SemantiCore.notification
		    .print("[ E ] GenericEffector : error sending the message");
	}
    }

    @Override
    public boolean publish(SemanticMessage message) {
	return super.publish(message);
    }

    public void publish(String to, String content) {
	publish(new Object[] { to, content });
    }
}
