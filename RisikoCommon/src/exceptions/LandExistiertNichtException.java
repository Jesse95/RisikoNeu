package exceptions;

public class LandExistiertNichtException extends Exception{
	public LandExistiertNichtException(String land) {
		super("Das Land " + land +  " existiert nicht");
	}
}
