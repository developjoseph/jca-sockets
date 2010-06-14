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

import org.apache.commons.cli.ParseException;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Client {
	public static void main(String[] args) throws Exception {
		Client client = new Client(args);
		if (client.clientOptions.isHelpRequested()) {
			client.clientOptions.printHelp(System.err);
			return;
		}

		client.execute();
		ExecutionStatistics executionStatistics = client.getExecutionStatistics();
		printStatisticsHeaderAsCSV(System.out);
		printStatisticsAsCSV(System.out, executionStatistics);
	}

	static void printStatisticsHeaderAsCSV(PrintStream printStream) {
		printStream
				.printf("bytesSent,bytesReceived,milliseconds,messagesSent,messagesReceived,minimumMessageSize,maximumMessageSize,bytesSentPerSecond,bytesReceivedPerSecond,messagesPerSecond\n");
	}

	static void printStatisticsAsCSV(PrintStream printStream, ExecutionStatistics executionStatistics) {
		printStream.printf("%d,%d,%d,%d,%d,%d,%d,%d,%d,%d\n", executionStatistics.getBytesSent(), executionStatistics
				.getBytesReceived(), executionStatistics.getElapsed(TimeUnit.MILLISECONDS), executionStatistics
				.getMessagesSent(), executionStatistics.getMessagesReceived(), executionStatistics
				.getMinimumMessageSize(), executionStatistics.getMaximumMessageSize(), executionStatistics
				.getBytesSentPerSecond(), executionStatistics.getBytesReceivedPerSecond(), executionStatistics
				.getMessagesPerSecond());
	}

	private SocketSenderFactory socketSenderFactory = new RemoteSocketSenderConnectionPerRequest();
//	private SocketSenderFactory socketSenderFactory = new RemoteSocketSenderKeepConnection();
	private ClientOptions clientOptions;
	private ExecutionStatistics executionStatistics;

	public Client(String[] args) throws ParseException {
		clientOptions = new ClientOptions();
		clientOptions.parseArguments(args);
	}

	void setSender(SocketSenderFactory socketSenderFactory) {
		this.socketSenderFactory = socketSenderFactory;
	}

	public void execute() throws InterruptedException, ExecutionException {
		int numberOfThreads = clientOptions.getNumberOfThreads();
		String ipAddress = clientOptions.getIpAddress();
		List<Integer> ports = clientOptions.getPorts();
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
		try {
			Collection<Callable<ExecutionStatistics>> senderTestRunners = new ArrayList<Callable<ExecutionStatistics>>(numberOfThreads);
			for (Integer port : ports) {
				for (int i = 0; i < numberOfThreads; i++) {
					SocketSender socketSender = socketSenderFactory.createSocketSender(ipAddress, port);
					senderTestRunners.add(new SenderTestRunner(clientOptions, socketSender));
				}
			}
			List<Future<ExecutionStatistics>> executionStatisticsFutures = executorService.invokeAll(senderTestRunners);
			executionStatistics = new ExecutionStatistics(null);
			for (Future<ExecutionStatistics> future : executionStatisticsFutures) {
				ExecutionStatistics that = future.get();
				executionStatistics.combine(that);
			}
		} finally {
			executorService.shutdown();
		}
	}

	public ExecutionStatistics getExecutionStatistics() {
		return executionStatistics;
	}

}
