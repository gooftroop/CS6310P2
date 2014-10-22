package tests;

import java.util.concurrent.ArrayBlockingQueue;

import common.ComponentBase;

import messaging.Message;
import messaging.Publisher;
import messaging.events.NeedDisplayDataMessage;
import messaging.events.ProduceContinuousMessage;
import messaging.events.ProduceMessage;

public class DummyModel extends ComponentBase {
	private Publisher pub = Publisher.getInstance();
	ArrayBlockingQueue<Integer> q;
	
	public DummyModel(ArrayBlockingQueue<Integer> q) {
		this.q = q;
	}
	
	@Override
	public void dispatchMessage(Message msg) {
		if (msg instanceof ProduceContinuousMessage) {
			process((ProduceContinuousMessage) msg);
		} else if (msg instanceof ProduceMessage) {
			process((ProduceMessage) msg);
		} else if (msg instanceof NeedDisplayDataMessage) {
			process((NeedDisplayDataMessage) msg);
		} else {
			System.err.printf("WARNING: No processor specified in class %s for message %s\n",
					this.getClass().getName(), msg.getClass().getName());
		}
	}

	private void process(ProduceContinuousMessage msg) {
		generateData();
		pub.send(msg); // resend message to self (since continuous)
	}

	private void process(ProduceMessage msg) {
		generateData();
	}

	private void process(NeedDisplayDataMessage msg) {
		generateData();
	}

	public void close() {
		// destructor when done with class
	}
	
	private void generateData() {
		try {
			q.put(1);
			System.out.printf("added generated data to buffer\n");
		} catch (InterruptedException e) {
			stopThread = true;
		}
	}
}
