package exceptions;

public class KannLandNichtBenutzenException extends Exception{
	public KannLandNichtBenutzenException() {
		super("Dieses Land gehört dir nicht. Bitte wähle ein anderes Land aus");
	}
}
