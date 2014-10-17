package messaging.events;

import messaging.Message;
import messaging.MessageListener;

public class UpdatedMessage implements Message {
	
	private final MessageListener src;
	
	public UpdatedMessage(MessageListener src) {
		this.src = src;
	}
	
	public MessageListener getSource() {
		return this.src;
	}

	@Override
	public void process(MessageListener l) {
		// do nothing
		return;
	}
}
