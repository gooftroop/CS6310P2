package messaging.events;

import messaging.MessageListener;

public class ConsumeMessage extends AbstractMessage {

	@Override
	public void process(MessageListener l) {
		l.generate();
	}
}