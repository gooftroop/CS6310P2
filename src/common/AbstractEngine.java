package common;

import java.util.concurrent.ConcurrentLinkedQueue;

import messaging.Message;
import messaging.MessageListener;
import messaging.Publisher;
import messaging.events.StopMessage;

public abstract class AbstractEngine implements MessageListener, IEngine {

	protected final ConcurrentLinkedQueue<Message> msgQueue;

	public AbstractEngine() {
		msgQueue = new ConcurrentLinkedQueue<Message>();
	}
	
	public synchronized void onMessage(Message msg) {

		// enque message to be processed later
		if (msg instanceof StopMessage) { 
			msgQueue.clear();
			Thread.currentThread().interrupt();
		} else 
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
}
