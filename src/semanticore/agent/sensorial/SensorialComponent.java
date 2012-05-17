package semanticore.agent.sensorial;

import java.util.Vector;

import semanticore.agent.kernel.action.model.SensorialEvent;
import semanticore.domain.SemantiCore;
import semanticore.domain.model.Component;
import semanticore.domain.model.SemanticAgent;

public class SensorialComponent extends Component {
    private Vector sensors = new Vector();

    public void put(Object information) {
	this.msgBuffer.put(information);
    }

    public SensorialComponent(SemanticAgent owner) {
	super(owner, "sensorial");

	interpreter = new SensorialInterpreter(this);
    }

    public void run() {
	Object o;

	while (running) {
	    try {
		o = msgBuffer.getFirst();

		Object result = null;

		if (o instanceof SensorialEvent)
		    result = interpreter.evaluate((SensorialEvent) o);
		else {
		    result = testSensors(o);
		}

		if (result != null)
		    transmit(result);
	    } catch (Exception e) {
		SemantiCore.notification.print(">>>> [ " + owner.getName()
			+ " ]  SensorialComponent Error : " + e.getMessage());
	    }
	}
    }

    private Object testSensors(Object o) {
	Sensor sensor;
	Object result = null;

	for (int i = 0; i < sensors.size(); i++) {
	    sensor = (Sensor) sensors.get(i);

	    if ((result = sensor.evaluate(o)) != null) {
		return result;
	    }
	}

	return null;
    }

    public boolean addSensor(Sensor sensor) {
	Sensor s;

	boolean error = false;

	for (int i = 0; i < sensors.size(); i++) {
	    s = (Sensor) sensors.get(i);

	    if (s.getName().equals(sensor.getName())) {
		SemantiCore.notification
			.print("ERROR | Sensorial Component: A sensor with this name already exists.");
		error = true;
		break;
	    }
	}

	if (!error) {
	    sensor.sensorialComponent = this;
	    return sensors.add(sensor);
	}

	return !error;
    }

    public boolean removeSensor(String sensorName) {
	Sensor s;

	for (int i = 0; i < sensors.size(); i++) {
	    s = (Sensor) sensors.get(i);

	    if (s.getName().equals(sensorName)) {
		sensors.remove(s);
		return true;
	    }
	}

	return false;
    }

    public void addTransmissionComponent(String component) {
	this.transmissionComponent.add(component);
    }
}
