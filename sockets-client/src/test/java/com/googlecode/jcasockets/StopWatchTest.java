package com.googlecode.jcasockets;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

public class StopWatchTest {
	
	private TimeProvider timeProvider;

	@Before
	public void setUp(){
		timeProvider = createStrictMock( TimeProvider.class );
	}
	@After
	public void tearDown(){
		verify(timeProvider);
	}
	
	@Test
	public void testStartStop() {
		expect( timeProvider.nanoTime() ).andReturn(1L);
		expect( timeProvider.nanoTime() ).andReturn(5L);
		replay(timeProvider);
		StopWatch stopWatch = new StopWatch(timeProvider);
		stopWatch.start();
		stopWatch.stop();
		long elapsed = stopWatch.getElapsed( TimeUnit.NANOSECONDS );
		assertEquals( 4, elapsed);

	}
}
