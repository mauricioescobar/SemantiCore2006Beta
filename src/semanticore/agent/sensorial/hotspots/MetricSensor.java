package semanticore.agent.sensorial.hotspots;

import semanticore.agent.sensorial.Sensor;
import semanticore.domain.model.SemanticMessage;
import semanticore.general.util.Metric;

public class MetricSensor extends Sensor {

    public MetricSensor() {
	super("MetricSensor");
    }

    @Override
    public Object evaluate(Object facts) {

	if (facts instanceof SemanticMessage) {
	    SemanticMessage message = (SemanticMessage) facts;

	    if (message.getContent() instanceof String)
		if (message.getContent().toString().equals("metric")) {
		    Metric.showResult(getOwner().metric);
		    return new Integer(1);
		}
	}

	return null;
    }

}
