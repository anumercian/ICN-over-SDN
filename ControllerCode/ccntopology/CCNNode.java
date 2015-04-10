package net.floodlightcontroller.ccntopology;

public class CCNNode {
	private String ip;
	private String mac;
	private short port;
	public CCNNode() {
		
	}
	
	public CCNNode(String src_ip, String src_mac, short src_port) {
		// TODO Auto-generated constructor stub
		this.ip = src_ip;
		this.mac = src_mac;
		this.port = src_port;
	}

	/*public void CCNNode(String ip, String mac, short port) {
		this.ip = ip;
		this.mac = mac;
		this.port = port;
	}*/
	
	public String getIP() {
		return this.ip;
	}
	
	public String getMAC() {
		return this.mac;
	}
	
	public short getPort() {
		return this.port;
	}

}
