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
package com.googlecode.jcasockets.sample;

import java.io.LineNumberReader;
import java.io.PrintStream;
import java.util.logging.Logger;

import javax.ejb.EJBException;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;

import com.googlecode.jcasockets.SocketMessage;
import com.googlecode.jcasockets.SocketMessageEndpoint;

public class SocketMessageDrivenBean implements MessageDrivenBean, SocketMessageEndpoint {
	private static final long serialVersionUID = 1L;
	private final Logger log = Logger.getLogger(SocketMessageDrivenBean.class.getName());

	public SocketMessageDrivenBean() {
		log.info("Construct MDB");
	}

	public void ejbRemove() throws EJBException {
		log.info("ejbRemove");

	}

	public void ejbCreate() throws EJBException {
		log.info("EJBCreate" );
	}

	public void setMessageDrivenContext(MessageDrivenContext ctx) throws EJBException {
	}

	public void onMessage(SocketMessage socketMessage) throws Exception {
		LineNumberReader in = new LineNumberReader(socketMessage.getReader());
		final PrintStream socketOutput = new PrintStream(socketMessage.getOutputStream());
		String line;
		int size = 0;
		while ((line = in.readLine()) != null) {
			if ( "EXIT".equals( line )){
				socketMessage.getRawSocket().close();
				break;	
			}else{
				socketOutput.println(line);
				size += line.length();
				socketOutput.flush();
			}
		}
		log.info("Processed message size was: " + size );
	}
}
