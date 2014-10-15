package common;

import java.util.concurrent.ConcurrentLinkedQueue;

import messaging.Message;
import messaging.MessageListener;
import messaging.Publisher;
import messaging.events.StopMessage;

public abstract class ComponentBase implements MessageListener, IComponent {

	protected final ConcurrentLinkedQueue<Message> msgQueue;

	public ComponentBase() {
		msgQueue = new ConcurrentLinkedQueue<Message>();
	}
	
	public void onMessage(Message msg) {

		// enque message to be processed later
		if (msg instanceof StopMessage) { 
			msgQueue.clear();
			Thread.currentThread().interrupt();
		} else 
			msgQueue.add(msg);
	}

	// TODO guard against starvation
	public void performAction() {

		Message msg;
		if ((msg = msgQueue.poll()) != null) 
			msg.process(this);
	}
	
	public void run() {

		while (!Thread.currentThread().isInterrupted()) {
			// Just loop
			// Thread.yield was here, but it is dangerous to use
		}
	}

	// This method dispatches a message to the appropriate processor
	public <T extends Message> void dispatchMessage(T msg) {
		Publisher.getInstance().send(msg);
	}
}
