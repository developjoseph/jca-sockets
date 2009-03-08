package com.googlecode.jcasockets;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

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
