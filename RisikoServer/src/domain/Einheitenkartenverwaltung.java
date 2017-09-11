package domain;

import java.util.ArrayList;
import java.util.Collections;

import valueobjects.Einheitenkarten;
import valueobjects.Spieler;

public class Einheitenkartenverwaltung{

	private ArrayList<Einheitenkarten> kartenstapel;
	private int kartenEingeloest = 1;
	
	/**
	 * Kosntruktor Einheitenkartenverwaltung
	 */
	public Einheitenkartenverwaltung() {
		kartenstapelErstellen();
	}

	/**
	 *  erstellt Kartenstapel zum "ziehen"
	 * @return ArrayList<Einheitenkarten>
	 */
	public ArrayList<Einheitenkarten> kartenstapelErstellen() {
		kartenstapel = new ArrayList<Einheitenkarten>();
		for(int i = 0;i < 14;i++) {
			kartenstapel.add(new Einheitenkarten("Soldat"));
			kartenstapel.add(new Einheitenkarten("Pferd"));
			kartenstapel.add(new Einheitenkarten("Panzer"));
		}
		kartenstapel.add(new Einheitenkarten("Joker"));
		kartenstapel.add(new Einheitenkarten("Joker"));
		Collections.shuffle(kartenstapel);
		
		return kartenstapel;
	}
	
	/**
	 * Spieler zieht eine Karte
	 * @param spieler
	 */
	public void karteNehmen(Spieler spieler) {
		if(spieler.getEinheitenkarten().size() < 5){
			spieler.karteNehmen(kartenstapel.get(0));
			kartenstapel.remove(0);
		}
	}
	
	/**
	 * Einheitenkarten werden aus der Hand des Gegeners gelöscht
	 * @param spieler
	 * @param benutzteKarten
	 */
	private void einheitenKartenVonSpielerEntfernen(Spieler spieler, ArrayList<String> benutzteKarten)
	{
		ArrayList<Einheitenkarten> kartenListe = spieler.getEinheitenkarten();
		
		for (String karte: benutzteKarten) {
			for(Einheitenkarten k : kartenListe){
				if(k.getKartenwert().equals(karte)){
					kartenListe.remove(k);
					break;
				}
			}
		}
	}
	
	/**
	 * einheitenkarten werden eingelöst und zustzliche einheiten zurückgegeben 
	 * @param spieler
	 * @param tauschKarten
	 * @return int
	 */
	public int einheitenkartenEinloesen(Spieler spieler,ArrayList<String> tauschKarten) {
		int einheiten = 0;
			
		switch(kartenEingeloest) {
		case 1: einheiten = 4;
				break;
		case 2: einheiten = 6;
				break;
		case 3: einheiten = 8;
				break;
		case 4: einheiten = 10;
				break;
		case 5: einheiten = 12;
				break;
		case 6: einheiten = 15;
				break;
		}
		
		if(kartenEingeloest > 6) {
			einheiten = 15 + (kartenEingeloest - 6) * 5;
		}
		
		kartenEingeloest++;
		einheitenKartenVonSpielerEntfernen(spieler, tauschKarten);
		
		return einheiten;
	}
}
