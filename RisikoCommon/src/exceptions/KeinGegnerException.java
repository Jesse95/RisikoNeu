package exceptions;

import valueobjects.Land;

public class KeinGegnerException extends Exception{
	public KeinGegnerException(Land land) {
		super("Das Land " + land.getName() +  " geh\u00F6rt dir");
	}
}
