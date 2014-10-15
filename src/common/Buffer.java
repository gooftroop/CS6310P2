package common;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Buffer implements IBuffer {
	
	private BlockingQueue<IGrid> buffer;
	
	private static int size;
	private static Buffer instance = null;
	private static LinkedList<ICallback> callbacks = new LinkedList<ICallback>();
	
	public static Buffer getBuffer() {
		if (instance == null) instance = new Buffer();
		
		return instance;
	}
	
	public void create(int size) {
		
		if (size < 1 || size > Integer.MAX_VALUE) 
			throw new IllegalArgumentException("Invalid size");

		Buffer.size = size;
		buffer = new LinkedBlockingQueue<IGrid>(size);
	}
	
	public static void addCallback(ICallback c) {
		if (!callbacks.contains(c))
			callbacks.add(c);
	}
	
	public static void removeCallback(ICallback c) {
		if (callbacks.contains(c))
			callbacks.remove(c);
	}
	
	private Buffer() {
		// do nothing
	}
	
	@Override
	public void add(IGrid grid) {
		try {
			buffer.offer(grid);
		} finally {
			for (ICallback c : callbacks)
				c.invoke();
		}
	}

	@Override
	public IGrid get() throws InterruptedException {
		try {
			return buffer.take();
		} finally {
			for (ICallback c : callbacks)
				c.invoke();
		}
	}
	
	@Override
	public int size() {
		return buffer.size();
	}

	@Override
	public int getCapacity() {
		return Buffer.size;
	}

	@Override
	public int getRemainingCapacity() {
		return buffer.remainingCapacity();
	}
}