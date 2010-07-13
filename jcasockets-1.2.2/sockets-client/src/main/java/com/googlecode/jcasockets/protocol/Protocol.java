package com.googlecode.jcasockets.protocol;


public interface Protocol {
	public interface Handler{
		void handleMessage();
	}

	boolean clientSendsToken();

	boolean isEndOfConversation();

}
