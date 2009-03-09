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

import com.googlecode.jcasockets.perf.SocketSender;
import com.googlecode.jcasockets.perf.SocketSenderFactory;


public class MockSocketSender implements SocketSenderFactory, SocketSender {

	@Override
	public String send(String send) {
		return send;
	}

	@Override
	public SocketSender createSocketSender(String ipAddress, Integer port) {
		return new MockSocketSender();
	}
}
