package local.domain.exceptions;

public class KannEinheitenNichtVerschiebenException extends Exception{

	public KannEinheitenNichtVerschiebenException(boolean zuViele) {
		super("Du kannst nicht " + (zuViele? "so viele " : " so wenige " + "Einheiten verschieben"));
	}
}
