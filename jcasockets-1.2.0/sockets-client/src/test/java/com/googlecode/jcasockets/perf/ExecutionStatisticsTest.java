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
package com.googlecode.jcasockets.perf;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class ExecutionStatisticsTest {

	@Test
	public void testSendReceiveMesages() {
		TimeProvider timeProvider = TimeProviderFixture.createTimeProvider(TimeUnit.MILLISECONDS, 1,4, 5,8  );
		ExecutionStatistics executionStatistics = new ExecutionStatistics(timeProvider);
		executionStatistics.recordSend("xx");
		executionStatistics.recordReceive("xx");
		assertEquals(4, executionStatistics.getBytesSent());
		assertEquals(4, executionStatistics.getBytesReceived());
		assertEquals(1, executionStatistics.getMessagesSent());
		assertEquals(1, executionStatistics.getMessagesReceived());
		assertEquals(4, executionStatistics.getMinimumMessageSize());
		assertEquals(4, executionStatistics.getMaximumMessageSize());
		assertEquals(3, executionStatistics.getElapsed(TimeUnit.MILLISECONDS));
		
		assertEquals(1333, executionStatistics.getBytesSentPerSecond());
		assertEquals(1333, executionStatistics.getBytesReceivedPerSecond());
		assertEquals(333, executionStatistics.getMessagesPerSecond());

		executionStatistics.recordSend("yyy");
		executionStatistics.recordReceive("Ryyy");
		assertEquals(10, executionStatistics.getBytesSent());
		assertEquals(12, executionStatistics.getBytesReceived());
		assertEquals(2, executionStatistics.getMessagesSent());
		assertEquals(2, executionStatistics.getMessagesReceived());
		assertEquals(4, executionStatistics.getMinimumMessageSize());
		assertEquals(6, executionStatistics.getMaximumMessageSize());
		assertEquals(6, executionStatistics.getElapsed(TimeUnit.MILLISECONDS));

		assertEquals(1667, executionStatistics.getBytesSentPerSecond());
		assertEquals(2000, executionStatistics.getBytesReceivedPerSecond());
		assertEquals(333, executionStatistics.getMessagesPerSecond());
	}

	@Test
	public void testCombineMesages() {
		TimeProvider timeProvider = TimeProviderFixture.createIncrementalTimeProvider();
		ExecutionStatistics executionStatistics1 = new ExecutionStatistics(timeProvider);
		executionStatistics1.recordSend("xxx");
		executionStatistics1.recordReceive("xxx");
		ExecutionStatistics executionStatistics2 = new ExecutionStatistics(timeProvider);
		executionStatistics2.recordSend("yyyy");
		executionStatistics2.recordReceive("yyyy");
		ExecutionStatistics executionStatistics = new ExecutionStatistics(null);
		executionStatistics.combine(executionStatistics1);
		executionStatistics.combine(executionStatistics2);

		assertEquals(14, executionStatistics.getBytesSent());
		assertEquals(14, executionStatistics.getBytesReceived());
		assertEquals(2, executionStatistics.getMessagesSent());
		assertEquals(2, executionStatistics.getMessagesReceived());
		assertEquals(8, executionStatistics.getMaximumMessageSize());
		assertEquals(6, executionStatistics.getMinimumMessageSize());

	}
}
