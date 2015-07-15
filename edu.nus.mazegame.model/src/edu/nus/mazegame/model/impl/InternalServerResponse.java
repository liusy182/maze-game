package edu.nus.mazegame.model.impl;

import java.io.Serializable;
import java.util.Map;

public class InternalServerResponse implements Serializable {
	public void setServerResponse(ServerResponse serverResponse) {
		this.serverResponse = serverResponse;
	}

	/**
	 * Generated Serialized Id
	 */
	private static final long serialVersionUID = 4076668085447479722L;
	
	private ServerResponse serverResponse;
	private IpAddressInfo mainServerAddress;
	private IpAddressInfo backupServerAddress;
	private Map<String,IpAddressInfo> playerAddressMap;
	private boolean toBeBackup;
	
	public ServerResponse getServerResponse(){
		return serverResponse;
	}
	
	public void setMainServerAddress(IpAddressInfo mainServerAddress){
		this.mainServerAddress = mainServerAddress;
	}
	
	public IpAddressInfo getMainServerAddress(){
		return mainServerAddress;
	}
	
	public void setBackupServerAddress(IpAddressInfo backupServerAddress){
		this.backupServerAddress = backupServerAddress;
	}
	
	public IpAddressInfo getBackupServerAddress(){
		return backupServerAddress;
	}
	
	public void setToBeBackup(boolean toBeBackup){
		this.toBeBackup = toBeBackup;
	}
	
	public boolean isToBeBackup(){
		return toBeBackup;
	}

	/**
	 * @return the playerAddressMap
	 */
	public Map<String,IpAddressInfo> getPlayerAddressSet() {
		return playerAddressMap;
	}

	/**
	 * @param playerAddressMap the playerAddressMap to set
	 */
	public void setPlayerAddressSet(Map<String,IpAddressInfo> playerAddressMap) {
		this.playerAddressMap = playerAddressMap;
	}
	
	
	public String toString(){
		StringBuilder sb = new StringBuilder("InternalServerResponse:{\n");
		sb.append("mainServer[").append(mainServerAddress != null ? mainServerAddress.toString() : null).append("]\n");
		sb.append("backupServer[").append(backupServerAddress != null ? backupServerAddress.toString() : null).append("]\n");
		sb.append("playerAddressMap:\n[");
		if(playerAddressMap == null){
			sb.append("null");
		}else{
			for(IpAddressInfo address: playerAddressMap.values()){
				sb.append(address).append("]\n");
			}
		}
		sb.append("]}");
		return sb.toString();
	}
}
