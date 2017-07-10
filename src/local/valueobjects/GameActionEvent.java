package local.valueobjects;

public class GameActionEvent extends GameEvent{
	private GameActionEventType type;
	private String text;
	public enum GameActionEventType{VERTEILEN,ANGRIFF,EROBERT};
	public GameActionEvent(String text,Spieler spieler, GameActionEventType type){
		super(spieler);
		this.type = type;
		this.text = text;
	}
	
	public GameActionEventType getType(){
		return type;
	}
	public String getText(){
		return text;
	}
}
