package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import local.domain.Spielfeld;

public class anfragenVerwaltung {
	private Spielfeld sp;
	private Socket cSocket;
	private BufferedReader in;
	private PrintStream out;
	public anfragenVerwaltung(Socket socket){
		System.out.println("Server gestartet");
		sp = new Spielfeld();
		cSocket = socket;
		try{
			in = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
			out = new PrintStream(cSocket.getOutputStream());
		} catch(IOException e){
			try{
				cSocket.close();
			} catch (IOException e2){
				
			}
		}
	}
	public static void main(String[] args){
		
	}
	public void anfragenVerarbeitung(){
		String input = "";
		out.println("Server bereit");
		
		do{
			try{
				input = in.readLine();
			} catch (IOException e){
				
			}
			if(input.equals("Phase")){
				out.println("Die aktuelle Phase ist " + sp.getTurn());
			}
		}while(!input.equals("Beenden"));
		try{
			cSocket.close();
		}catch(IOException e2){
			
		}
	}
}
