package semanticore.general.util;

import java.util.*;
import java.io.*;

import semanticore.domain.SemantiCore;

public class SynchronizedQueue<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<T> list;

    private class RootSync implements Serializable {

	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;
    }

    private RootSync root;

    public SynchronizedQueue() {
	list = Collections.synchronizedList(new LinkedList<T>()); // Synchronized

	root = new RootSync();
    }

    public void put(T o) {
	list.add(o);

	synchronized (root) {
	    root.notifyAll();
	}
    }

    public T getFirst() {
	if (list.isEmpty()) {
	    try {
		synchronized (root) {
		    root.wait();
		}
	    } catch (InterruptedException ie) {
		SemantiCore.notification
			.print(">>> SynchonizedQueue : Error : "
				+ ie.getMessage());
	    } catch (Exception e) {
		SemantiCore.notification
			.print(">>>> SynchonizedQueue : Error : "
				+ e.getMessage());
	    }
	}

	T item = null;

	try {
	    item = list.remove(0);
	} catch (IndexOutOfBoundsException e) {
	    e.printStackTrace();
	}

	return item;
    }

    public synchronized boolean isEmpty() {
	return list.isEmpty();
    }
}
