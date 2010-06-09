package com.googlecode.jcasockets.protocol;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

public class LengthPrefixedProtocolTest extends UnitilsJUnit4 {
	
	class TestProtocol extends AbstractLengthPrefixedMessageProtocol{

		public TestProtocol(Socket socket, int sizeOfLengthField) throws IOException {
			super(socket, sizeOfLengthField);
		}

		@Override
		boolean isEndOfConversation(String message) {
			return message.length() == 0;
		}
	}
	
	private Mock<Socket> socket;
	private final String message = "Abcedef";
	private final String messageWithLengthPrefix = "007Abcedef";
	private final int sizeOfLengthFiled = 3;
	private ByteArrayOutputStream out = new ByteArrayOutputStream();
	private TestProtocol testProtocol;

	public void setupMocks(String inputMessage) throws IOException{
		ByteArrayInputStream in = new ByteArrayInputStream(inputMessage.getBytes());
		socket.returns(in).getInputStream();
		socket.returns(out).getOutputStream();
		testProtocol = new TestProtocol(socket.getMock(), sizeOfLengthFiled );
	}

	@Test
	public void testWriteIsOk() throws Exception {
		setupMocks("");
		testProtocol.writeMessage(message);
		assertEquals( messageWithLengthPrefix, out.toString() ); 
	}

	@Test
	public void testReadIsOk() throws Exception {
		setupMocks(messageWithLengthPrefix);
		String readMessage = testProtocol.readMessage();
		assertEquals( message, readMessage ); 
		assertFalse( testProtocol.isEndOfConversation() ); 
	}
	@Test
	public void testReadMultipleMessagesOk() throws Exception {
		setupMocks(messageWithLengthPrefix + messageWithLengthPrefix);
		String readMessage1 = testProtocol.readMessage();
		assertEquals( message, readMessage1 ); 
		String readMessage2 = testProtocol.readMessage();
		assertEquals( message, readMessage2 ); 
		assertFalse( testProtocol.isEndOfConversation() ); 
	}

	@Test
	public void testTerminationMessagesOk() throws Exception {
		setupMocks(messageWithLengthPrefix + "000");
		String readMessage1 = testProtocol.readMessage();
		assertEquals( message, readMessage1 ); 
		String readMessage2 = testProtocol.readMessage();
		assertNull( readMessage2 );
		assertTrue( testProtocol.isEndOfConversation() ); 
	}
	
}
