package com.googlecode.jcasockets;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import javax.resource.spi.UnavailableException;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.endpoint.MessageEndpointFactory;

import org.easymock.EasyMock;
import org.junit.Test;

public class EndpointPoolTest {
	private interface MockEndpoint extends SocketMessageEndpoint, MessageEndpoint{
	}
	private class TestObject{
		MessageEndpointFactory endpointFactory = EasyMock.createNiceMock(MessageEndpointFactory.class);
		private SocketMessageEndpoint[] socketMessageEndpoints;
		
		public TestObject(int numEndpoints) throws UnavailableException {
			endpointFactory = EasyMock.createNiceMock(MessageEndpointFactory.class);
			socketMessageEndpoints = new SocketMessageEndpoint[numEndpoints];
			for (int i = 0; i < numEndpoints ; i++) {
				socketMessageEndpoints[i] = EasyMock.createNiceMock(MockEndpoint.class); 
				EasyMock.expect( endpointFactory.createEndpoint(null) ).andReturn((MessageEndpoint)socketMessageEndpoints[i]);
			}
			EasyMock.replay(endpointFactory);
		}
		public void assertEndpointNum( SocketMessageEndpoint socketMessageEndpoint, int num ){
			assertTrue("Expected " + num , socketMessageEndpoint == socketMessageEndpoints[num] );
		}
	}
	
	@Test
	public void testGetNewWithRelease() throws UnavailableException{
		TestObject testObject = new TestObject( 2 );
		EndpointPool endpointPool = new EndpointPool( testObject.endpointFactory, 2, 1, TimeUnit.MILLISECONDS);
		
		SocketMessageEndpoint endpoint = endpointPool.getEndpoint();
		testObject.assertEndpointNum(endpoint, 0);
		endpointPool.release(endpoint);
		testObject.assertEndpointNum(endpoint, 0);
	}
	
	@Test
	public void testCreatePool() throws UnavailableException{
		TestObject testObject = new TestObject( 2 );
		EndpointPool endpointPool = new EndpointPool( testObject.endpointFactory, 2, 1, TimeUnit.MILLISECONDS);
		SocketMessageEndpoint actual = endpointPool.getEndpoint();
		testObject.assertEndpointNum(actual,0 ); 
	}

	
	@Test
	public void testGetNew() throws UnavailableException{
		TestObject testObject = new TestObject( 2 );
		EndpointPool endpointPool = new EndpointPool( testObject.endpointFactory, 2, 1, TimeUnit.MILLISECONDS);
		
		testObject.assertEndpointNum(endpointPool.getEndpoint(), 0);
		testObject.assertEndpointNum(endpointPool.getEndpoint(), 1);
	}
	@Test
	public void testBlockWithTimeout() throws UnavailableException {
		TestObject testObject = new TestObject( 2 );
		long millisBlock = 100;
		EndpointPool endpointPool = new EndpointPool( testObject.endpointFactory, 2, millisBlock, TimeUnit.MILLISECONDS);
		
		SocketMessageEndpoint actual1 = endpointPool.getEndpoint();
		testObject.assertEndpointNum(actual1,0 ); 
		SocketMessageEndpoint actual2 = endpointPool.getEndpoint();
		testObject.assertEndpointNum(actual2,1 ); 
		
		long currentTimeMillis = System.currentTimeMillis();
		try {
			endpointPool.getEndpoint();
			fail("Expected unavailable exception");
		} catch (UnavailableException e) {
			long elapsedMillis = System.currentTimeMillis() - currentTimeMillis;
			// test for 20% more than the blocking time (clock inaccuracies)
			assertTrue( elapsedMillis * 1.2 >= millisBlock);
		}
	}
}
