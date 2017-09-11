package valueobjects;

import java.util.ArrayList;
import java.util.List;

public class LaenderMission extends Mission{

	private int anzahlLaender = 0;
	private int anzahlEinheiten = 0;	
	protected ArrayList<Land> laender;
	
	public LaenderMission(int id, Spieler spieler, int anzahlLaender, int anzahlEinheiten, ArrayList<Land> laender) {
		super(id,"<html> Erobern Sie " + anzahlLaender + " Laender <br> und besetzen Sie jedes mit " + anzahlEinheiten + " Einheiten.</html>", spieler,"laender");
		this.anzahlLaender = anzahlLaender;
		this.anzahlEinheiten = anzahlEinheiten;
		this.laender = laender;
	}

	/**Gibt die Länder der Ländermission zurück.
	 * @return
	 */
	public List<Land> getLaender() {
		return laender;
	}

	/**Setzt die Länder für die Ländermission.
	 * @param laender
	 */
	public void setLaender(ArrayList<Land> laender) {
		this.laender = laender;
	}

	/**
	 * Überprüft ob der Spieler die Anzahl an Ländern besitzt, die er für die Ländermission erreichen muss.
	 */
	public boolean istAbgeschlossen() {
		int counter = 0;
		for(Land l : laender){
			if(l.getBesitzer().equals(spieler)){
				if (l.getEinheiten() >= anzahlEinheiten){
					counter++;
				}
			}
		}
		if(counter >= anzahlLaender){
			return true;
		}
		return false;
	}
}
