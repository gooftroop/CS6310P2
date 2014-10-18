package tests.util;

import common.IEngine;

import messaging.Message;

public class ConcreteMsg implements Message {
	public int someDataHere;

	@Override
	public void process(IEngine l) {
		System.out.printf("processor1 called! (%s)\n", this.getClass().getName());
	}
}
