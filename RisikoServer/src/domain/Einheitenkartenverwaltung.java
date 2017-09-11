package domain;

import java.util.ArrayList;
import java.util.Collections;

import valueobjects.Einheitenkarten;
import valueobjects.Spieler;

public class Einheitenkartenverwaltung{

	private ArrayList<Einheitenkarten> kartenstapel;
	private int kartenEingeloest = 1;
	
	
	public Einheitenkartenverwaltung() {
		kartenstapelErstellen();
	}

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
	
	public void karteNehmen(Spieler spieler) {
		if(spieler.getEinheitenkarten().size() < 5){
			spieler.karteNehmen(kartenstapel.get(0));
			kartenstapel.remove(0);
		}
	}
	
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