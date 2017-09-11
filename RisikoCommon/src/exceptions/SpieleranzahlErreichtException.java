package exceptions;

public class SpieleranzahlErreichtException extends Exception {

	public SpieleranzahlErreichtException() {
		super("Die maximale Spieleranzahl für dieses Spiel ist bereits erreicht.");
	}
}
