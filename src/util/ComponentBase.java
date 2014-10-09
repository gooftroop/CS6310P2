package util;

import java.util.concurrent.ConcurrentLinkedQueue;
import messaging.Message;
import messaging.MessageListener;

public abstract class ComponentBase implements MessageListener, Runnable {
	ConcurrentLinkedQueue<Message> msgQueue = new ConcurrentLinkedQueue<Message>();

	public void onMessage(Message msg) {
//		System.out.printf("%s.onMessage (%s)\n", this.getClass().getName(), msg.getClass().getName());
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
//		System.out.printf("starting run of %s\n", this.getClass().getName());
		Boolean queueEmpty;
		while (!Thread.currentThread().isInterrupted()) {
			runAutomaticActions();
			queueEmpty = processMessageQueue();
			if (queueEmpty) {
				// yield execution thread if nothing to process (save cpu)
				Thread.yield();
			}
		}

	}

	public void runAutomaticActions() {
		// override this method for actions to be ran automatically
	}
}
