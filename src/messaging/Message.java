package messaging;

// This class defines the baseclass for all messages.
// Each message type should extend this, adding class properties as desired
// to communicate necessary data.
public abstract class Message {

	// Can't find a way to make this work just yet. For now receivers will have
	// to make "instanceof" checks...
	
	// // method to return a message cast to its actual type.
	// // This is useful when dispatching messages to processing functions
	// @SuppressWarnings("unchecked")
	// public <T extends Message> T getOriginalType(Message msg) {
	// return msg.getClass();
	// }
}