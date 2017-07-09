package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class risikoServer {
	protected int port;
	protected ServerSocket sSocket;
	public risikoServer(int port){
		this.port = port;
		try{
			sSocket = new ServerSocket(port);
		}catch (IOException e){
			
		}
		verbindungsAufbau();
	}
	public static void main(String[] args) {
		
		
	}
	
	public void verbindungsAufbau(){
		try{
			while(true){
				Socket cSocket = sSocket.accept();
				anfragenVerwaltung av = new anfragenVerwaltung(cSocket);
				av.anfragenVerarbeitung();
			}
		}catch (IOException e){
			
		}
	}

}
