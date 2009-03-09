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

import static org.junit.Assert.*;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

import com.googlecode.jcasockets.perf.ClientOptions;
import com.googlecode.jcasockets.perf.ExecutionStatistics;
import com.googlecode.jcasockets.perf.SenderTestRunner;
import com.googlecode.jcasockets.perf.SocketSender;

public class SenderTestRunnerTest {
	@Test
	public void testSmallMessageSize() throws Exception{
		ClientOptions conformanceClient = getCommandLineOptions("-s1 -m3 -M15 -p8000");
		SocketSender socketSender = new MockSocketSender();
		SenderTestRunner senderTestRunner = new SenderTestRunner(conformanceClient, socketSender);
		ExecutionStatistics executionStatistics = senderTestRunner.call();
		
		int messageCount = executionStatistics.getMessagesSent();
		assertTrue(messageCount > 0 );

		// TODO in fact this is a bit flakey it is possible (but unlikely) for this to fail
		// maybe the first 2 executions should not use random but max/min
		assertEquals( 6, executionStatistics.getMinimumMessageSize() );
		assertEquals( 30, executionStatistics.getMaximumMessageSize() );
		
	}

	private ClientOptions getCommandLineOptions(String string)
			throws ParseException {
		ClientOptions conformanceClient = new ClientOptions();
		String[] args = string.split(" ");
		conformanceClient.parseArguments( args );
		return conformanceClient;
	}
}
