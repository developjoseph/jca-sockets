package com.googlecode.jcasockets;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.resource.spi.UnavailableException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkEvent;
import javax.resource.spi.work.WorkListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EndpointPool implements WorkListener {

	private final Log log = LogFactory.getLog(EndpointPool.class);
	private MessageEndpointFactory endpointFactory;
	private int poolSize;
	private final int maxEndpoints;
	private final BlockingQueue<SocketMessageEndpoint> endpoints;
	private final Set<SocketMessageEndpoint> allocatedEndpoints;
	private final long timeout;
	private final TimeUnit timeUnit;
    /**
     * Lock held on updates to poolSize, corePoolSize,
     * maximumPoolSize, runState, and workers set.
     */
    private final ReentrantLock mainLock = new ReentrantLock();

	public EndpointPool(MessageEndpointFactory endpointFactory, int maxEndpoints, long timeout, TimeUnit timeUnit) {
		this.endpointFactory = endpointFactory;
		this.maxEndpoints = maxEndpoints;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
		allocatedEndpoints = new HashSet<SocketMessageEndpoint>();
		endpoints = new ArrayBlockingQueue<SocketMessageEndpoint>(maxEndpoints);
	}

    private SocketMessageEndpoint addIfUnderMaximumPoolSize() throws UnavailableException {
    	SocketMessageEndpoint messageEndpoint = null;
    	final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
			if (poolSize < maxEndpoints ){
				messageEndpoint =(SocketMessageEndpoint) endpointFactory.createEndpoint(null);
				poolSize++;
				log.info("Allocated endpoint, poolsize " + poolSize + " of " + maxEndpoints);
			}
        } finally {
            mainLock.unlock();
        }
        return messageEndpoint;
    }
    private void allocateEndPoint( SocketMessageEndpoint messageEndpoint) throws UnavailableException {
    	final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
			allocatedEndpoints.add(messageEndpoint);
        } finally {
            mainLock.unlock();
        }
    }
	
	public SocketMessageEndpoint getEndpoint() throws UnavailableException {
    	SocketMessageEndpoint messageEndpoint = endpoints.poll();
    	if (messageEndpoint == null ){
    		messageEndpoint = addIfUnderMaximumPoolSize();
    	}
		if ( messageEndpoint == null ){
			try {
				messageEndpoint = endpoints.poll( timeout, timeUnit );
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		if  (messageEndpoint != null  ){
			allocateEndPoint(messageEndpoint);
		}else {
			throw new UnavailableException("Could not allocate endpoint");
		}
		return messageEndpoint;
	}

	public void release(SocketMessageEndpoint socketMessageEndpoint) {
		boolean wasRemoved = allocatedEndpoints.remove(socketMessageEndpoint);
		
		if (!wasRemoved) {
			throw new IllegalStateException("Endpoint specified was not allocated.");
		}
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
