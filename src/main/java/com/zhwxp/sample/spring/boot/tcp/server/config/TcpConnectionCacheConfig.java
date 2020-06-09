package com.zhwxp.sample.spring.boot.tcp.server.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Configuration;
import org.springframework.integration.ip.tcp.connection.TcpConnection;

@Configuration
public class TcpConnectionCacheConfig {

	private final Map<String, TcpConnection> tcpConnections = new HashMap<>();

	public boolean addConnection(String connectionId, TcpConnection tcpConnection) {
		tcpConnections.put(connectionId, tcpConnection);
		return true;
	}

	public boolean removeConnection(String connectionId) {
		tcpConnections.remove(connectionId);
		return true;
	}

	public TcpConnection getTcpConnection(String connectionId) {
		return tcpConnections.get(connectionId);
	}

	public Map<String, TcpConnection> getAllConnections() {
		return tcpConnections;
	}
}
