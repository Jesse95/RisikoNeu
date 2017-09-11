package valueobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class KontinentenMission extends Mission{

	private ArrayList<Kontinent> kontinente;
	
	public KontinentenMission(int id, Spieler spieler, ArrayList<Kontinent> kontinente) {
		super(id, "<html>Erobern Sie die Kontinente : <br><center> " +  kontinente.get(0).getName() + "<br>" + kontinente.get(1).getName() +"</center></html>",spieler,"kontinent");
		this.kontinente = kontinente;
	}

	public boolean istAbgeschlossen() {
		List<Land> laender = new Vector<>();
		
		for(Kontinent k : kontinente){
			laender.addAll(k.getLaender());
		}
		
		for(Land l : laender){
			if(! l.getBesitzer().equals(spieler)){
				return false;
			}
		}
		return true;
	}
}
