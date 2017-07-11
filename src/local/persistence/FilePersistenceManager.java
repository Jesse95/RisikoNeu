package local.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import local.valueobjects.Einheitenkarten;
import local.valueobjects.Land;
import local.valueobjects.Mission;
import local.valueobjects.Spieler;

public class FilePersistenceManager {
	private BufferedReader reader = null;
	private PrintWriter writer = null;
	
	public void lesekanalOeffnen(String datei) throws FileNotFoundException{
		reader = new BufferedReader(new FileReader(datei));
	}
	
	public void schreibkanalOeffnen(String datei) throws IOException{
		writer = new PrintWriter(new BufferedWriter(new FileWriter(datei)));
	}

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

	public boolean spielSpeichern(ArrayList<Land> welt, ArrayList<Spieler> spielerListe, String phase, int aktiverSpieler, ArrayList<Mission> missionsListe){
		schreibeZeile(phase);
		for(Spieler s : spielerListe){
			schreibeZeile(s.getName());
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
		
//		schreibeZeile("");
		
		for(Spieler s: spielerListe) {
			for(Einheitenkarten k:s.getEinheitenkarten()){
				schreibeZeile(k.getKartenwert());
			}
			schreibeZeile("");
		}
		
		return true;
	}
	
	public String spielstandLaden() throws IOException{
		return liesZeile();
	}

	private String liesZeile() throws IOException{
		if(reader != null){
			return reader.readLine();
		}else{
			return "";
		}
	}
	
	private void schreibeZeile(String daten) {
		if (writer != null)
			writer.println(daten);
	}

}