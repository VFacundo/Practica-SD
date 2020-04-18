package ejer3;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IControl extends Remote{
	void detenerSv() throws RemoteException;
}
