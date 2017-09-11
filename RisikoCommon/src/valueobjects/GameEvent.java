package valueobjects;

import java.io.Serializable;

public abstract class GameEvent implements Serializable{
	private Spieler player;
	
	public GameEvent(Spieler player){
		super();
		this.player = player;
	}
	
	/**Gibt den Spieler zurück.
	 * @return
	 */
	public Spieler getSpieler(){
		return player;
	}
}
