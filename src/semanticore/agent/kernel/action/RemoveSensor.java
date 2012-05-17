package semanticore.agent.kernel.action;

import com.hp.hpl.jena.vocabulary.RDF;

import semanticore.agent.kernel.action.model.SensorialEvent;
import semanticore.agent.kernel.information.SimpleFact;
import semanticore.agent.sensorial.SensorialComponent;
import semanticore.domain.model.ComponentMessage;
import semanticore.domain.model.SCOnt;

public class RemoveSensor extends SensorialEvent<String, SensorialComponent> {
    public RemoveSensor(String sensorName) {
	super(SCOnt.REMOVE_SENSOR, sensorName);
    }

    @Override
    public Object evaluate(SensorialComponent param) {
	if (param.removeSensor(getParameter()))
	    return new ComponentMessage("sensorial", "decision",
		    new SimpleFact(SensorialEvent.NAMESPACE + getParameter(),
			    RDF.type.toString(), SensorialEvent.SENSOR_REMOVED));
	else
	    return null;
    }
}
