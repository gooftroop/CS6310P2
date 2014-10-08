package concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadManager extends ThreadPoolExecutor {
	
	private final static int POOL_SIZE = 2;
	private final static long TIMEOUT = 0L;

	public ThreadManager() {
		
		super(POOL_SIZE, POOL_SIZE, TIMEOUT, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		// TODO Auto-generated constructor stub
	}

	public void addRunnableSim(SimRunner r) {
		
		this.execute(r);
	}
	
	public void stop() {
		
		this.shutdown();
		
		while(this.isTerminating()) {
			// block
		}
		
		if (!this.isShutdown()) this.shutdownNow();
	}
}