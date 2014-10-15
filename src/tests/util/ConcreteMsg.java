package tests.util;

import messaging.Message;
import messaging.MessageListener;

public class ConcreteMsg implements Message {
	public int someDataHere;

	@Override
	public void process(MessageListener l) {
		System.out.printf("processor1 called! (%s)\n", this.getClass().getName());
	}
}
