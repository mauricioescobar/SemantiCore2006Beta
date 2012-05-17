package semanticore.general.util;

import java.util.GregorianCalendar;
import java.util.LinkedList;

import semanticore.domain.model.SemanticAgent;

public class MetricFrame {

    private SemanticAgent agent;

    public long start = 0;

    public long stop = 0;

    public ExecObject execution;

    class ExecObject {
	String label;

	long startTime;

	long stopTime;

	long result;

	LinkedList<ExecObject> objects = new LinkedList<ExecObject>();

	public ExecObject(String label, long start, long stop) {
	    this.label = label;
	    this.startTime = start;
	    this.stopTime = stop;
	}

	public void addExecObject(ExecObject ob) {
	    objects.add(ob);
	}
    }

    public MetricFrame(SemanticAgent agent, String id) {
	this.agent = agent;

	execution = new ExecObject(id, 0, 0);

    }

    public void start() {
	start = new GregorianCalendar().getTimeInMillis();
    }

    public void stop(String id) {
	stop = new GregorianCalendar().getTimeInMillis();

	long result = (stop - start);
	execution.objects.add(new ExecObject(id, start, stop));
    }
}
