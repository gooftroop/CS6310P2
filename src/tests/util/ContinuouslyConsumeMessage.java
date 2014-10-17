package tests.util;

import messaging.Message;
import messaging.MessageListener;
import messaging.Publisher;
import messaging.events.ProduceMessage;

public class ContinuouslyConsumeMessage implements Message {

	@Override
	public void process(MessageListener l) {
		l.generate();
		Publisher.getInstance().send(new ProduceMessage());
		Publisher.getInstance().send(this);
	}

}