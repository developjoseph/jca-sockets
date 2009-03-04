package com.googlecode.jcasockets;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ConformanceClientCliTest {

	@Test 
	public void testCommandLineParsingOfExecutionSeconds() throws Exception{
		ConformanceClientCli conformanceClient = new ConformanceClientCli( );
		String argument = getOption(ConformanceClientCli.OPTION_EXECUTION_SECONDS, " 5");
		conformanceClient.parseArguments( argument); 
		assertEquals( 5,  conformanceClient.getExecutionSeconds( ) );
	}
	private String getOption(String option, String value ){
		return "-" + option + value; 
	}
}
