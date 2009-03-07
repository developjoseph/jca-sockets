package com.googlecode.jcasockets;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.cli.ParseException;

public class ConformanceClient {
	public static void main(String[] args) throws Exception {
		ConformanceClient conformanceClient = new ConformanceClient(args);
		conformanceClient.execute();
		ExecutionStatistics executionStatistics = conformanceClient.getExecutionStatistics();
		System.out.println(executionStatistics.toString());
	}

	private SocketSenderFactory socketSenderFactory = new RemoteSocketSender();
	private ConformanceClientCli clientCli;
	private ExecutionStatistics executionStatistics;

	public ConformanceClient(String[] args) throws ParseException {
		clientCli = new ConformanceClientCli();
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
		executionStatistics = new ExecutionStatistics();
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
