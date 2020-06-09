package com.zhwxp.sample.spring.boot.tcp.server.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;

import com.zhwxp.sample.spring.boot.tcp.server.service.MessageService;

@MessageEndpoint
public class TcpServerEndpoint {

	private MessageService messageService;

	@Autowired
	public TcpServerEndpoint(MessageService messageService) {
		this.messageService = messageService;
	}

	@ServiceActivator(inputChannel = "inboundChannel", outputChannel = "outboundChannel")
	public byte[] process(byte[] message, @Headers MessageHeaders messageHeaders) {
		return messageService.processMessage(message, messageHeaders);
	}

}
