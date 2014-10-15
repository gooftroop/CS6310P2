package simulation;

import messaging.Message;
import common.AbstractEngine;

public abstract class EarthEngine extends AbstractEngine {
	
	@Override
	public <T extends Message> void dispatchMessage(T msg) {
		
	}
	
	@Override
	public void close() {
		
	}
}
