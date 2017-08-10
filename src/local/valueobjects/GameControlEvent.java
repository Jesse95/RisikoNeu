package local.valueobjects;

public class GameControlEvent extends GameEvent{
	private phasen phase;
	
	public enum phasen {VERTEILEN, VERSCHIEBEN, STARTPHASE, ANGRIFF, STARTEN, AKTUALISIEREN};
	public GameControlEvent(Spieler spieler, phasen phase){
		super(spieler);
		
		this.phase = phase;
	}
	
	public phasen getTurn(){
		return phase;
	}
}
