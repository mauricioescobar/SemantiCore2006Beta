package semanticore.general.util;

import java.io.Serializable;
import java.lang.Thread.State;

public abstract class SCThread implements Runnable, Serializable {
    protected State state;

    private String name;

    public SCThread() {
	state = State.NEW;
    }

    public final void start() {
	if (state == State.TERMINATED) {
	    this.state = State.RUNNABLE;
	    new Thread(this).start();
	} else {
	    state = State.RUNNABLE;
	    new Thread(this).start();
	}
    }

    public void run() {
	exec();

	this.state = State.TERMINATED;

	terminate(name);
    }

    public abstract void terminate(Object param);

    public abstract void exec();

    public State getState() {
	return state;
    }

    public void setName(String name) {
	try {
	    this.name = name;
	} catch (Exception e) {

	}
    }

    public String getName() {
	return name;
    }

    public void suspend() {
	if (state == State.RUNNABLE)
	    this.state = State.BLOCKED;
    }

    public boolean resume() {
	if (state == State.BLOCKED) {
	    this.state = State.RUNNABLE;
	    return true;
	} else
	    return false;

    }

    public void stop() {
	this.state = State.TERMINATED;
    }
}
