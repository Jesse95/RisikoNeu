package valueobjects;

import java.io.Serializable;
import java.util.List;

public class SpielerMission extends Mission implements Serializable{

	List<Spieler> spielerliste;

	public SpielerMission(int id, Spieler spieler,Spieler spieler2,List<Spieler> spielerliste) {
		super(id, "<html>Erobern Sie alle Länder von<br>" + spieler2.getName() +"</html>", spieler,"spieler"); // +  spieler2,spieler);
		this.spielerliste = spielerliste;
	}
	
	/**
	 * Setzt die Beschreibung der Mission neu.
	 */
	public void resetBeschreibung(){
		beschreibung = "<html>Erobern Sie alle Länder von<br> " + spieler2.getName() +"</html>";
	}

	/**
	 * Überprüft ob der Spieler eliminiert wurde.
	 */
	public boolean istAbgeschlossen() {
		if(spielerliste.contains(spieler2)){
			return false;
		}
		return true;
	}
}
