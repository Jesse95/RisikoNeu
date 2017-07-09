package local.valueobjects;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface GameEventListener extends Remote{
	public void handleGameEvent(GameEvent event) throws RemoteException;
}
