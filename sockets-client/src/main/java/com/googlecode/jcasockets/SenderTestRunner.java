package com.googlecode.jcasockets;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class SenderTestRunner implements Callable<ExecutionStatistics> {
	private ExecutionStatistics executionStatistics;;
	private ConformanceClientCli conformanceClientCli;
	private SocketSender socketSender;
	private TimeProvider timeProvider = TimeProvider.DEFAULT;
	private long endNanos;
	Random random = new Random();
	private String filledString;

	public SenderTestRunner(ConformanceClientCli conformanceClientCli,
			SocketSender socketSender) {
		this.conformanceClientCli = conformanceClientCli;
		this.socketSender = socketSender;
		long nanosToExecute = TimeUnit.SECONDS.toNanos( conformanceClientCli.getExecutionSeconds() );
		endNanos = timeProvider.nanoTime() + nanosToExecute;
		char[] chars = new char[ conformanceClientCli.getMaximumMessageSize() ]; 
		Arrays.fill(chars, 'X');
		filledString = new String(chars);
	}

	@Override
	public ExecutionStatistics call() throws Exception {

		executionStatistics = new ExecutionStatistics();
		while ( endNanos > System.nanoTime()){
			String sendMessage = generateMessage();
			executionStatistics.recordSend(sendMessage);
			String receivedMessage = socketSender.send(sendMessage);
			executionStatistics.recordReceive(receivedMessage);
			
		}
		return executionStatistics;
	}
	private String generateMessage() {
		int range = conformanceClientCli.getMaximumMessageSize() - conformanceClientCli.getMinimumMessageSize() + 1;
		int size = random.nextInt(range) + conformanceClientCli.getMinimumMessageSize();
		return filledString.substring(0, size);
	}

}
