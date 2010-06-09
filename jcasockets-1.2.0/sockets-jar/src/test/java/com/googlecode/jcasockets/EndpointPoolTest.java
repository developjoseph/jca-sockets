package com.googlecode.jcasockets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;

import javax.resource.spi.UnavailableException;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.endpoint.MessageEndpointFactory;

import java.util.concurrent.TimeUnit;

public class EndpointPoolTest extends UnitilsJUnit4{

	@TestedObject
	private EndpointPool endpointPool;
	
	private Mock<MessageEndpointFactory> messageEndpointFactory;
	
	private Mock<MockEndpoint> endpoint1;
	private Mock<MockEndpoint> endpoint2;

	private static final int NUM_ENDPOINTS_IN_POOL = 2;
	private static final long MILLIS_TO_BLOCK = 100;

	@Before 
	public void setupMocks() throws UnavailableException{
		endpointPool = new EndpointPool( messageEndpointFactory.getMock(), NUM_ENDPOINTS_IN_POOL, MILLIS_TO_BLOCK, TimeUnit.MILLISECONDS);
		messageEndpointFactory.onceReturns( endpoint1.getMock() ).createEndpoint(null);
		messageEndpointFactory.onceReturns( endpoint2.getMock() ).createEndpoint(null);
	}
	@Test
	public void testReleaseKeepsTheEndpointInThePoolForReuse() throws UnavailableException{
		SocketMessageEndpoint actualEndPoint = endpointPool.getEndpoint();
		assertEquals(endpoint1.getMock(), actualEndPoint);
		endpointPool.release(actualEndPoint);
		assertEquals(endpoint1.getMock(), endpointPool.getEndpoint());
	}
	
	@Test
	public void testGetEndpointReturnsNewEndpoint() throws UnavailableException{
		assertEquals(endpoint1.getMock(), endpointPool.getEndpoint());
		assertEquals(endpoint2.getMock(), endpointPool.getEndpoint());
	}

	@Test
	public void testWhenPoolExhaustedPoolBlocksUntilTimeout() throws UnavailableException {
		assertEquals(endpoint1.getMock(), endpointPool.getEndpoint());
		assertEquals(endpoint2.getMock(), endpointPool.getEndpoint());
		assertNextCallBlocksUntilTimeout();
	}
	
	private void assertNextCallBlocksUntilTimeout() {
		long currentTimeMillis = System.currentTimeMillis();
		try {
			endpointPool.getEndpoint();
			fail("Expected unavailable exception");
		} catch (UnavailableException e) {
			long elapsedMillis = System.currentTimeMillis() - currentTimeMillis;
			// test for 20% more than the blocking time (clock inaccuracies)
			assertTrue( elapsedMillis * 1.2 >= MILLIS_TO_BLOCK);
		}
	}

	private interface MockEndpoint extends SocketMessageEndpoint, MessageEndpoint{
		
	}
}
