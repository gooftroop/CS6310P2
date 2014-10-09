package util;

import java.util.concurrent.ConcurrentLinkedQueue;
import messaging.Message;
import messaging.MessageListener;

public abstract class ComponentBase implements MessageListener, Runnable {
	ConcurrentLinkedQueue<Message> msgQueue = new ConcurrentLinkedQueue<Message>();

	public void onMessage(Message msg) {
		System.out.printf("onMessage (%s)\n", msg.getClass().getName());
		// enque message to be processed later
		msgQueue.add(msg);
	}

	public void processFullMessageQueue() {
		while (!processMessageQueue()) {
		}
	}

	public Boolean processMessageQueue() {
		Boolean queueEmpty = false;
		Message msg = msgQueue.poll();
		if (msg == null) {
			queueEmpty = true;
		} else {
			dispatchMessage(msg);
		}
		return queueEmpty;
	}

	// This method dispatches a message to the appropriate processor
	public abstract void dispatchMessage(Message msg);

	public void run() {
		Boolean queueEmpty;
		while (Thread.currentThread().isInterrupted()) {
			queueEmpty = processMessageQueue();
			if (queueEmpty) {
				// Sleep execution thread if nothing to process (save cpu)
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
