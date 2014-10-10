package tests;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import common.ComponentBase;

import messaging.Message;
import messaging.Publisher;
import messaging.events.DisplayMessage;
import messaging.events.NeedDisplayDataMessage;

public class DummyView extends ComponentBase {
	private Publisher pub = Publisher.getInstance();
	ArrayBlockingQueue<Integer> q;
	
	public DummyView(ArrayBlockingQueue<Integer> q) {
		this.q = q;
	}
	
	@Override
	public void dispatchMessage(Message msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void runAutomaticActions() {
		// Check to see if there is anything in the data queue to process
		Integer data = null;
		try {
			data = q.poll(10, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(data != null) {
			present(data);
		}
		else {
			pub.send(new NeedDisplayDataMessage());
		}
	}
	
	private void present(Integer data) {
		System.out.printf("presenting data\n");
		pub.send(new DisplayMessage());
	}

	public void close() {
		// destructor when done with class
	}
}
