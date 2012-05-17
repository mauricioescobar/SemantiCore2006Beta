package semanticore.general.util;

import java.io.Serializable;
import java.util.LinkedList;

import semanticore.domain.SemantiCore;

public class SimpleQueue<E> implements Serializable {
    private LinkedList queue = new LinkedList();

    public synchronized boolean enqueue(E o) {
	try {
	    this.queue.addLast(o);
	    return true;
	} catch (Exception e) {
	    SemantiCore.notification.print(">>>> SimpleQueue 'enqueue' error");
	    return false;
	}
    }

    public synchronized E dequeue() {
	try {
	    if (!this.queue.isEmpty())
		return (E) this.queue.removeFirst();
	    else
		return null;
	} catch (Exception e) {
	    return null;
	}
    }

    public synchronized boolean isEmpty() {
	try {
	    return this.queue.isEmpty();
	} catch (Exception e) {
	    return false;
	}
    }

    public synchronized boolean contains(E e) {
	return queue.contains(e);
    }
}
