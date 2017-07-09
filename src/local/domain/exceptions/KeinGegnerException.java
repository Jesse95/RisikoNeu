package local.domain.exceptions;

public class KeinGegnerException extends Exception{
	public KeinGegnerException(String land) {
		super("Das Land " + land +  " geh\u00F6rt dir");
	}
}
