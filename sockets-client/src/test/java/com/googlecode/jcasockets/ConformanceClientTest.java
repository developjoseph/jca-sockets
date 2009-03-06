package com.googlecode.jcasockets;

import static org.junit.Assert.*;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

public class ConformanceClientTest {
	@Test
	public void testSmallMessageSize() throws ParseException{
		ConformanceClient conformanceClient = new ConformanceClient( "-s 1 -m 3 -M15");
		MockSocketSender socketSender = new MockSocketSender();
		
		conformanceClient.setSender( socketSender );
		conformanceClient.execute();
		int messageCount = socketSender.getMessageCount();
		assertTrue(messageCount > 0 );

		// TODO in fact this is a bit flakey it is possible (but unlikely) for this to fail
		assertEquals( 3, socketSender.getMinimumMessageSize() );
		assertEquals( 15, socketSender.getMaximumMessageSize() );
		assertEquals( 1, socketSender.getNumThreads() );
		
	}

	@Test
	public void testMultipleThread() throws ParseException{
		ConformanceClient conformanceClient = new ConformanceClient( "-s 1 -t2");
		MockSocketSender socketSender = new MockSocketSender();
		
		conformanceClient.setSender( socketSender );
		conformanceClient.execute();

		assertEquals( 2, socketSender.getNumThreads() );
		
	}
	
}
