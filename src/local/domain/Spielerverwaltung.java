package local.domain;
import java.util.ArrayList;

import local.domain.exceptions.SpielerExistiertBereitsException;
import local.persistence.FilePersistenceManager;
import local.valueobjects.Spieler;

public class Spielerverwaltung{
	private ArrayList<Spieler> spielerListe = new ArrayList<Spieler>();
	private int spielerNummer = 0;
	private FilePersistenceManager pm = new FilePersistenceManager();
	
	/**
	 * Gibt einen bestimmten Spieler aus der Liste zurück
	 * @param index
	 * @return Spieler
	 */
	public Spieler getSpieler(int index) {
		return this.spielerListe.get(index-1);
	}
	
	/**
	 * Gibt den SPieler zurück, der am Zug ist
	 * @return Spieler
	 */
	public Spieler getAktiverSpieler(){
		return this.spielerListe.get(spielerNummer);
	}
	
	/**
	 * 
	 * @return
	 */
	public int getAktiverSpielerNummer(){
		return spielerNummer;
	}
	
	/**
	 * Setzt den neuenSpieler für die nächste Runde
	 */
	public void naechsterSpieler(){
		if(spielerNummer < this.spielerListe.size()-1){
			spielerNummer++;
		}else{
			spielerNummer = 0;
		}
	}
	
	/**
	 * Fügt einen neuen Spieler zu
	 * @param name
	 * @throws SpielerExistiertBereitsException
	 */
	public void neuerSpieler(String name) throws SpielerExistiertBereitsException {
		Spieler neuerSpieler = new Spieler(name);
		if (spielerListe.contains(neuerSpieler) || name.length() == 0) {
			throw new SpielerExistiertBereitsException();
		}
		spielerListe.add(neuerSpieler);
	}

	/**
	 * Gibt die SPielerliste zurück
	 * @return List<Spieler>
	 */
	public ArrayList<Spieler> getSpielerList() {
		return spielerListe;
	}
	
	/**
	 * 
	 * @param spieler
	 */
	public void setAktiverSpieler(int spieler){
		this.spielerNummer = spieler;
	}
	
	public void setSpielerList(ArrayList<Spieler> liste){
		this.spielerListe = liste;
	}

	public void farbenVerteilen(){
		ArrayList<String> farben = new ArrayList<String>();
		farben.add("rot");
		farben.add("gruen");
		farben.add("blau");
		farben.add("gelb");
		farben.add("orange");
		farben.add("cyan");
		for (Spieler s : spielerListe) {
			s.setFarbe(farben.get(0));
			farben.remove(0);
		}
	}
	
}
