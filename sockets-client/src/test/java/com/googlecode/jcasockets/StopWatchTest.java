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

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Test;

public class StopWatchTest {

	private TimeProvider timeProvider;

	@After
	public void tearDown() {
		if (timeProvider != null) {
			verify(timeProvider);
		}
	}

	@Test
	public void testStartStop() {
		StopWatch stopWatch = createStopWatch(1, 5);
		stopWatch.start();
		stopWatch.stop();
		long elapsed = stopWatch.getElapsed(TimeUnit.NANOSECONDS);
		assertEquals(4, elapsed);
	}

	@Test
	public void testStartStopMilliseconds() {
		StopWatch stopWatch = createStopWatch(0, 123456789);
		stopWatch.start();
		stopWatch.stop();
		long elapsed = stopWatch.getElapsed(TimeUnit.MILLISECONDS);
		assertEquals(123, elapsed);
	}

	@Test
	public void testGetElapsedWhenStarted() {
		StopWatch stopWatch = createStopWatch(1);
		stopWatch.start();
		try {
			stopWatch.getElapsed(TimeUnit.NANOSECONDS);
			fail("Should not be able get elapsed time unless stopped.");
		} catch (IllegalStateException e) {
			// expected this
		}
	}

	@Test
	public void testStopWhenStopped() {
		StopWatch stopWatch = createStopWatch();
		try {
			stopWatch.stop();
			fail("Should not be able to stop a stopped watch, (initial state is stopped)");
		} catch (IllegalStateException e) {
			// expected this
		}
	}

	@Test
	public void testStartWhenStarted() {
		StopWatch stopWatch = createStopWatch(1);
		stopWatch.start();
		try {
			stopWatch.start();
			fail("Should not be able to start a started watch");
		} catch (IllegalStateException e) {
			// expected this
		}
	}

	@Test
	public void testCombineStopWatches() {
		StopWatch stopWatch1 = new StopWatch(TimeProviderFixture.createIncrementalTimeProvider());
		StopWatch stopWatch2 = new StopWatch(TimeProviderFixture.createIncrementalTimeProvider());

		stopWatch1.start();
		stopWatch1.stop();
		stopWatch2.combine(stopWatch1);

		assertEquals(1, stopWatch1.getElapsed(TimeUnit.NANOSECONDS));
		assertEquals(1, stopWatch2.getElapsed(TimeUnit.NANOSECONDS));
	}

	private StopWatch createStopWatch(long... nanoTimes) {
		timeProvider = TimeProviderFixture.createTimeProvider(nanoTimes);
		return new StopWatch(timeProvider);
	}
}
