package com.googlecode.jcasockets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ConformanceClientCliTest {

	@Test 
	public void testCommandLineParsingDefaults() throws Exception{
		ConformanceClientCli conformanceClient = new ConformanceClientCli( );
		conformanceClient.parseArguments( "" ); 
		assertEquals( ConformanceClientCli.DEFAULT_EXECUTION_SECONDS,  conformanceClient.getExecutionSeconds( ) );
		assertEquals( ConformanceClientCli.DEFAULT_NUMBER_OF_THREAD,  conformanceClient.getNumberOfThreads( ) );
		assertEquals( false,  conformanceClient.isHelpRequested() );
	}
	@Test 
	public void testCommandLineParsingOfHelp() throws Exception{
		ConformanceClientCli conformanceClient = new ConformanceClientCli( );
		String argument = getOption(ConformanceClientCli.OPTION_HELP);
		conformanceClient.parseArguments( argument); 
		assertTrue(  conformanceClient.isHelpRequested( ) );
	}
	@Test 
	public void testCommandLineParsingOfExecutionSeconds() throws Exception{
		ConformanceClientCli conformanceClient = new ConformanceClientCli( );
		String argument = getOption(ConformanceClientCli.OPTION_EXECUTION_SECONDS, " 5");
		conformanceClient.parseArguments( argument); 
		assertEquals( 5,  conformanceClient.getExecutionSeconds( ) );
	}
	@Test 
	public void testCommandLineParsingOfnumberOfThreads() throws Exception{
		ConformanceClientCli conformanceClient = new ConformanceClientCli( );
		String argument = getOption(ConformanceClientCli.OPTION_NUMBER_OF_THREAD, "3 ");
		conformanceClient.parseArguments( argument); 
		assertEquals( 3,  conformanceClient.getNumberOfThreads() );
	}

	@Test 
	public void testCommandLineParsingNoHelp() throws Exception{
		ConformanceClientCli conformanceClient = new ConformanceClientCli( );
		String argument = getOption(ConformanceClientCli.OPTION_EXECUTION_SECONDS, "3");
		conformanceClient.parseArguments( argument); 
		assertFalse(  conformanceClient.isHelpRequested( ) );
	}
	private String getOption(String option) {
		return "-" + option;
	}
	private String getOption(String option, String value ){
		return getOption( option ) + value; 
	}
}
