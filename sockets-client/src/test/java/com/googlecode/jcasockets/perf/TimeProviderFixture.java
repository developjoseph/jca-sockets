package com.googlecode.jcasockets.perf;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import com.googlecode.jcasockets.perf.TimeProvider;

public class TimeProviderFixture {

	public static TimeProvider createTimeProvider(long... nanoTimes) {
		TimeProvider timeProvider = createStrictMock( TimeProvider.class );
		
		for (long nanoTime : nanoTimes) {
			expect( timeProvider.nanoTime() ).andReturn(nanoTime);
		}
		replay(timeProvider);
		return timeProvider;
	}

	public static TimeProvider createIncrementalTimeProvider() {
		return new TimeProvider(){
			long time;

			@Override
			public long nanoTime() {
				return time++;
			}
		};
	}
	
}
