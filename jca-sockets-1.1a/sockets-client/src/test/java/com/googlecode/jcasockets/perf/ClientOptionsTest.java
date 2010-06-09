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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import org.junit.Test;

import com.googlecode.jcasockets.perf.ClientOptions;

public class ClientOptionsTest {

	@Test 
	public void testCommandLineParsingDefaults() throws Exception{
		ClientOptions clientOptions = new ClientOptions( );
		clientOptions.parseArguments( ); 
		assertEquals( ClientOptions.DEFAULT_EXECUTION_SECONDS,  clientOptions.getExecutionSeconds( ) );
		assertEquals( ClientOptions.DEFAULT_NUMBER_OF_THREAD,  clientOptions.getNumberOfThreads( ) );
		assertEquals( ClientOptions.DEFAULT_MIN_MESSAGE_SIZE,  clientOptions.getMinimumMessageSize() );
		assertEquals( ClientOptions.DEFAULT_MAX_MESSAGE_SIZE,  clientOptions.getMaximumMessageSize() );
		assertEquals( false,  clientOptions.isHelpRequested() );
	}
	@Test 
	public void testCommandLineParsingOfHelp() throws Exception{
		ClientOptions clientOptions = new ClientOptions( );
		String argument = getOption(ClientOptions.OPTION_HELP);
		clientOptions.parseArguments( argument); 
		assertTrue(  clientOptions.isHelpRequested( ) );
	}
	@Test 
	public void testCommandLineParsingOfSinglePort() throws Exception{
		ClientOptions clientOptions = new ClientOptions( );
		String argument = getOption(ClientOptions.OPTION_PORTS, " 9000");
		clientOptions.parseArguments( argument); 
		assertEquals( Arrays.asList(9000),  clientOptions.getPorts( ) );
	}
	@Test 
	public void testCommandLineParsingOfPorts() throws Exception{
		ClientOptions clientOptions = new ClientOptions( );
		String argument = getOption(ClientOptions.OPTION_PORTS, "100,123,111");
		clientOptions.parseArguments( argument); 
		assertEquals( Arrays.asList(100,123,111),  clientOptions.getPorts( ) );
	}
	@Test 
	public void testCommandLineParsingOfExecutionSeconds() throws Exception{
		ClientOptions clientOptions = new ClientOptions( );
		String argument = getOption(ClientOptions.OPTION_EXECUTION_SECONDS, " 5");
		clientOptions.parseArguments( argument); 
		assertEquals( 5,  clientOptions.getExecutionSeconds( ) );
	}
	@Test 
	public void testCommandLineParsingOfMinimumMessageSize() throws Exception{
		ClientOptions clientOptions = new ClientOptions( );
		String argument = getOption(ClientOptions.OPTION_MIN_MESSAGE_SIZE, " 5");
		clientOptions.parseArguments( argument); 
		assertEquals( 5,  clientOptions.getMinimumMessageSize( ) );
	}
	@Test 
	public void testCommandLineParsingOfMaximumMessageSize() throws Exception{
		ClientOptions clientOptions = new ClientOptions( );
		String argument = getOption(ClientOptions.OPTION_MAX_MESSAGE_SIZE, "200");
		clientOptions.parseArguments( argument); 
		assertEquals( 200,  clientOptions.getMaximumMessageSize( ) );
	}
	@Test 
	public void testCommandLineParsingOfNumberOfThreads() throws Exception{
		ClientOptions clientOptions = new ClientOptions( );
		String argument = getOption(ClientOptions.OPTION_NUMBER_OF_THREAD, "3 ");
		clientOptions.parseArguments( argument); 
		assertEquals( 3,  clientOptions.getNumberOfThreads() );
	}
	@Test 
	public void testCommandLineParsingOfIpAddress() throws Exception{
		ClientOptions clientOptions = new ClientOptions( );
		String argument = getOption(ClientOptions.OPTION_IP_ADDRESS, " myhost ");
		clientOptions.parseArguments( argument); 
		assertEquals( "myhost",  clientOptions.getIpAddress() );
	}

	@Test 
	public void testCommandLineParsingNoHelp() throws Exception{
		ClientOptions clientOptions = new ClientOptions( );
		String argument = getOption(ClientOptions.OPTION_EXECUTION_SECONDS, "3");
		clientOptions.parseArguments( argument); 
		assertFalse(  clientOptions.isHelpRequested( ) );
	}
	@Test 
	public void testPrintHelp() throws Exception{
		ClientOptions clientOptions = new ClientOptions( );
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(os);
		clientOptions.printHelp(printStream);
		printStream.flush();
		assertTrue( os.toString().contains( "-h,--help" ));
		
	}
	private String getOption(String option) {
		return "-" + option;
	}
	private String getOption(String option, String value ){
		return getOption( option ) + value; 
	}
}
