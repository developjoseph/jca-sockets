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

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class SenderTestRunner implements Callable<ExecutionStatistics> {
	private ExecutionStatistics executionStatistics;
	private ClientOptions clientOptions;
	private SocketSender socketSender;
	private TimeProvider timeProvider = TimeProvider.DEFAULT;
	private long endNanos;
	Random random = new Random();
	private String filledString;

	public SenderTestRunner(ClientOptions clientOptions,
			SocketSender socketSender) {
		this.clientOptions = clientOptions;
		this.socketSender = socketSender;
		long nanosToExecute = TimeUnit.SECONDS.toNanos( clientOptions.getExecutionSeconds() );
		endNanos = timeProvider.nanoTime() + nanosToExecute;
		char[] chars = new char[ clientOptions.getMaximumMessageSize() ]; 
		Arrays.fill(chars, 'X');
		filledString = new String(chars);
	}

	public ExecutionStatistics call() throws Exception {

		executionStatistics = new ExecutionStatistics(timeProvider);
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
			}
			
		}
		return executionStatistics;
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
