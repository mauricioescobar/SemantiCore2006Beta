package semanticore.domain.model;

import semanticore.general.util.Metric;
import semanticore.general.util.SCThread;

public abstract class Engine<COMPONENT> extends SCThread {
    protected COMPONENT component;

    public Metric metric;

    public void setComponent(COMPONENT c) {
	this.component = c;
    }

    protected SemanticAgent getOwner() {
	try {
	    return ((Component) component).getOwner();
	} catch (Exception e) {
	    return null;
	}
    }

    public void setMetric(Metric m) {
	this.metric = m;
    }

    public void startMetric() {
	if (metric != null)
	    metric.start();
    }

    public void stopMetric() {
	if (metric != null)
	    metric.stop();
    }
}
