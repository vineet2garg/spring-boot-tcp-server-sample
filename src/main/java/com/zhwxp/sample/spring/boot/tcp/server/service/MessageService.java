package com.zhwxp.sample.spring.boot.tcp.server.service;

import org.springframework.messaging.MessageHeaders;

public interface MessageService {

	public byte[] processMessage(byte[] message, MessageHeaders messageHeaders);

	public void sendMessageToClient(String clientId, String message);

	public String listAllConnections();

}
