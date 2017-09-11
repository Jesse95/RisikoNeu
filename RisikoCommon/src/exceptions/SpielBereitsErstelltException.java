package exceptions;

public class SpielBereitsErstelltException extends Exception {

	public SpielBereitsErstelltException() {
		super("Es wurde bereits ein Spiel erstellt.");
	}
}
