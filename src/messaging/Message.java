package messaging;

import common.IEngine;

// This class defines the baseclass for all messages.
// Each message type should extend this, adding class properties as desired
// to communicate necessary data.
public interface Message {

	public void process(IEngine l); 
}