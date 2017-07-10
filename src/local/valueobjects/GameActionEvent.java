package local.valueobjects;

public class GameActionEvent extends GameEvent{
	private GameActionEventType type;
	public enum GameActionEventType{VERTEILEN,ANGRIFF};
	public GameActionEvent(Spieler spieler, GameActionEventType type){
		super(spieler);
		this.type = type;
	}
	
	public GameActionEventType getType(){
		return type;
	}
}
