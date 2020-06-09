package com.zhwxp.sample.spring.boot.tcp.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zhwxp.sample.spring.boot.tcp.server.service.MessageService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/tcp/server")
public class TcpServerController {

	@Autowired
	private MessageService messageService;

	@RequestMapping(value = "/send", method = RequestMethod.GET)
	public void sendMessageToClient(@RequestParam String clientId, @RequestParam String message) {
		log.info("Send Message {}, to Client {} ", message, clientId);
		messageService.sendMessageToClient(clientId, message);
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ResponseEntity<String> listAllConnections() {
		log.info("Listing all Connections");
		return ResponseEntity.ok(messageService.listAllConnections());
	}
}
