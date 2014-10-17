package messaging.events;

import messaging.Message;
import messaging.MessageListener;

public class StartMessage implements Message {

	@Override
	public void process(MessageListener l) {
		// Nothing to do - this should be a signal message
	}
}