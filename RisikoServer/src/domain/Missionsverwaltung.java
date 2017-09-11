package domain;

import java.io.IOException;
import java.util.ArrayList;

import valueobjects.Kontinent;
import valueobjects.KontinentenMission;
import valueobjects.LaenderMission;
import valueobjects.Land;
import valueobjects.Mission;
import valueobjects.Spieler;
import valueobjects.SpielerMission;

public class Missionsverwaltung{
	public ArrayList<Mission> missionsListe = new ArrayList<Mission>();
	
	/**
	 * Erstellt die Missionsliste
	 * @param spielerListe 
	 * @param kontinentenListe 
	 * @param laenderListe 
	 * @throws IOException 
	 */
	public void missionsListeErstellen(ArrayList<Land> laenderListe, ArrayList<Kontinent> kontinentenListe, ArrayList<Spieler> spielerListe) {
		Spieler platzhalterSpieler = new Spieler("Platzhalter");
		//Befreien Sie Nordamerika und Afrika
		ArrayList<Kontinent> mission1Kontinente = new ArrayList<Kontinent>();
		mission1Kontinente.add(kontinentenListe.get(3));
		mission1Kontinente.add(kontinentenListe.get(5));
		missionsListe.add(new KontinentenMission(1,platzhalterSpieler,mission1Kontinente));
		//Befreien Sie Nordamerika und Australien
		ArrayList<Kontinent> mission2Kontinente = new ArrayList<Kontinent>();
		mission2Kontinente.add(kontinentenListe.get(2));
		mission2Kontinente.add(kontinentenListe.get(5));
		missionsListe.add(new KontinentenMission(2,platzhalterSpieler,mission2Kontinente));
		//Befreien Sie Asien und Südamerika
		ArrayList<Kontinent> mission3Kontinente = new ArrayList<Kontinent>();
		mission3Kontinente.add(kontinentenListe.get(1));
		mission3Kontinente.add(kontinentenListe.get(4));
		missionsListe.add(new KontinentenMission(3,platzhalterSpieler,mission3Kontinente));
		//Befreien Sie Afrika und Asien
		ArrayList<Kontinent> mission4Kontinente = new ArrayList<Kontinent>();
		mission4Kontinente.add(kontinentenListe.get(1));
		mission4Kontinente.add(kontinentenListe.get(3));
		missionsListe.add(new KontinentenMission(4,platzhalterSpieler,mission4Kontinente));
		//Befreien Sie 24 Laender Ihrer Wahl
		missionsListe.add(new LaenderMission(5,platzhalterSpieler, 24, 1, laenderListe));
		//Befreien Sie 18 Laender und setzen Sie in jedes Land mindestens 2 Armeen
		missionsListe.add(new LaenderMission(6,platzhalterSpieler, 18, 2, laenderListe));
		//Befreien Sie alle Länder von den roten Armeen
		missionsListe.add(new SpielerMission(7,platzhalterSpieler,platzhalterSpieler,spielerListe));
		missionsListe.add(new SpielerMission(8,platzhalterSpieler,platzhalterSpieler,spielerListe));
	}
	
	/**
	 * verteilt Missionen an Spieler
	 * @param spielerListe
	 */
	public void missionenVerteilen(ArrayList<Spieler> spielerListe){
		ArrayList<Mission> speicher= new ArrayList<Mission>();
		for(Mission m : this.missionsListe){
			speicher.add(m);
		}
			
		for(Spieler s : spielerListe){
			Mission spielerMission = null;
			int random = (int)(Math.random() * speicher.size());
			for(Mission m : this.missionsListe){
				if(m.getId() == speicher.get(random).getId()){
					spielerMission = m;
				}
			}
			
			if(spielerMission instanceof LaenderMission){
				spielerMission.setSpieler(s);
			} else if(spielerMission instanceof SpielerMission) {
				boolean gegnerGefunden = false;
				
				do{
					int random2 = (int)(Math.random() * spielerListe.size());
					if(!s.equals(spielerListe.get(random2))){
						spielerMission.setSpieler2(spielerListe.get(random2));
						gegnerGefunden = true;
					}
				}while(!gegnerGefunden);
				
				spielerMission.setSpieler(s);
				((SpielerMission) spielerMission).resetBeschreibung();
							
			} else if(spielerMission instanceof KontinentenMission){
				spielerMission.setSpieler(s);
			}
			speicher.remove(random);	
		}
	}
	
	/**
	 * gibt Missionen aus
	 * @param spieler
	 * @return String
	 */
	public String missionAusgeben(Spieler spieler){
		String ausgabe = "";
		for(Mission m : this.missionsListe){
			if(m.getSpieler() != null && m.getSpieler().equals(spieler)){
				ausgabe = m.getBeschreibung();
			}
		}
		return ausgabe;
	}
	
	/**
	 * Getter Missionsliste
	 * @return ArrayList<Mission>
	 */
	public ArrayList<Mission> getMissionsListe(){
		return this.missionsListe;
	}
	
	/**
	 *  getter Mission von Spieler
	 * @param spieler
	 * @return
	 */
	public Mission getSpielerMission(Spieler spieler){
		for(Mission m : missionsListe){
			if(m.getSpieler().equals(spieler)){
				return m;
			}
		}
		return null;
	}
}
