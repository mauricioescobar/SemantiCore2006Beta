package semanticore.agent.kernel.action;

import com.hp.hpl.jena.vocabulary.RDF;

import semanticore.agent.kernel.action.model.SensorialEvent;
import semanticore.agent.kernel.information.SimpleFact;
import semanticore.agent.sensorial.Sensor;
import semanticore.agent.sensorial.SensorialComponent;
import semanticore.domain.model.ComponentMessage;
import semanticore.domain.model.SCOnt;

public class AddSensor extends SensorialEvent<Sensor, SensorialComponent> {
    public AddSensor(Sensor sensor) {
	super(SCOnt.ADD_SENSOR, sensor);
    }

    @Override
    public Object evaluate(SensorialComponent param) {
	param.addSensor(getParameter());

	//
	String sensorName = getParameter().getName();

	return new ComponentMessage("sensorial", "decision", new SimpleFact(
		"http://semanticore.pucrs.br#" + sensorName,
		RDF.type.toString(), SensorialEvent.SENSOR_ADDED));
    }
}
