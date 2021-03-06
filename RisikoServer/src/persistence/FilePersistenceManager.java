package persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import valueobjects.Einheitenkarten;
import valueobjects.Land;
import valueobjects.Mission;
import valueobjects.Spieler;
import valueobjects.Spielstand;

public class FilePersistenceManager {
	private BufferedReader reader = null;
	private PrintWriter writer = null;
	
	/**
	 * Öffnet den "Lesekanal" für den übergebenen Dateipfad zum einlesen der Weltdatei
	 * @param datei
	 * @throws FileNotFoundException
	 */
	public void lesekanalOeffnen(String datei) throws FileNotFoundException{
		reader = new BufferedReader(new FileReader(datei));
	}
	
	/**
	 * Öffnet den "Lesekanal" für den übergebenen Dateipfad zum einlesen der gespeicherten Spielstände
	 * @param datei
	 * @throws FileNotFoundException
	 */
	public void ladeLesekanalOeffnen(String datei) throws FileNotFoundException{
		reader = new BufferedReader(new FileReader("../RisikoCommon/Speicher/" + datei));
	}
	
	/**
	 * Öffnet den "Schreibkanal" für den übergebenen Dateipfad zum speichern der Spielstände
	 * @param datei
	 * @throws IOException
	 */
	public void schreibkanalOeffnen(String datei) throws IOException{
		writer = new PrintWriter(new BufferedWriter(new FileWriter(datei)));
	}

	/**
	 * Schließt die geöffneten Reader
	 * @return
	 */
	public boolean close(){
		if(writer != null){
			writer.close();
		}
		if(reader != null){
			try{
				reader.close();
			}catch(IOException e){
				e.printStackTrace();
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Liest alle wichtigen Informationen der gespeicherten Länder ein und gibt diese zurück
	 * @return Land
	 * @throws IOException
	 */
	public Land ladeLand() throws IOException{
		String name = liesZeile();
		if(name == null){
			return null;
		}
		String kuerzel = liesZeile();
		int fahneX = Integer.parseInt(liesZeile());
		int fahneY = Integer.parseInt(liesZeile());
		
		return new Land(name,null,1,kuerzel,fahneX, fahneY);
		
	}
	/**
	 * läd das Spiel aus einer Datei
	 * @param datei
	 * @return Spielstand
	 * @throws IOException
	 */
	public Spielstand spielLaden(String datei) throws IOException {
		Spielstand spielstand = new Spielstand();
		ladeLesekanalOeffnen(datei);
		String phase = liesZeile();
		String spieler = "";
		String spieler2 = "";
		Spieler spielerS2 = null;
		String land = "";
		String kuerzel = "";
		String karte = "";
		int einheiten = 0;
		
		spielstand.setAktuellePhase(phase);
		
		do{
			spieler = liesZeile();
			if(spieler.length() != 0){
				spielstand.getSpielerListe().add(new Spieler(spieler));
			}
		}while(spieler.length() != 0);
		
		do{
			land = liesZeile();
			if(land.length() != 0){
				spieler = liesZeile();
				einheiten = Integer.parseInt(liesZeile());
				kuerzel = liesZeile();
				int fahneX = Integer.parseInt(liesZeile());
				int fahneY = Integer.parseInt(liesZeile());
				for(Spieler s : spielstand.getSpielerListe()){
					if(s.getName().equals(spieler)){
						spielstand.getLaenderListe().add(new Land(land,s,einheiten,kuerzel, fahneX, fahneY));
					}
				}
			}	
		}while(land.length() != 0);
		
		int spielerNummer = Integer.parseInt(liesZeile());
		spielstand.setAktiverSpielerNummer(spielerNummer);

		spielstand.kontinenteErstellen();
		
		for(Spieler anzSp: spielstand.getSpielerListe()) {
			spieler = liesZeile();
			String istSpielerMission = liesZeile();
			if(istSpielerMission.equals("spieler"))
			{
				spieler2 = liesZeile();
				for(Spieler s : spielstand.getSpielerListe()){
					if(s.getName().equals(spieler2)){
						spielerS2 = s;
					}
				}
			}
			int id = Integer.parseInt(liesZeile());
			for(Spieler s : spielstand.getSpielerListe()){
				if(s.getName().equals(spieler)){
					spielstand.missionLaden(spielstand.getLaenderListe(), spielstand.getKontinentenListe(), spielstand.getSpielerListe(),s,spielerS2,id);
				}
			}
		}

		for(Spieler s: spielstand.getSpielerListe()) {
			do{
				karte = liesZeile();
				Einheitenkarten einheitenkarte = new Einheitenkarten(karte);
				s.getEinheitenkarten().add(einheitenkarte);
			}while(karte.length() != 0);
		}
		
		if(phase.equals("VERTEILEN")) {
			spielstand.setSetzbareEinheitenVerteilen(Integer.parseInt(liesZeile()));
		}
		close();
		
		return spielstand;
	}

	/**
	 * Bekommt alle wichtigen Infos von dem Spiel übergeben, um diese in einer Textdatei zu speichern
	 * @param welt
	 * @param spielerListe
	 * @param phase
	 * @param aktiverSpieler
	 * @param missionsListe
	 * @param setzbareEinheitenVerteilen
	 * @return boolean
	 */
	public boolean spielSpeichern(ArrayList<Land> welt, ArrayList<Spieler> spielerListe, String phase, int aktiverSpieler, ArrayList<Mission> missionsListe, int setzbareEinheitenVerteilen){
		schreibeZeile(phase);
		int anzahlSpieler = 0;
		for(Spieler s : spielerListe){
			schreibeZeile(s.getName());
			anzahlSpieler++;
		}
		schreibeZeile("");
		for(Land l : welt){
			schreibeZeile(l.getName());
			schreibeZeile(l.getBesitzer().getName());
			schreibeZeile(l.getEinheiten() + "");
			schreibeZeile(l.getKuerzel());
			schreibeZeile(l.getFahneX() + "");
			schreibeZeile(l.getFahneY() + "");
		}
		schreibeZeile("");
		
		schreibeZeile(aktiverSpieler+"");

		for(Spieler s: spielerListe){
			for(Mission m: missionsListe){
				if(s.getName().equals(m.getSpieler().getName()))
				{
					schreibeZeile(m.getSpieler().getName());
					schreibeZeile(m.getArt());
					if(m.getArt().equals("spieler")){
						schreibeZeile(m.getSpieler2().getName());
					}
					schreibeZeile(m.getId()+"");
				}
			}
		}
		
		for(Spieler s: spielerListe) {
			for(Einheitenkarten k:s.getEinheitenkarten()){
				schreibeZeile(k.getKartenwert());
			}
			schreibeZeile("");
		}
		
		//Speicherung für innerhalb der Phase
		if(phase.equals("VERTEILEN")) {
			schreibeZeile(setzbareEinheitenVerteilen + "");
		}
		
		return true;
	}

	/**
	 * Gibt eine Zeile aus einer Datei zurück
	 * @return String
	 * @throws IOException
	 */
	private String liesZeile() throws IOException{
		if(reader != null){
			return reader.readLine();
		}else{
			return "";
		}
	}
	
	/**
	 * Schreibt eine Zeile in die Datei, von dem übergebenen Dateipfad
	 * @param daten
	 */
	private void schreibeZeile(String daten) {
		if (writer != null)
			writer.println(daten);
	}
	
	/**
	 * Liest alle Länder aus der Welt.txt ein, speichert diese in einer ArrayList und gibt diese dann zurück
	 * @return ArrayList<Land>
	 * @throws IOException
	 */
	public ArrayList<Land> laenderAusDateiLaden()	throws IOException{
		lesekanalOeffnen("./Daten/Welt.txt");
		Land land;
		ArrayList<Land> laenderListe = new ArrayList<Land>();
		do{
			land = ladeLand();
			if(land != null){	
				laenderListe.add(land);
			}
		}while(land != null);
		close();
		
		return laenderListe;
	}

}