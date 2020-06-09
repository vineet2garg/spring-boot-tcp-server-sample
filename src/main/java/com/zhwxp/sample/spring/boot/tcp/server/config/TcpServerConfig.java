package com.zhwxp.sample.spring.boot.tcp.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
import org.springframework.integration.ip.tcp.TcpSendingMessageHandler;
import org.springframework.integration.ip.tcp.connection.TcpConnection;
import org.springframework.integration.ip.tcp.connection.TcpConnectionCloseEvent;
import org.springframework.integration.ip.tcp.connection.TcpConnectionEvent;
import org.springframework.integration.ip.tcp.connection.TcpConnectionOpenEvent;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableIntegration
@IntegrationComponentScan
@Slf4j
public class TcpServerConfig {

	@Value("${spring.tcp.server.port}")
	private int tcpPort;

	@Autowired
	private TcpConnectionCacheConfig cacheConfig;

	@Bean
	public MessageChannel inboundChannel() {
		return new DirectChannel();
	}

	@Bean
	public MessageChannel outboundChannel() {
		return new DirectChannel();
	}

	@Bean
	public TcpReceivingChannelAdapter server(TcpNetServerConnectionFactory cf) {
		TcpReceivingChannelAdapter adapter = new TcpReceivingChannelAdapter();
		adapter.setConnectionFactory(cf);
		adapter.setOutputChannel(inboundChannel());
		return adapter;
	}

	@Bean
	public TcpNetServerConnectionFactory cf() {
		return new TcpNetServerConnectionFactory(tcpPort);
	}

	@Bean
	public MessageHandler sender() {
		TcpSendingMessageHandler tcpSendingMessageHandler = new TcpSendingMessageHandler();
		tcpSendingMessageHandler.setConnectionFactory(cf());
		return tcpSendingMessageHandler;
	}

	@Bean
	public IntegrationFlow outbound() {
		return IntegrationFlows.from(outboundChannel()).handle(sender()).get();
	}

	@EventListener
	public void tcpConnectionEvent(TcpConnectionEvent event) {
		log.info("TCP Connection Details : Connection ID: {}, Connection Source: {}", event.getConnectionId(),
				event.getSource(), event.getConnectionFactoryName());
	}

	@EventListener
	public void tcpConnectionOpenEvent(TcpConnectionOpenEvent event) {
		log.info("TCP Connection Open Details : {}", event);
		cacheConfig.addConnection(event.getConnectionId(), (TcpConnection) event.getSource());
	}

	@EventListener
	public void tcpConnectionCloseEvent(TcpConnectionCloseEvent event) {
		log.info("TCP Connection Close Details : {}", event);
		cacheConfig.removeConnection(event.getConnectionId());
	}
}
