package local.domain;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import local.valueobjects.Kontinent;
import local.valueobjects.KontinentenMission;
import local.valueobjects.LaenderMission;
import local.valueobjects.Land;
import local.valueobjects.Mission;
import local.valueobjects.Spieler;
import local.valueobjects.SpielerMission;

public class Missionsverwaltung {
	public List<Mission> missionsListe = new Vector<Mission>();
	
	/**
	 * Erstellt die Missionsliste
	 * @param spielerListe 
	 * @param kontinentenListe 
	 * @param laenderListe 
	 * @throws IOException 
	 */
	public void missionsListeErstellen(List<Land> laenderListe, List<Kontinent> kontinentenListe, List<Spieler> spielerListe) {
		Spieler platzhalterSpieler = new Spieler("Platzhalter");
		//Befreien Sie Nordamerika und Afrika
		List<Kontinent> mission1Kontinente = new Vector<Kontinent>();
		mission1Kontinente.add(kontinentenListe.get(3));
		mission1Kontinente.add(kontinentenListe.get(5));
		missionsListe.add(new KontinentenMission(1,platzhalterSpieler,mission1Kontinente));
		//Befreien Sie Nordamerika und Australien
		List<Kontinent> mission2Kontinente = new Vector<Kontinent>();
		mission2Kontinente.add(kontinentenListe.get(2));
		mission2Kontinente.add(kontinentenListe.get(5));
		missionsListe.add(new KontinentenMission(2,platzhalterSpieler,mission2Kontinente));
		//Befreien Sie Asien und Südamerika
		List<Kontinent> mission3Kontinente = new Vector<Kontinent>();
		mission3Kontinente.add(kontinentenListe.get(1));
		mission3Kontinente.add(kontinentenListe.get(4));
		missionsListe.add(new KontinentenMission(3,platzhalterSpieler,mission3Kontinente));
		//Befreien Sie Afrika und Asien
		List<Kontinent> mission4Kontinente = new Vector<Kontinent>();
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
	 * 
	 * @param spielerListe
	 */
	public void missionenVerteilen(List<Spieler> spielerListe){
		List<Mission> speicher= new Vector<Mission>();
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
	 * 
	 * @param spieler
	 * @return
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
	 * 
	 * @return
	 */
	public List<Mission> getMissionsListe(){
		return this.missionsListe;
	}
	
	/**
	 * 
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
	
	/**
	 * 
	 * @param laenderListe
	 * @param kontinentenListe
	 * @param spielerListe
	 * @param spieler
	 * @param spieler2
	 * @param id
	 */
	public void missionLaden(List<Land> laenderListe, List<Kontinent> kontinentenListe, List<Spieler> spielerListe, Spieler spieler, Spieler spieler2, int id) {
		if(id == 1) {
			//Befreien Sie Nordamerika und Afrika
			List<Kontinent> mission1Kontinente = new Vector<Kontinent>();
			mission1Kontinente.add(kontinentenListe.get(3));
			mission1Kontinente.add(kontinentenListe.get(5));
			missionsListe.add(new KontinentenMission(1,spieler,mission1Kontinente));
		} else if(id == 2) {
			//Befreien Sie Nordamerika und Australien
			List<Kontinent> mission2Kontinente = new Vector<Kontinent>();
			mission2Kontinente.add(kontinentenListe.get(2));
			mission2Kontinente.add(kontinentenListe.get(5));
			missionsListe.add(new KontinentenMission(2,spieler,mission2Kontinente));
		} else if(id == 3) {
			//Befreien Sie Asien und Südamerika
			List<Kontinent> mission3Kontinente = new Vector<Kontinent>();
			mission3Kontinente.add(kontinentenListe.get(1));
			mission3Kontinente.add(kontinentenListe.get(4));
			missionsListe.add(new KontinentenMission(3,spieler,mission3Kontinente));
		} else if(id == 4) {
			//Befreien Sie Afrika und Asien
			List<Kontinent> mission4Kontinente = new Vector<Kontinent>();
			mission4Kontinente.add(kontinentenListe.get(1));
			mission4Kontinente.add(kontinentenListe.get(3));
			missionsListe.add(new KontinentenMission(4,spieler,mission4Kontinente));
		} else if(id == 5) {
			//Befreien Sie 24 Laender Ihrer Wahl
			missionsListe.add(new LaenderMission(5,spieler, 24, 1, laenderListe));
		} else if(id == 6) {
			//Befreien Sie 18 Laender und setzen Sie in jedes Land mindestens 2 Armeen
			missionsListe.add(new LaenderMission(6,spieler, 18, 2, laenderListe));
		} else if(id == 7) {
			//Befreien Sie alle Länder von den roten Armeen
			System.out.println("1");
			missionsListe.add(new SpielerMission(7,spieler,spieler2,spielerListe));
			System.out.println("2");
		} else if(id == 8) {
			missionsListe.add(new SpielerMission(8,spieler,spieler2,spielerListe));
		}
	}
}
