package tests.util;

import messaging.Message;
import messaging.MessageListener;
import messaging.Publisher;
import messaging.events.ConsumeMessage;

public class ContinuouslyProduceMessage implements Message {

	@Override
	public void process(MessageListener l) {
		l.generate();
		Publisher.getInstance().send(new ConsumeMessage());
		Publisher.getInstance().send(this);
	}
}