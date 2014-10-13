package simulation;

import messaging.Message;
import messaging.events.ConsumeMessage;
import messaging.events.ProduceContinuousMessage;
import messaging.events.ProduceMessage;
import common.ComponentBase;

public abstract class EarthEngine extends ComponentBase {
	
	private void process(ProduceMessage msg) {
		
	}
	
	private void process(ConsumeMessage msg) {
		
	}
	
	private void process(ProduceContinuousMessage msg) {
		
	}
	
	protected abstract void generate();
	
	@Override
	public <T extends Message> void dispatchMessage(T msg) {
		
		
	}
}
