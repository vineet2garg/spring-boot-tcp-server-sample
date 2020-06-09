package com.zhwxp.sample.spring.boot.tcp.server.service.impl;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.ip.IpHeaders;
import org.springframework.integration.ip.tcp.connection.TcpConnection;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;

import com.zhwxp.sample.spring.boot.tcp.server.config.TcpConnectionCacheConfig;
import com.zhwxp.sample.spring.boot.tcp.server.service.MessageService;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

	@Autowired
	private DynamoDbAsyncClient dbAsyncClient;

	@Autowired
	private TcpConnectionCacheConfig cacheConfig;

	@Autowired
	private MessageChannel outboundChannel;

	@Override
	public byte[] processMessage(byte[] message, MessageHeaders messageHeaders) {
		// Logging Message Header
		log.info("Message Header : {}", messageHeaders);

		String messageContent = new String(message);
		log.info("Receive message: {}", messageContent);
		try {
			consumeAndPublishMessage(messageContent);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String responseContent = String.format("Message \"%s\" is processed", messageContent);
		return responseContent.getBytes();
	}

	/*
	 * truck1,358503060656593,20200602024223,151.139083,-33.79516,20,15,70,0,310,129
	 * ,0,0.000,0.000,20200602024222,342145,0000,00000000,00,00,00,00,00,00,00,00,00
	 * ,00,00
	 */
	private void consumeAndPublishMessage(String request) throws InterruptedException, ExecutionException {
		log.info("Message recevied : {}", request);
		Map<String, AttributeValue> itemValues = new HashMap<String, AttributeValue>();

		String[] data = request.split(",");
		if (data.length >= 3) {
			String payload = String.join(",", Arrays.copyOfRange(data, 2, data.length - 1));

			itemValues.put("truck_number", AttributeValue.builder().s(data[0]).build());
			itemValues.put("truck_device_ip", AttributeValue.builder().s(data[1]).build());
			itemValues.put("payload", AttributeValue.builder().s(payload).build());
			itemValues.put("ttl",
					AttributeValue.builder()
							.n("" + (Instant.now().plusSeconds(24 * 60 * 60).atOffset(ZoneOffset.UTC).toEpochSecond()))
							.build());

			CompletableFuture<PutItemResponse> putItemAsyncResponse = dbAsyncClient
					.putItem(PutItemRequest.builder().tableName("bt-truck-payload-poc-table").item(itemValues).build());

			log.info("Item Added : {}", putItemAsyncResponse.get());
		} else {
			log.info("Incorrect message : {}", request);
		}

	}

	@Override
	public void sendMessageToClient(String clientId, String message) {
		TcpConnection tcpConnection = cacheConfig.getTcpConnection(clientId);
		outboundChannel.send(MessageBuilder.withPayload(message)
				.setHeader(IpHeaders.CONNECTION_ID, tcpConnection.getConnectionId()).build());

	}

	@Override
	public String listAllConnections() {
		final StringBuilder connections = new StringBuilder();
		cacheConfig.getAllConnections().forEach(
				(k, v) -> connections.append("Connection : Id").append(k).append("Details: ").append(v).append("\n"));

		return connections.toString();
	}

}
