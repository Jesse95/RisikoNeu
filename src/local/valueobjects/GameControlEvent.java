package local.valueobjects;

public class GameControlEvent extends GameEvent{
	private phasen phase;
	
	public enum phasen {VERTEILEN, VERSCHIEBEN, ANGRIFF, STARTPHASE, AKTUALISIEREN, BEENDEN, GEWONNEN};
	public GameControlEvent(Spieler spieler, phasen phase){
		super(spieler);
		
		this.phase = phase;
	}
	
	public phasen getTurn(){
		return phase;
	}
}
