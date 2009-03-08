package com.googlecode.jcasockets;

import java.util.concurrent.TimeUnit;

public class StopWatch {

	private final TimeProvider timeProvider;

	public StopWatch(TimeProvider timeProvider) {
		this.timeProvider = timeProvider;
	}

	public void start() {
		
	}

	public void stop() {
		
	}

	public long getElapsed(TimeUnit milliseconds) {
		return 0;
	}

}
