package local.domain.exceptions;

public class NichtGenugEinheitenException extends Exception{
	public NichtGenugEinheitenException(int einheiten) {
		super("Mit " + einheiten + " Eiheiten, kannst du diese Aktion nicht ausführen.");
	}
}