/*
 * Copyright 2009 Mark Jeffrey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.jcasockets;

import java.util.concurrent.TimeUnit;

public class StopWatch {
	private static enum State {
		RUNNING, STOPPED;
	}

	private final TimeProvider timeProvider;
	private long accumulatedTime;
	private long lastStartTime;
	private State state = State.STOPPED;

	public StopWatch(TimeProvider timeProvider) {
		this.timeProvider = timeProvider;
	}

	public void start() {
		if (state != State.STOPPED) {
			throw new IllegalStateException("Cannot stop a Stopwatch that is already running");
		}
		lastStartTime = timeProvider.nanoTime();
		state = State.RUNNING;
	}

	public void stop() {
		if (state != State.RUNNING) {
			throw new IllegalStateException("Cannot stop a Stopwatch that is already running");
		}
		long stopTime = timeProvider.nanoTime();
		accumulatedTime += stopTime - lastStartTime;
		state = State.STOPPED;
	}

	public long getElapsed(TimeUnit timeUnit) {
		if (state != State.STOPPED) {
			throw new IllegalStateException("Stopwatch must be stopped to get elapsed time");
		}
		return  timeUnit.convert(accumulatedTime,  TimeUnit.NANOSECONDS);
	}

	public void combine(StopWatch that) {
		this.accumulatedTime += that.accumulatedTime;
	}

}
