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

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class SenderTestRunner implements Callable<ExecutionStatistics> {
	private ClientOptions clientOptions;
	private SocketSender socketSender;
	private TimeProvider timeProvider = TimeProvider.DEFAULT;
	private ExecutionStatistics executionStatistics = new ExecutionStatistics(timeProvider);
	private long endNanos;
	Random random = new Random();
	private final String filledString;

	public SenderTestRunner(ClientOptions clientOptions, SocketSender socketSender) {
		this.clientOptions = clientOptions;
		this.socketSender = socketSender;
		long nanosToExecute = TimeUnit.SECONDS.toNanos( clientOptions.getExecutionSeconds() );
		endNanos = timeProvider.nanoTime() + nanosToExecute;
		StringBuilder sb = new StringBuilder(clientOptions.getMaximumMessageSize()); 
		while ( sb.length() < clientOptions.getMaximumMessageSize()){
			sb.append("abcdefghijklmnopqrstuvwxyz");
		}
		filledString = sb.toString();
	}

	public ExecutionStatistics call() throws Exception{
		try {
			doSend();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			socketSender.close();
		}
		return executionStatistics;
	}

	private void doSend() throws InterruptedException {
		String name = Thread.currentThread().getName();
		int count = 0;
		while ( endNanos > timeProvider.nanoTime()){
			String sendMessage = generateMessage();
			executionStatistics.recordSend(sendMessage);
			String receivedMessage = socketSender.send(sendMessage);
			executionStatistics.recordReceive(receivedMessage);
			String expectedResponse = generateExpectedResponse( sendMessage );
			// TODO parameterize the expectation 
			if (!expectedResponse.equals(receivedMessage)){
				throw new RuntimeException("Messages are different expected\n" 
						+ expectedResponse + "\nactual\n" + receivedMessage);
			}else{
				System.out.println("Thread" + name + ": Messages same " + ++count);
			}
			
			TimeUnit.MILLISECONDS.sleep(clientOptions.getMillisPause());
			
		}
	}
	private String generateMessage() {
		int range = clientOptions.getMaximumMessageSize() - clientOptions.getMinimumMessageSize() + 1;
		int size = random.nextInt(range) + clientOptions.getMinimumMessageSize();
		return filledString.substring(0, size);
	}

	private String generateExpectedResponse(String request) {
		return request;
	}
}
