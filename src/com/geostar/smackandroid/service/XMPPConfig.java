package com.geostar.smackandroid.service;

public class XMPPConfig {
	
	private String serverIp;
	private String serviceName;
	private int port;
	
	private String resourceName;

	public XMPPConfig(String serverIp, String serviceName, int port,
			String resourceName) {
		super();
		this.serverIp = serverIp;
		this.serviceName = serviceName;
		this.port = port;
		this.resourceName = resourceName;
	}

	public String getServerIp() {
		return serverIp;
	}

	public String getServiceName() {
		return serviceName;
	}

	public int getPort() {
		return port;
	}

	public String getResourceName() {
		return resourceName;
	}
	
}
