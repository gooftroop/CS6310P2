package messaging.events;

import messaging.Message;
import messaging.MessageListener;

public class CloseMessage implements Message {

	@Override
	public void process(MessageListener l) {
		l.close();
	}
}
