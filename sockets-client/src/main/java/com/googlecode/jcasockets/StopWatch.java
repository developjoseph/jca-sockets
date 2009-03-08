package com.googlecode.jcasockets;

import java.util.concurrent.TimeUnit;

public class StopWatch {
	private static enum State{
		RUNNING,
		STOPPED;
	}

	private final TimeProvider timeProvider;
	private long accumulatedTime;
	private long lastStartTime;
	private State state = State.STOPPED;

	public StopWatch(TimeProvider timeProvider) {
		this.timeProvider = timeProvider;
	}

	public void start() {
		if ( state != State.STOPPED){
			throw new IllegalStateException("Cannot stop a Stopwatch that is already running");
		}
		lastStartTime = timeProvider.nanoTime();
		state = State.RUNNING;
	}

	public void stop() {
		if ( state != State.RUNNING){
			throw new IllegalStateException("Cannot stop a Stopwatch that is already running");
		}
		long stopTime = timeProvider.nanoTime();
		accumulatedTime += stopTime - lastStartTime;
		state = State.STOPPED;
	}

	public long getElapsed(TimeUnit timeUnit) {
		return TimeUnit.NANOSECONDS.convert(accumulatedTime, timeUnit);
	}

}
