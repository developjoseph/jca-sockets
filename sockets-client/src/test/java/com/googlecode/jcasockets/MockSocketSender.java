package com.googlecode.jcasockets;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MockSocketSender implements SocketSender {

	private ConcurrentMap<Thread, AtomicInteger> messageCountMap = new ConcurrentHashMap<Thread, AtomicInteger>();
	private int minimumMessageSize = Integer.MAX_VALUE;
	private int maximumMessageSize = Integer.MIN_VALUE;;  

	@Override
	public String send(String send) {
		Thread currentThread = Thread.currentThread();
		AtomicInteger existing = messageCountMap.putIfAbsent(currentThread, new AtomicInteger(1));
		if (existing != null){
			existing.getAndIncrement();
		}
		int length = send.length();
		if( length < minimumMessageSize){
			minimumMessageSize = length;
		}
		if( length > maximumMessageSize){
			maximumMessageSize = length;
		}
		return send;
	}

	public int getMessageCount() {
		int totalMessageCount = 0;
		for ( AtomicInteger messageCounts : messageCountMap.values()) {
			totalMessageCount += messageCounts.get();
		}
		return totalMessageCount;
	}
	public int getMaximumMessageSize() {
		return maximumMessageSize;
	}

	public int getMinimumMessageSize() {
		return minimumMessageSize;
	}

	public int getNumThreads() {
		return messageCountMap.size();
	}
}
