package common;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Buffer implements IBuffer {
	
	private BlockingQueue<IGrid> buffer;
	
	private static int size;
	private static Buffer instance = null;
	
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
	
	private Buffer() {
		// do nothing
	}
	
	@Override
	public void add(IGrid grid) throws InterruptedException {
		System.out.println("inserting....current remaining capacity " + buffer.remainingCapacity());
		synchronized (buffer) {
			buffer.offer(grid, 3, TimeUnit.SECONDS);
		}
		System.out.println("inserted. Buffer size is now " + buffer.size() + ", and capacity is " + + buffer.remainingCapacity());
	}

	@Override
	public IGrid get() throws InterruptedException {
		System.out.println("getting....current remaining capacity " + buffer.remainingCapacity());
		synchronized (buffer) {
			IGrid grid =  buffer.poll(3, TimeUnit.SECONDS);
			System.out.println("got " + grid + ". Buffer size is now " + buffer.size() + ", and capacity is " + + buffer.remainingCapacity());
			return grid;
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