package tests;

import java.util.concurrent.ArrayBlockingQueue;

import messaging.Message;

import common.AbstractEngine;

public class DummyModel extends AbstractEngine {
	
	ArrayBlockingQueue<Integer> q;
	
	public DummyModel(ArrayBlockingQueue<Integer> q) {
		this.q = q;
	}
	
	@Override
	public <T extends Message> void dispatchMessage(T msg) {
		msg.process(this);
	}


	@Override
	public void close() {
		// destructor when done with class
	}

	@Override
	public void generate() {
		try {
			q.put(1);
			System.out.printf("added generated data to buffer\n");
		} catch (InterruptedException e) {
			// Do nothing
		}
	}

	@Override
	public void configure(int gs, int timeStep) {
		return;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
}
