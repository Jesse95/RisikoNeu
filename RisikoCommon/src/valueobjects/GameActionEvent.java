package valueobjects;

public class GameActionEvent extends GameEvent{
	private GameActionEventType type;
	private String text;
	
	public enum GameActionEventType{VERTEILEN,ANGRIFF,EROBERT};
	
	public GameActionEvent(String text,Spieler spieler, GameActionEventType type){
		super(spieler);
		this.type = type;
		this.text = text;
	}
	
	/**Gibt die Art des GameActionEvents zurück.
	 * @return
	 */
	public GameActionEventType getType(){
		return type;
	}
	
	/**Gibt den Text zurück.
	 * @return
	 */
	public String getText(){
		return text;
	}
}
