package local.domain.exceptions;

public class KeinNachbarlandException extends Exception {
	public KeinNachbarlandException(String land) {
		super("Das Land " + land +  " ist kein Nachbarland");
	}
}
