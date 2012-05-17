package semanticore.domain.model;

public abstract class Event<T, C> {
    protected String descriptor;

    protected T parameter;

    protected int id = -1;

    private static int uniqueId = 0;

    public Event(String descriptor, T parameter) {
	this.descriptor = descriptor;
	this.parameter = parameter;
	this.id = uniqueId++;
    }

    public synchronized String getDescriptor() {
	return descriptor;
    }

    public synchronized T getParameter() {
	return parameter;
    }

    public abstract Object evaluate(C param);
}
