package common;

import java.util.concurrent.ConcurrentLinkedQueue;

import messaging.Message;
import messaging.Publisher;
import messaging.events.UpdatedMessage;

public abstract class AbstractEngine implements IEngine {

	protected final ConcurrentLinkedQueue<Message> msgQueue;
	protected final boolean isThreaded;
	
	protected boolean stopped = false;
	
	public AbstractEngine(final boolean isThreaded) {
		this.isThreaded = isThreaded;
		msgQueue = new ConcurrentLinkedQueue<Message>();
	}

	public AbstractEngine() {
		this(false);
	}

	public void onMessage(Message msg) {

		// If threaded, enque message to be processed later
		if (this.isThreaded)
			msgQueue.add(msg);
		else
			msg.process(this);
	}

	// TODO guard against starvation?
	public void performAction() {

		Message msg;
		while ((msg = msgQueue.poll()) != null) {
			msg.process(this);
		}
	}

	public void run() {

		while (!Thread.currentThread().isInterrupted() && !this.stopped) {
			// Just loop
			Publisher.getInstance().send(new UpdatedMessage());
			this.performAction();
		}
	}

	public void processQueue() {

		ConcurrentLinkedQueue<Message> curr = new ConcurrentLinkedQueue<Message>(msgQueue);
		while (!curr.isEmpty()) {
			this.performAction();
		}
	}

	// This method dispatches a message to the appropriate processor
	public <T extends Message> void dispatchMessage(T msg) {
		Publisher.getInstance().send(msg);
	}
	
	@Override
	public void generate() {
		return;
	}

	public void stop() {
		Thread.currentThread().interrupt();
		this.stopped = true;
	}
	
	@Override
	public void close() {
		return;
	}
	
	public void pause(Object lock) throws InterruptedException {
		synchronized (lock) {
			lock.wait();
		}
	}

	@Override
	public void start() {
		return;
	}
}
