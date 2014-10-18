package messaging;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import common.IEngine;

// This class is a singleton responsible for handling message distribution

public class Publisher {

	private static Publisher instance = null;
	private static ConcurrentHashMap<Class<?>, ConcurrentLinkedQueue<IEngine>> subscribers;

	private Publisher() {

		// singleton. Use getInstance to access.
		subscribers = new ConcurrentHashMap<Class<?>, ConcurrentLinkedQueue<IEngine>>();
	}

	public static Publisher getInstance() {
		if (instance == null) {
			instance = new Publisher();
		}
		return instance;
	}

	public synchronized void subscribe(Class<?> cls, IEngine listener) {

		ConcurrentLinkedQueue<IEngine> subscriberList = subscribers.get(cls);

		if (subscriberList == null) {
			subscriberList = new ConcurrentLinkedQueue<IEngine>();
		}

		subscriberList.add(listener);
		subscribers.put(cls, subscriberList);
	}

	public static synchronized void unsubscribeAll() {
		subscribers.clear();
	}
	
	public void send(Message msg) {

		// Send message to all subscribers
		ConcurrentLinkedQueue<IEngine> allListeners = subscribers.get(msg.getClass());
		if (allListeners != null) {
			for (IEngine listener : allListeners) {
				listener.onMessage(msg);
			}
		}
	}
}
