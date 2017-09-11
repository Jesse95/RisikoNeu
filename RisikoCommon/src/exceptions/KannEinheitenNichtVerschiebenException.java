package exceptions;

public class KannEinheitenNichtVerschiebenException extends Exception{

	public KannEinheitenNichtVerschiebenException() {
		super("Du kannst diese Anzahl an Einheiten nicht verschieben");
	}
}
