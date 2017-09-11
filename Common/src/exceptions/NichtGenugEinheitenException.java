package local.domain.exceptions;

public class NichtGenugEinheitenException extends Exception{
	public NichtGenugEinheitenException() {
		super("Du hast leider nur eine Einheit und kannst diese AKtion daher nicht ausführen.");
	}
}