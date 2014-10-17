package tests;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import tests.util.ContinuouslyConsumeMessage;
import messaging.Publisher;
import messaging.events.DisplayMessage;
import common.AbstractEngine;

public class DummyView extends AbstractEngine {

	private Publisher pub = Publisher.getInstance();
	ArrayBlockingQueue<Integer> q;

	public DummyView(ArrayBlockingQueue<Integer> q) {
		this.q = q;
	}

	private void present(Integer data) {
		System.out.printf("presented data\n");
		pub.send(new DisplayMessage());
	}

	@Override
	public void close() {
		// destructor when done with class
	}

	@Override
	public void generate() {
		// Check to see if there is anything in the data queue to process
		Integer data = null;
		try {
			data = q.poll(10, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			data = null;
		}

		// NOTE: we need to be careful that we don't do something silly like
		// spam
		// a bazillion messages while paused. (don't think this is a problem
		// now but we should ensure it is not later.)
		if (data != null) {
			// NOTE: we need to do something here to manage the animation update
			// rate...
			present(data);
		} else {
			pub.send(new ContinuouslyConsumeMessage());
		}
	}

	@Override
	public void configure(int gs, int timeStep) {
		return;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
}
