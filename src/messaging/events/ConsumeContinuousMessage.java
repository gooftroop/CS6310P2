package messaging.events;

import messaging.Message;
import messaging.MessageListener;
import messaging.Publisher;

public class ConsumeContinuousMessage implements Message {

	@Override
	public void process(MessageListener l) {
		l.generate();
		Publisher.getInstance().send(this);
	}

}