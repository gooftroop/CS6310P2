package messaging;

import messaging.events.AbstractMessage;
import messaging.events.ConsumeMessage;
import messaging.events.ProduceMessage;

public class ContinuouslyProduceCommand extends AbstractMessage {

	@Override
	public void process(MessageListener l) {
		Publisher.getInstance().send(new ConsumeMessage());
		Publisher.getInstance().send(new ProduceMessage());
	}
}