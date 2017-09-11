package valueobjects;

import java.io.Serializable;

public abstract class GameEvent implements Serializable{
	private Spieler player;
	
	public GameEvent(Spieler player){
		super();
		this.player = player;
	}
	
	public Spieler getSpieler(){
		return player;
	}
}
