package com.googlecode.jcasockets;

import static org.junit.Assert.*;

import org.junit.Test;

public class ExecutionStatisticsTest {
	@Test
	public void testSendMesages() {
		ExecutionStatistics executionStatistics = new ExecutionStatistics();
		executionStatistics.recordSend( "xx" );
		assertEquals(4, executionStatistics.getBytesSent());
		assertEquals(0, executionStatistics.getBytesReceived());
		assertEquals(1, executionStatistics.getMessagesSent());
		assertEquals(0, executionStatistics.getMessagesReceived());
		assertEquals(4, executionStatistics.getMaximumMessageSize());
		assertEquals(4, executionStatistics.getMinimumMessageSize());
		executionStatistics.recordSend( "yyy" );
		assertEquals(10, executionStatistics.getBytesSent());
		assertEquals(0, executionStatistics.getBytesReceived());
		assertEquals(2, executionStatistics.getMessagesSent());
		assertEquals(0, executionStatistics.getMessagesReceived());
		assertEquals(6, executionStatistics.getMaximumMessageSize());
		assertEquals(4, executionStatistics.getMinimumMessageSize());
	}

	@Test
	public void testReceiveMesages() {
		ExecutionStatistics executionStatistics = new ExecutionStatistics();
		executionStatistics.recordReceive( "xx" );
		assertEquals(0, executionStatistics.getBytesSent());
		assertEquals(4, executionStatistics.getBytesReceived());
		assertEquals(0, executionStatistics.getMessagesSent());
		assertEquals(1, executionStatistics.getMessagesReceived());
		executionStatistics.recordReceive( "yyy" );
		assertEquals(0, executionStatistics.getBytesSent());
		assertEquals(10, executionStatistics.getBytesReceived());
		assertEquals(0, executionStatistics.getMessagesSent());
		assertEquals(2, executionStatistics.getMessagesReceived());
	}
	@Test
	public void testCombineMesages() {
		ExecutionStatistics executionStatistics1 = new ExecutionStatistics();
		executionStatistics1.recordSend( "xxx" );
		executionStatistics1.recordReceive( "xxx" );
		ExecutionStatistics executionStatistics2 = new ExecutionStatistics();
		executionStatistics2.recordSend( "yyyy" );
		executionStatistics2.recordReceive( "yyyy" );
		ExecutionStatistics executionStatistics = new ExecutionStatistics();
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
