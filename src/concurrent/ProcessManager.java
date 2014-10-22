package concurrent;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import messaging.Message;
import messaging.Publisher;
import messaging.events.ResumeMessage;
import common.IEngine;

public class ProcessManager extends ThreadPoolExecutor implements IEngine {

	private final static int POOL_SIZE = 5;
	private final static long TIMEOUT = 0L;
	private final Queue<IEngine> queued;

	private static ProcessManager instance = null;
	private boolean started;

	public static ProcessManager getManager() {
		if (instance == null)
			instance = new ProcessManager();

		return instance;
	}

	private ProcessManager() {

		super(POOL_SIZE, POOL_SIZE, TIMEOUT, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>());

		queued = new LinkedList<IEngine>();
		started = false;
	}

	public synchronized void add(IEngine r) {

		if (started || this.isShutdown() || this.isTerminating()
				|| this.isTerminated())
			return;
		if (queued.contains(r))
			return;

		this.queued.add(r);
	}

	public void stop() {

		if (!started || this.isShutdown() || this.isTerminating()
				|| this.isTerminated())
			return;

		// make sure nothing is paused
		Publisher.getInstance().send(new ResumeMessage());

		for (IEngine r : queued) {
			// Reach down and interrupt the threads
			r.stop();
		}

		started = false;
	}

	public void close() {

		if (this.isShutdown() || this.isTerminating() || this.isTerminated())
			return;

		this.stop();
		this.shutdown();

		while (this.isTerminating()) {
			// block
		}

		if (!this.isShutdown())
			this.shutdownNow();

	}

	public void start() {

		if (started || this.isShutdown() || this.isTerminating()
				|| this.isTerminated())
			return;

		System.out.println("starting...");

		System.out.println(this.getActiveCount());

		for (IEngine r : queued) {
			System.out.println("starting " + r);
			this.execute(r);
		}

		System.out.println(this.getActiveCount());

		started = true;
	}

	@Override
	public void onMessage(Message msg) {
		msg.process(this);
	}

	@Override
	public void generate() {
		return;
	}

	@Override
	public void run() {
		return;
	}

	@Override
	public void performAction() {
		return;
	}

	@Override
	public void configure(int gs, int timeStep) {
		return;
	}

	@Override
	public void processQueue() {
		return;
	}

	public void pause() {
		return;
	}

	public void resume() {
		return;
	}
}