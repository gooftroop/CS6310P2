package tests;

import tests.util.ConcreteMsg;
import tests.util.ConcreteMsg2;
import messaging.Message;
import messaging.Publisher;
import util.ComponentBase;

public class TestComponent extends ComponentBase {
	Publisher publisher = Publisher.getInstance();

	public void start() throws IllegalAccessException {
		publisher.subscribe(ConcreteMsg.class, this);
		publisher.subscribe(ConcreteMsg2.class, this);

		ConcreteMsg msg = new ConcreteMsg();
		publisher.send(msg);
		ConcreteMsg2 msg2 = new ConcreteMsg2();
		publisher.send(msg2);
		publisher.send(msg);

		processFullMessageQueue();
	}

	// This method dispatches a message to the appropriate processor
	public void dispatchMessage(Message msg) {
		if (msg instanceof ConcreteMsg) {
			process((ConcreteMsg) msg);
		} else if (msg instanceof ConcreteMsg2) {
			process((ConcreteMsg2) msg);
		} else {
			System.err
					.printf("WARNING: No processor specified in class %s for message %s\n",
							this.getClass().getName(), msg.getClass().getName());
		}
	}

	public void process(ConcreteMsg msg) {
		System.out
				.printf("processor1 called! (%s)\n", msg.getClass().getName());
	}

	public void process(ConcreteMsg2 msg) {
		System.out
				.printf("processor2 called! (%s)\n", msg.getClass().getName());
	}

}
