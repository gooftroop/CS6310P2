package concurrent;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import messaging.Message;
import messaging.Publisher;
import messaging.events.ResumeMessage;
import common.IEngine;

public class ProcessManager extends ThreadPoolExecutor implements IEngine {
	
	private static final Object LSUSPEND = new Object();
	
	private final static int POOL_SIZE = 5;
	private final static long TIMEOUT = 0L;
	private final Queue<IEngine> queued;
	
	private static ProcessManager instance = null;
	
	public static ProcessManager getManager() {
		if (instance == null) instance = new ProcessManager();
		
		return instance;
	}

	private ProcessManager() {
		
		super(POOL_SIZE, POOL_SIZE, TIMEOUT, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		
		queued = new LinkedList<IEngine>();
	}

	public synchronized void add(IEngine r) {
		
		if (this.isShutdown() || this.isTerminating() || this.isTerminated()) return;
		if (queued.contains(r)) return;
		
		this.queued.add(r);
	}
	
	public void stop() {
		
		if (this.isShutdown() || this.isTerminating() || this.isTerminated()) return;
		
		// make sure nothing is paused
		Publisher.getInstance().send(new ResumeMessage());
		
		for (IEngine r : queued) {
			// Reach down and interrupt the threads
			r.stop();
		}
	}
	
	public void close() {
		
		if (this.isShutdown() || this.isTerminating() || this.isTerminated()) return;
		
		this.stop();
		this.shutdown();
		
		while(this.isTerminating()) {
			// block
		}
		
		if (!this.isShutdown()) this.shutdownNow();
		
	}

	public void start() {
		if (this.isShutdown() || this.isTerminating() || this.isTerminated()) return;
		for (IEngine r : queued) {
			this.execute(r);
		}
	}
	
	public void pause() {
		
		if (this.isShutdown() || this.isTerminating() || this.isTerminated()) return;
		
		for (IEngine r : queued) {
			try {
				r.pause(LSUSPEND);
			} catch (InterruptedException e) {
				// log and then stop
				System.err.println("An error occurred while pausing thread: " + e);
				this.stop();
			}
		}
	}

	// TODO convert to internal calling messages
	@Override
	public void onMessage(Message msg) {
		msg.process(this);
	}

	@Override
	public void generate() {
		// nothing to do
		return;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void performAction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configure(int gs, int timeStep) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processQueue() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause(Object lock) throws InterruptedException {
		
		for (IEngine r: queued) {
			r.pause(lock);
		}
	}
}