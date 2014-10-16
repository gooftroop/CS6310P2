package common;

import java.util.concurrent.ConcurrentLinkedQueue;

import messaging.Message;
import messaging.MessageListener;
import messaging.Publisher;

public abstract class AbstractEngine implements MessageListener, IEngine {

	protected final ConcurrentLinkedQueue<Message> msgQueue;

	public AbstractEngine() {
		msgQueue = new ConcurrentLinkedQueue<Message>();
	}
	
	public synchronized void onMessage(Message msg) {

		// Enque message to be processed later
		msgQueue.add(msg);
	}

	// TODO guard against starvation
	public synchronized void performAction() {

		Message msg;
		if ((msg = msgQueue.poll()) != null) 
			msg.process(this);
	}
	
	public void run() {

		while (!Thread.currentThread().isInterrupted()) {
			// Just loop
			this.performAction();
			// Thread.yield was here, but it is dangerous to use
		}
	}
	
	public synchronized void processQueue() {
		
		while(!msgQueue.isEmpty()) {
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
}
