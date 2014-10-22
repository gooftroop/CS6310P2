package messaging.events;

import messaging.MessageListener;

public class ProduceMessage extends AbstractMessage {

	@Override
	public void process(MessageListener l) {
		l.generate();
	}
}