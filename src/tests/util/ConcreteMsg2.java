package tests.util;

import messaging.Message;
import messaging.MessageListener;

public class ConcreteMsg2 implements Message {
	public int someDataHere;

	@Override
	public void process(MessageListener l) {
		System.out.printf("processor2 called! (%s)\n", this.getClass().getName());
		
	}
}
