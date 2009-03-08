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
		StopWatch stopWatch = createStopWatch(1,5);
		stopWatch.start();
		stopWatch.stop();
		long elapsed = stopWatch.getElapsed( TimeUnit.NANOSECONDS );
		assertEquals( 4, elapsed);
	}
	@Test
	public void testGetElapsedWhenStarted() {
		StopWatch stopWatch = createStopWatch(1);
		stopWatch.start();  
		try {
			stopWatch.getElapsed(TimeUnit.NANOSECONDS);  
			fail("Should not be able get elapsed time unless stopped."); 
		} catch (IllegalStateException e) {
			// expected this
		}
	}
	@Test
	public void testStopWhenStopped() {
		StopWatch stopWatch = createStopWatch();
		try {
			stopWatch.stop(); 
			fail("Should not be able to stop a stopped watch, (initial state is stopped)"); 
		} catch (IllegalStateException e) {
			// expected this
		}
	}
	
	@Test
	public void testStartWhenStarted() {
		StopWatch stopWatch = createStopWatch(1);
		stopWatch.start();  
		try {
			stopWatch.start();  
			fail("Should not be able to start a started watch"); 
		} catch (IllegalStateException e) {
			// expected this
		}
	}
	
	private StopWatch createStopWatch(long... nanoTimes) {
		for (long nanoTime : nanoTimes) {
			expect( timeProvider.nanoTime() ).andReturn(nanoTime);
		}
		replay(timeProvider);
		return new StopWatch(timeProvider);
	}
}
