package local.domain.exceptions;

public class SpielerGibtEsNichtException extends Exception {

	public SpielerGibtEsNichtException() {
		super("Du musst einen bereits existierenden Spieler eingeben!");
	}
}
