package tests.util;

import common.IEngine;

import messaging.Message;

public class ConcreteMsg2 implements Message {
	public int someDataHere;

	@Override
	public void process(IEngine l) {
		System.out.printf("processor2 called! (%s)\n", this.getClass().getName());
		
	}
}
