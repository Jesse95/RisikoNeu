package valueobjects;

import java.io.Serializable;
import java.util.ArrayList;

public class Spielstand implements Serializable{

	private String aktuellePhase;
	private ArrayList<Spieler> spielerListe;
	private ArrayList<Land> laenderListe;
	private ArrayList<Mission> missionsListe;
	private ArrayList<Kontinent> kontinentenListe;
	private int aktiverSpielerNummer;
	private int setzbareEinheitenVerteilen;
	
	
	public Spielstand() {
		super();
		this.spielerListe = new ArrayList<Spieler>();
		this.laenderListe = new ArrayList<Land>();
		this.missionsListe = new ArrayList<Mission>();
		this.kontinentenListe = new ArrayList<Kontinent>();
	}

	/**Gibt aktuelle Phase des Spielstands zurück.
	 * @return
	 */
	public String getAktuellePhase() {
		return aktuellePhase;
	}
	
	/**Setzt aktuelle Phase des Spielstands.
	 * @param aktuellePhase
	 */
	public void setAktuellePhase(String aktuellePhase) {
		this.aktuellePhase = aktuellePhase;
	}
	
	/**Gibt die Spielerliste des Spielstand zurück.
	 * @return
	 */
	public ArrayList<Spieler> getSpielerListe() {
		return spielerListe;
	}
	
	/**Setzt die Spielerliste des Spielstands.
	 * @param spielerListe
	 */
	public void setSpielerListe(ArrayList<Spieler> spielerListe) {
		this.spielerListe = spielerListe;
	}
	
	/**Gibt die Länderliste des Spielstand zurück.
	 * @return
	 */
	public ArrayList<Land> getLaenderListe() {
		return laenderListe;
	}
	
	/**Setzt die Länderliste des Spielstands.
	 * @param laenderListe
	 */
	public void setLaenderListe(ArrayList<Land> laenderListe) {
		this.laenderListe = laenderListe;
	}
	
	/**Gibt die Missionsliste des Spielstands zurück.
	 * @return
	 */
	public ArrayList<Mission> getMissionsListe() {
		return missionsListe;
	}
	
	/**Setzt die Mission des Spielstands.
	 * @param missionsListe
	 */
	public void setMissionsListe(ArrayList<Mission> missionsListe) {
		this.missionsListe = missionsListe;
	}
	
	/**Gibt die Nummer vom aktiven Spieler des Spielstands zurück.
	 * @return
	 */
	public int getAktiverSpielerNummer() {
		return aktiverSpielerNummer;
	}
	
	/**Setzt die Nummer des aktiven Spielers im Spielstand.
	 * @param aktiverSpieler
	 */
	public void setAktiverSpielerNummer(int aktiverSpieler) {
		this.aktiverSpielerNummer = aktiverSpieler;
	}
	
	/**Gibt die Kontinentenliste des Spielstands zurück.
	 * @return
	 */
	public ArrayList<Kontinent> getKontinentenListe() {
		return kontinentenListe;
	}

	/**Setzt die Kontinentenliste des Spielstands.
	 * @param kontinentenListe
	 */
	public void setKontinentenListe(ArrayList<Kontinent> kontinentenListe) {
		this.kontinentenListe = kontinentenListe;
	}

	/** Lädt die Missionen je nach ID in die Missionsliste.
	 * @param laenderListe
	 * @param kontinentenListe
	 * @param spielerListe
	 * @param spieler
	 * @param spieler2
	 * @param id
	 */
	public void missionLaden(ArrayList<Land> laenderListe, ArrayList<Kontinent> kontinentenListe, ArrayList<Spieler> spielerListe, Spieler spieler, Spieler spieler2, int id) {
		if(id == 1) {
			//Befreien Sie Nordamerika und Afrika
			ArrayList<Kontinent> mission1Kontinente = new ArrayList<Kontinent>();
			mission1Kontinente.add(kontinentenListe.get(3));
			mission1Kontinente.add(kontinentenListe.get(5));
			missionsListe.add(new KontinentenMission(1,spieler,mission1Kontinente));
		} else if(id == 2) {
			//Befreien Sie Nordamerika und Australien
			ArrayList<Kontinent> mission2Kontinente = new ArrayList<Kontinent>();
			mission2Kontinente.add(kontinentenListe.get(2));
			mission2Kontinente.add(kontinentenListe.get(5));
			missionsListe.add(new KontinentenMission(2,spieler,mission2Kontinente));
		} else if(id == 3) {
			//Befreien Sie Asien und Südamerika
			ArrayList<Kontinent> mission3Kontinente = new ArrayList<Kontinent>();
			mission3Kontinente.add(kontinentenListe.get(1));
			mission3Kontinente.add(kontinentenListe.get(4));
			missionsListe.add(new KontinentenMission(3,spieler,mission3Kontinente));
		} else if(id == 4) {
			//Befreien Sie Afrika und Asien
			ArrayList<Kontinent> mission4Kontinente = new ArrayList<Kontinent>();
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
			missionsListe.add(new SpielerMission(7,spieler,spieler2,spielerListe));
		} else if(id == 8) {
			missionsListe.add(new SpielerMission(8,spieler,spieler2,spielerListe));
		}
	}
	
	/**
	 * Kontinente werden in den Spielstand geladen.
	 */
	public void kontinenteErstellen() {
		ArrayList<Land> europa = new ArrayList<Land>();
		
		for(int i = 0;i < 7;i++) {
			europa.add(laenderListe.get(i));
		}
	
		kontinentenListe.add(new Kontinent("Europa",europa));
		ArrayList<Land> asien = new ArrayList<Land>();
		
		for (int i=7;i < 19;i++){
			asien.add(laenderListe.get(i));
		}
		kontinentenListe.add(new Kontinent("Asien",asien));
		ArrayList<Land> australien= new ArrayList<Land>();
		for (int i=19;i < 23;i++){
			australien.add(laenderListe.get(i));
		}
		kontinentenListe.add(new Kontinent("Australien",australien));
		ArrayList<Land> afrika = new ArrayList<Land>();
		for (int i=23;i < 29;i++){
			afrika.add(laenderListe.get(i));
		}
		kontinentenListe.add(new Kontinent("Afrika",afrika));
		ArrayList<Land> suedamerika = new ArrayList<Land>();
		for (int i=29;i < 33;i++){
			suedamerika.add(laenderListe.get(i));
		}
		kontinentenListe.add(new Kontinent("Suedamerika",suedamerika));
		ArrayList<Land> nordamerika = new ArrayList<Land>();
		for (int i=33;i < 42;i++){
			nordamerika.add(laenderListe.get(i));
		}
		kontinentenListe.add(new Kontinent("Nordamerika",nordamerika));
	}

	/**Gibt die setzbaren Einheiten in der Verteilenphase des Spielstands zurück.
	 * @return
	 */
	public int getSetzbareEinheitenVerteilen() {
		return setzbareEinheitenVerteilen;
	}

	/**Setzt die setzbaren Einheiten in der Verteilenphase des Spielstands.
	 * @param setzbareEinheitenVerteilen
	 */
	public void setSetzbareEinheitenVerteilen(int setzbareEinheitenVerteilen) {
		this.setzbareEinheitenVerteilen = setzbareEinheitenVerteilen;
	}
}
