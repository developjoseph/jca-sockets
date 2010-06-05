package com.googlecode.jcasockets.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;

public class ProtocolTest extends UnitilsJUnit4{
	
	private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	private AbstractCharacterTerminatedMessageProtocol protocol;
	private Mock<Socket> mockSocket; 

	@Test
	public void testOngoingConversation() throws Exception {
		String messageString = "123456\n379";
		setupProtcolWithMessageAndExitToken(messageString, "dont call exit");
		assertEquals( "123456", protocol.getMessage());
		assertEquals( "379", protocol.getMessage());
		assertTrue(!protocol.isEndOfConversation());
	}
	@Test
	public void testEndOfConversationWhenExitTokenOnSeparateLine() throws Exception {
		String exitString = "EXIT";
		setupProtcolWithMessageAndExitToken("123456\n" + exitString, exitString);
		
		assertEquals( "123456", protocol.getMessage());
		assertNull( protocol.getMessage());
		assertTrue(protocol.isEndOfConversation());
	}

	@Test
	public void testClientSendsTokenIsAlwaysTrueForThisProtocol() throws Exception {
		setupProtcolWithMessageAndExitToken("ANY", "XXX");
		assertTrue( protocol.clientSendsToken() );
	}

	private AbstractCharacterTerminatedMessageProtocol setupProtcolWithMessageAndExitToken(String inString, String endToken)
			throws IOException {
		InputStream inputStream = new ByteArrayInputStream( inString.getBytes() );
		mockSocket.onceReturns( inputStream ).getInputStream( );
		mockSocket.onceReturns(byteArrayOutputStream).getOutputStream();
		protocol = new ClientSendsEndTokenProtocol(mockSocket.getMock(), endToken);
		return protocol;
	}
	
}
