package exceptions;

public class LandBereitsBenutztException extends Exception{
	public LandBereitsBenutztException(String land) {
		super("Das Land " + land +  " wurde bereits zum angreifen benutzt");
	}
}
