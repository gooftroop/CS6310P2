package simulation;

import java.util.concurrent.ArrayBlockingQueue;

import common.ComponentBase;
import common.IGrid;
import messaging.Message;
import messaging.Publisher;
import messaging.events.NeedDisplayDataMessage;
import messaging.events.ProduceContinuousMessage;
import messaging.events.ProduceMessage;

public class Model extends ComponentBase {
	private Publisher pub = Publisher.getInstance();
	Earth model;
	
	public Model(ArrayBlockingQueue<IGrid> q, int gs, int timeStep) {
		model = new Earth(q);
		model.configure(gs, timeStep);
		model.start();
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
			model.generate();
//			System.out.printf("added generated data to buffer\n");
		} catch (InterruptedException e) {
			stopThread = true;
		}
	}
}
