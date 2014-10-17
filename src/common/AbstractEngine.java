package common;

import java.util.concurrent.ConcurrentLinkedQueue;

import messaging.Message;
import messaging.MessageListener;
import messaging.Publisher;

public abstract class AbstractEngine implements MessageListener, IEngine {

	protected final ConcurrentLinkedQueue<Message> msgQueue;
	protected final boolean isThreaded;
	
	public AbstractEngine(final boolean isThreaded) {
		this.isThreaded = isThreaded;
		msgQueue = new ConcurrentLinkedQueue<Message>();
	}

	public AbstractEngine() {
		this(false);
	}

	public synchronized void onMessage(Message msg) {

		// If threaded, enque message to be processed later
		if (this.isThreaded)
			msgQueue.add(msg);
		else
			msg.process(this);
	}

	// TODO guard against starvation
	public void performAction() {

		Message msg;
		if ((msg = msgQueue.poll()) != null)
			msg.process(this);
	}

	// I'd like to remove this...
	public void run() {

		while (!Thread.currentThread().isInterrupted()) {
			// Just loop
			this.performAction();
			// Thread.yield was here, but it is dangerous to use
		}
	}

	public void processQueue() {

		ConcurrentLinkedQueue<Message> curr = new ConcurrentLinkedQueue<Message>(msgQueue);
		while (!curr.isEmpty()) {
			this.performAction();
		}
	}

	// This method dispatches a message to the appropriate processor
	public synchronized <T extends Message> void dispatchMessage(T msg) {
		Publisher.getInstance().send(msg);
	}

	public void pause(Object lock) throws InterruptedException {
		synchronized (lock) {
			lock.wait();
		}
	}

	public void stop() {
		Thread.currentThread().interrupt();
	}
	
	@Override
	public void close() {
		// do nothing
	}
}
