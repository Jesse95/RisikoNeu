package client;

import java.util.ArrayList;
import java.util.List;

import valueobjects.Land;
import valueobjects.Mission;
import valueobjects.Spieler;

public class SpielSpeichern {

	public SpielSpeichern(){
		
	}
	public boolean spielSpeichern(ArrayList<Land> welt, ArrayList<Spieler> spielerListe, String phase, int aktiverSpieler, List<Mission> missionsListe){
//		schreibeZeile(phase);
//		for(Spieler s : spielerListe){
//			schreibeZeile(s.getName());
//		}
//		schreibeZeile("");
//		for(Land l : welt){
//			schreibeZeile(l.getName());
//			schreibeZeile(l.getBesitzer().getName());
//			schreibeZeile(l.getEinheiten() + "");
//			schreibeZeile(l.getKuerzel());
//			schreibeZeile(l.getFahneX() + "");
//			schreibeZeile(l.getFahneY() + "");
//		}
//		schreibeZeile("");
//		
//		schreibeZeile(aktiverSpieler+"");
//
//		for(Spieler s: spielerListe){
//			for(Mission m: missionsListe){
//				if(s.getName().equals(m.getSpieler().getName()))
//				{
//					schreibeZeile(m.getSpieler().getName());
//					schreibeZeile(m.getArt());
//					if(m.getArt().equals("spieler")){
//						schreibeZeile(m.getSpieler2().getName());
//					}
//					schreibeZeile(m.getId()+"");
//				}
//			}
//		}
//		
////		schreibeZeile("");
//		
//		for(Spieler s: spielerListe) {
//			for(Einheitenkarten k:s.getEinheitenkarten()){
//				schreibeZeile(k.getKartenwert());
//			}
//			schreibeZeile("");
//		}
//		
		return true;
	}
}
