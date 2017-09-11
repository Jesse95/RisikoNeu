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

	public List<Land> getLaender() {
		return laender;
	}

	public void setLaender(ArrayList<Land> laender) {
		this.laender = laender;
	}

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
