package com.googlecode.jcasockets;

import static org.junit.Assert.*;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

public class ConformanceClientTest {
	@Test
	public void testMessageSize() throws ParseException{
		ConformanceClient conformanceClient = new ConformanceClient( "-s 1 -m 3 -M15");
		MockSocketSender socketSender = new MockSocketSender();
		conformanceClient.setSender( socketSender );
		conformanceClient.execute();
		int messageCount = socketSender.getMessageCount();
		assertTrue(messageCount > 0 );
	}

}
