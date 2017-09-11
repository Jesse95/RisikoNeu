package exceptions;

public class ServerBereitsGestartetException extends Exception{

	public ServerBereitsGestartetException() {
		super("Server ist bereits gestartet.");
	}
}
