package edu.nus.mazegame.client.util;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import edu.nus.mazegame.model.impl.IpAddressInfo;
import edu.nus.mazegame.model.interf.RMIConstant;
import edu.nus.mazegame.model.rmi.InternalServerRemote;

public class ClientUtil {
	private static final Logger logger = Logger.getLogger(ClientUtil.class.getName());
	
	public static Image getImage(String path){
		Image image = null;
		try {
			InputStream is = ClientUtil.class.getClass().getResourceAsStream(path);
			image = ImageIO.read(is);
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new RuntimeException(e);
		}
		return image;
	}
	
	/*
	 * retrieves the InternalServerRemote RMI object with given address
	 * */
	public static InternalServerRemote getInternalServerRemote(IpAddressInfo address) throws RemoteException, NotBoundException{
		InternalServerRemote remoteStub = null;
		if(address == null){
			return null;
		}
		Registry registry = LocateRegistry.getRegistry(address.getIp(),
				address.getPort());
		Object object = registry.lookup(RMIConstant.RMI_ID);
		remoteStub = (InternalServerRemote)object ;
		return remoteStub;
	}
	


}
