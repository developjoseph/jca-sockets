package com.googlecode.jcasockets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.resource.spi.UnavailableException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkEvent;
import javax.resource.spi.work.WorkListener;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class EndpointPool implements WorkListener {

	private final Log log = LogFactory.getLog(EndpointPool.class);
	private MessageEndpointFactory endpointFactory;
	private int poolSize;
	private final int maxEndpoints;
	private final BlockingQueue<SocketMessageEndpoint> endpoints;
	private final long timeout;
	private final TimeUnit timeUnit;

	public EndpointPool(MessageEndpointFactory endpointFactory, int maxEndpoints, long timeout, TimeUnit timeUnit) {
		this.endpointFactory = endpointFactory;
		this.maxEndpoints = maxEndpoints;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
		endpoints = new ArrayBlockingQueue<SocketMessageEndpoint>(maxEndpoints);
		log.info("Socket endpoints configured, maxEndPoints = " + maxEndpoints + ", timeout=" + timeout + "(" + timeUnit.toString()+")");
	}

	private SocketMessageEndpoint addIfUnderMaximumPoolSize() throws UnavailableException {
		SocketMessageEndpoint messageEndpoint = null;
		if (poolSize < maxEndpoints) {
			messageEndpoint = (SocketMessageEndpoint) endpointFactory.createEndpoint(null);
			poolSize++;
			log.info("Allocated endpoint, poolsize " + poolSize + " of " + maxEndpoints);
		}
		return messageEndpoint;
	}

	public SocketMessageEndpoint getEndpoint() throws UnavailableException {
		SocketMessageEndpoint messageEndpoint = endpoints.poll();
		if (messageEndpoint == null) {
			messageEndpoint = addIfUnderMaximumPoolSize();
		}
		if (messageEndpoint == null) {
			try {
				messageEndpoint = endpoints.poll(timeout, timeUnit);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
        if ( messageEndpoint == null ){
            throw new UnavailableException( "No more endpoints available, maxEnpoints=" + maxEndpoints);
        }
		return messageEndpoint;
	}

	public void release(SocketMessageEndpoint socketMessageEndpoint) {
		endpoints.offer(socketMessageEndpoint);
	}

	public void workAccepted(WorkEvent workEvent) {
	}

	public void workCompleted(WorkEvent workEvent) {
		Work work = workEvent.getWork();
		SocketProcessor socketProcessor = (SocketProcessor) work;
		SocketMessageEndpoint messageEndpoint = socketProcessor.getMessageEndpoint();
		release(messageEndpoint);
	}

	public void workRejected(WorkEvent workEvent) {
	}

	public void workStarted(WorkEvent workEvent) {
	}

}
