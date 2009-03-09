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
import static org.junit.Assert.assertTrue;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

import com.googlecode.jcasockets.perf.Client;
import com.googlecode.jcasockets.perf.ExecutionStatistics;

public class ClientTest {
	@Test
	public void testMultipleThread() throws Exception{

		Client conformanceClient = getConformanceClient( "-s 1 -t2 -m10 -M10 -p 9000");
		MockSocketSender socketSender = new MockSocketSender();
		
		conformanceClient.setSender( socketSender );
		conformanceClient.execute();
		ExecutionStatistics executionStatistics = conformanceClient.getExecutionStatistics();

		assertTrue(executionStatistics.getMessagesSent() > 0 );
		// TODO in fact this is a bit flakey it is possible (but unlikely) for this to fail
		// maybe the first 2 executions should not use random but max/min
		assertEquals( 20, executionStatistics.getMinimumMessageSize() );
		assertEquals( 20, executionStatistics.getMaximumMessageSize() );
	}

	private Client getConformanceClient(String string) throws ParseException {
		String[] args = string.split(" ");
		Client conformanceClient = new Client(args);
		return conformanceClient;
	}

	@Test
	public void testSmallMessageSize() throws Exception{
		Client conformanceClient = getConformanceClient( "-s 1 -m 3 -M15 -p 8888");
		MockSocketSender socketSender = new MockSocketSender();
		
		conformanceClient.setSender( socketSender );
		conformanceClient.execute();
		ExecutionStatistics executionStatistics = conformanceClient.getExecutionStatistics();
		
		assertTrue(executionStatistics.getMessagesSent() > 0 );

		// TODO in fact this is a bit flakey it is possible (but unlikely) for this to fail
		// maybe the first 2 executions should not use random but max/min
		assertEquals( 6, executionStatistics.getMinimumMessageSize() );
		assertEquals( 30, executionStatistics.getMaximumMessageSize() );
		
	}

	
}
