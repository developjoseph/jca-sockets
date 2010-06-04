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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

import com.googlecode.jcasockets.perf.Client;
import com.googlecode.jcasockets.perf.ExecutionStatistics;

public class ClientTest {

	@Test
	public void whenSingleThreadSendsMessages_MessagesAreSentCorrectly() throws Exception {
		Client client = getClient("-s 1 -m 3 -M 3 -p 8888");
		int expectedBytesIsTwiceSentMessage = 6;

		MockSocketSender socketSender = new MockSocketSender();
		client.setSender(socketSender);
		client.execute();
		ExecutionStatistics executionStatistics = client.getExecutionStatistics();

		assertTrue(executionStatistics.getMessagesSent() > 0);
		assertEquals(expectedBytesIsTwiceSentMessage, executionStatistics.getMinimumMessageSize());  // 2 * number of characters sent   
		assertEquals(expectedBytesIsTwiceSentMessage, executionStatistics.getMaximumMessageSize());   // 2 * number of characters sent

	}


	@Test
	public void whenMultipleThreadsSendMessages_MessagesAreSentCorrectly() throws Exception {

		Client client = getClient("-s 1 -t3 -m10 -M10 -p 8888");
		int expectedBytesIsTwiceSentMessage = 20;
		MockSocketSender socketSender = new MockSocketSender();

		client.setSender(socketSender);
		client.execute();
		ExecutionStatistics executionStatistics = client.getExecutionStatistics();

		assertTrue(executionStatistics.getMessagesSent() > 0);
		assertEquals(expectedBytesIsTwiceSentMessage, executionStatistics.getMinimumMessageSize());  
		assertEquals(expectedBytesIsTwiceSentMessage, executionStatistics.getMaximumMessageSize());
	}

	private Client getClient(String string) throws ParseException {
		String[] args = string.split(" ");
		Client client = new Client(args);
		return client;
	}

	@Test
	public void testExecutionStatisticsFormattedAsCSV() throws Exception {
		TimeProvider timeProvider = TimeProviderFixture.createTimeProvider(TimeUnit.SECONDS, 1,2,3,4,5,6);
		ExecutionStatistics executionStatistics = new ExecutionStatistics(timeProvider);
		executionStatistics.recordSend("12345");
		executionStatistics.recordReceive("R12345");
		executionStatistics.recordSend("1");
		executionStatistics.recordReceive("R1");
		executionStatistics.recordSend("1234567890");
		executionStatistics.recordReceive("R123456789");
		
		String[] values = getExectuionStatsAsCSV(executionStatistics);
		assertEquals(10, values.length);
		assertCsvHeaderAndBodyHaveTheSameNumberOfValues(values);
		
		assertEquals(executionStatistics.getBytesSent(), Integer.parseInt(values[0]));
		assertEquals(executionStatistics.getBytesReceived(), Integer.parseInt(values[1]));
		assertEquals(executionStatistics.getElapsed(TimeUnit.MILLISECONDS), Integer.parseInt(values[2]));
		assertEquals(executionStatistics.getMessagesSent(), Integer.parseInt(values[3]));
		assertEquals(executionStatistics.getMessagesReceived(), Integer.parseInt(values[4]));
		assertEquals(executionStatistics.getMinimumMessageSize(), Integer.parseInt(values[5]));
		assertEquals(executionStatistics.getMaximumMessageSize(), Integer.parseInt(values[6]));
		assertEquals(executionStatistics.getBytesSentPerSecond(), Integer.parseInt(values[7]));
		assertEquals(executionStatistics.getBytesReceivedPerSecond(), Integer.parseInt(values[8]));
		assertEquals(executionStatistics.getMessagesPerSecond(), Integer.parseInt(values[9]));
	}


	private void assertCsvHeaderAndBodyHaveTheSameNumberOfValues(String[] values) {
		assertEquals(getCSVHeaderStrings().length, values.length);
	}


	private String[] getExectuionStatsAsCSV(ExecutionStatistics executionStatistics) {
		ByteArrayOutputStream os = new ByteArrayOutputStream(); 
		PrintStream printStream = new PrintStream( os ); 
		Client.printStatisticsAsCSV(printStream, executionStatistics);
		String csv = os.toString().trim();
		String[] values = csv.split(",");
		return values;
	}


	private String[] getCSVHeaderStrings() {
		ByteArrayOutputStream os = new ByteArrayOutputStream(); 
		PrintStream printStream = new PrintStream( os ); 
		Client.printStatisticsHeaderAsCSV(printStream);
		String csv = os.toString().trim();
		String[] values = csv.split(",");
		return values;
	}
}
