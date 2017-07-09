package local.domain.exceptions;

public class SpielerExistiertBereitsException extends Exception {

	public SpielerExistiertBereitsException() {
		super("Name ungültig oder bereits verwendet.");
	}
}
