package com.googlecode.jcasockets;

import static org.junit.Assert.*;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

public class SenderTestRunnerTest {
	@Test
	public void testSmallMessageSize() throws Exception{
		ConformanceClientCli conformanceClient = getCommandLineOptions("-s1 -m3 -M15 -p8000");
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

	private ConformanceClientCli getCommandLineOptions(String string)
			throws ParseException {
		ConformanceClientCli conformanceClient = new ConformanceClientCli();
		String[] args = string.split(" ");
		conformanceClient.parseArguments( args );
		return conformanceClient;
	}
}
