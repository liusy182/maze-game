package edu.nus.mazegame.model.impl;

import java.io.Serializable;

public class IpAddressInfo implements Serializable{
	/**
	 * Generated Serialized Id
	 */
	private static final long serialVersionUID = 5748496879667518944L;

	public IpAddressInfo(String ip, int port){
		this.setIp(ip);
		this.setPort(port);
	}
	
	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}
	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the Port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param Port the Port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	private String ip;
	private int port;

	public String toString(){
		return ip + ":" + port;
	}
	
	@Override
	public boolean equals(Object object){
		if(object == null || object instanceof IpAddressInfo == false){
			return false;
		}
		IpAddressInfo ipAddressInfo = (IpAddressInfo)object;
		if(ip.equals(ipAddressInfo.getIp()) && port == ipAddressInfo.getPort()){
			return true;
		}else{
			return false;
		}
	}
	
	@Override 
	public int hashCode(){
		return this.getPort()*17 + this.getIp().hashCode();
	}
	
}
