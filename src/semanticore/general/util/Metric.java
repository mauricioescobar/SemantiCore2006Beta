package semanticore.general.util;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;

import semanticore.domain.SemantiCore;

public class Metric implements Serializable {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1;

    public String id;

    public long start = 0;

    public long stop = 0;

    public double resultInSeconds = 0;

    public double resultMinutos = 0;

    public int nivel = 0;

    LinkedList<Metric> objects = new LinkedList<Metric>();

    public Metric(String id) {
	this.id = id;
    }

    public void addExecObject(Metric ob) {
	objects.add(ob);
    }

    public void start() {
	start = new GregorianCalendar().getTimeInMillis();
    }

    public void stop() {
	stop = new GregorianCalendar().getTimeInMillis();
	long r = (stop - start);
	resultInSeconds = ((double) r) / 1000;
	resultMinutos = resultInSeconds / 60;
    }

    public Metric crateExecObject(String label) {
	Metric o = new Metric(label);
	o.nivel = nivel + 1;
	addExecObject(o);
	return o;
    }

    public static void showResult(Metric m) {
	SemantiCore.notification.print(getMetric(m));

	Iterator<Metric> iter = m.objects.iterator();
	while (iter.hasNext())
	    showResult(iter.next());
    }

    public static String getResult(Metric m) {
	String content = "";
	content += getMetric(m);

	Iterator<Metric> iter = m.objects.iterator();
	while (iter.hasNext())
	    content += getResult(iter.next());

	return content;
    }

    private static String getMetric(Metric m) {

	String content = "";

	if (m.nivel == 0)
	    content += m.id + "\t" + m.resultInSeconds + "\t";
	else
	    content += m.resultInSeconds + "\t";

	return content;
    }
}
