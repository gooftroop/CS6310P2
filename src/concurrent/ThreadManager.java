package concurrent;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadManager extends ThreadPoolExecutor {
	
	private final static int POOL_SIZE = 2;
	private final static long TIMEOUT = 0L;
	private final Queue<Runnable> queued;

	public ThreadManager() {
		
		super(POOL_SIZE, POOL_SIZE, TIMEOUT, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		
		queued = new LinkedList<Runnable>();
	}

	public synchronized void add(Runnable r) {
		
		if (this.isShutdown() || this.isTerminating() || this.isTerminated()) return;
		if (queued.contains(r)) return;
		
		this.queued.add(r);
	}
	
	public void stop() {
		
		if (this.isShutdown() || this.isTerminating() || this.isTerminated()) return;
		
		this.shutdown();
		
		while(this.isTerminating()) {
			// block
		}
		
		if (!this.isShutdown()) this.shutdownNow();
	}

	public void start() {
		if (this.isShutdown() || this.isTerminating() || this.isTerminated()) return;
		for (Runnable r : this.queued) this.execute(r);
	}
}