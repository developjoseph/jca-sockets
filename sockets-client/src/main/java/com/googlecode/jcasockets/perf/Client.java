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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.ParseException;

public class Client {
	public static void main(String[] args) throws Exception {
		Client conformanceClient = new Client(args);
		conformanceClient.execute();
		ExecutionStatistics executionStatistics = conformanceClient.getExecutionStatistics();
		printStatisticsAsCSV(executionStatistics );
	}

	private static void printStatisticsAsCSV(ExecutionStatistics executionStatistics) {
		System.out.printf("bytesSent,bytesReceived,milliseconds,messagesSent,messagesReceived,minimumMessageSize,maximumMessageSize\n"); 
		System.out.printf("%d,%d,%d,%d,%d,%d,%d\n", 
				executionStatistics.getBytesSent(), 
				executionStatistics.getBytesReceived(),
				executionStatistics.getElapsed(TimeUnit.MILLISECONDS),
				executionStatistics.getMessagesSent(),
				executionStatistics.getMessagesReceived(),
				executionStatistics.getMinimumMessageSize(),
				executionStatistics.getMaximumMessageSize()
				);
	}

	private SocketSenderFactory socketSenderFactory = new RemoteSocketSender();
	private ClientOptions clientCli;
	private ExecutionStatistics executionStatistics;

	public Client(String[] args) throws ParseException {
		clientCli = new ClientOptions();
		clientCli.parseArguments(args);
	}

	void setSender(SocketSenderFactory socketSenderFactory) {
		this.socketSenderFactory = socketSenderFactory;
	}

	public void execute() throws InterruptedException, ExecutionException {
		int numberOfThreads = clientCli.getNumberOfThreads();
		String ipAddress = clientCli.getIpAddress();
		List<Integer> ports = clientCli.getPorts();

		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
		List<SenderTestRunner> senderTestRunners = new ArrayList<SenderTestRunner>(numberOfThreads);
		for (Integer port : ports) {
			for (int i = 0; i < numberOfThreads; i++) {
				SocketSender socketSender = socketSenderFactory.createSocketSender(ipAddress, port);
				senderTestRunners.add(new SenderTestRunner(clientCli, socketSender));
			}
		}
		List<Future<ExecutionStatistics>> executionStatisticsFutures = executorService.invokeAll(senderTestRunners);
		executionStatistics = new ExecutionStatistics(null);
		for (Future<ExecutionStatistics> future : executionStatisticsFutures) {
			ExecutionStatistics that = future.get();
			executionStatistics.combine(that);
		}
		executorService.shutdown();
	}

	public ExecutionStatistics getExecutionStatistics() {
		return executionStatistics;
	}

}
