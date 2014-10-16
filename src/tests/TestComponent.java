package tests;

import common.AbstractEngine;

import tests.util.ConcreteMsg;
import tests.util.ConcreteMsg2;
import messaging.Message;
import messaging.Publisher;

public class TestComponent extends AbstractEngine {
	
	Publisher publisher = Publisher.getInstance();

	public void start() throws IllegalAccessException {
		
		publisher.subscribe(ConcreteMsg.class, this);
		publisher.subscribe(ConcreteMsg2.class, this);

		ConcreteMsg msg = new ConcreteMsg();
		publisher.send(msg);
		ConcreteMsg2 msg2 = new ConcreteMsg2();
		publisher.send(msg2);
		publisher.send(msg);

		this.processQueue();
	}

	@Override
	public void generate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends Message> void dispatchMessage(T msg) {
		msg.process(this);
	}

	@Override
	public void configure(int gs, int timeStep) {
		return;
	}

	@Override
	public void close() {
		return;
	}
}
