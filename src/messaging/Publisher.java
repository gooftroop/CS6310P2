package messaging;

import java.util.HashMap;
import java.util.LinkedList;

// This class is a singleton responsible for handling message distribution

public class Publisher {
	
	private static Publisher instance = null;
	private static HashMap<Class<?>, LinkedList<MessageListener>> subscribers;

	// Used to ensure no subscribe attempts are attempted after broadcasting has
	// begun. This is in place for thread safety concerns and could be relaxed
	// with some tweaks to the implementation.
	private Boolean messageBroadcasted = false;

	private Publisher() {
		
		// singleton. Use getInstance to access.
		subscribers = new HashMap<Class<?>, LinkedList<MessageListener>>();
	}

	public static Publisher getInstance() {
		if (instance == null) 
			instance = new Publisher();
		
		return instance;
	}

	public synchronized void subscribe(Class<?> cls, MessageListener listener)
			throws IllegalAccessException {
		// Don't allow subscription after broadcasting has begun.
		// This is in place to ensure no thread safety issues, but could be
		// relaxed in the future.
		if (messageBroadcasted) 
			throw new IllegalAccessException("No subscriptions allowed after broadcast has begun!");

		LinkedList<MessageListener> subscriberList = subscribers.get(cls);
		
		if (subscriberList == null) 
			subscriberList = new LinkedList<MessageListener>();
		
		subscriberList.add(listener);
		subscribers.put(cls, subscriberList);
	}

	public void send(Message msg) {
		
		messageBroadcasted = true;
		
		// Send message to all subscribers
		LinkedList<MessageListener> allListeners = subscribers.get(msg.getClass());
		if (allListeners != null) {
			for (MessageListener listener : allListeners) {
				listener.onMessage(msg);
			}
		}
	}
}
