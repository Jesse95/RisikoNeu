package local.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import local.domain.exceptions.KeinGegnerException;
import local.domain.exceptions.LandExistiertNichtException;
import local.persistence.FilePersistenceManager;
import local.valueobjects.Kontinent;
import local.valueobjects.Land;
import local.valueobjects.Spieler;

public class Weltverwaltung{

	private static int laenderAnzahl = 42;
	private boolean[][] laenderAufteilung;
	private ArrayList<Land> laenderListe = new ArrayList<Land>();
	private ArrayList<Kontinent> kontinentenListe = new ArrayList<Kontinent>();
	private FilePersistenceManager pm = new FilePersistenceManager();

	/**
	 * @param spielerVw
	 * @throws IOException 
	 */
	public Weltverwaltung(){
		laenderAufteilung = new boolean[laenderAnzahl][laenderAnzahl];
		
		for(int spalte = 0; spalte < laenderAnzahl;spalte++) {
			for(int zeile = 0; zeile < laenderAnzahl;zeile++) {
				laenderAufteilung[spalte][zeile] = false;
			}
		}
	}

	public void erstellen(){
		this.verbindungenErstellen();
		this.kontinenteErstellen();
	}
	
	/**
	 * Teilt die Länder auf
	 * @param anzahlSpieler
	 * @param spielerVw
	 * @param weltVw
	 */
	public void laenderAufteilen(List<Spieler> spielerListe){
		List<Land> laenderWahl = new Vector<Land>();
		int random;
		
		for(Land ls :this.getLaenderListe()){
			laenderWahl.add(ls);
		}
		
		while(laenderWahl.size() > 0){
			for(Spieler s : spielerListe){
				if(laenderWahl.size() > 0){
					random = (int)(Math.random() * laenderWahl.size());
					String land = laenderWahl.get(random).getName();
					for(Land l : this.getLaenderListe()){
						if(l.getName().equals(land)){
							l.setBesitzer(s);
						}
					}
					laenderWahl.remove(random);
				}
			}
		}
		for(Land l : this.laenderListe){
			System.out.println("Das Land: " + l.getName() + " hat den Besitzer " + l.getBesitzer().getName() + " bekommen");
		}
	}

	/**
	 * erstellt Länder
	 */
	public void laenderErstellen()	throws IOException{
		pm.lesekanalOeffnen("Welt.txt");
		Land land;
		do{
				land = pm.ladeLand();
			if(land != null){	
				laenderListe.add(land);
			}
		}while(land != null);
		pm.close();
	}
	
	/**
	 * erstellt Verbindungen zwischen den Ländern
	 */
	private void verbindungenErstellen() {
		verbindungEinfuegen(0,new int[] {1,6,41});
		verbindungEinfuegen(1,new int[] {0,2,3,6});		
		verbindungEinfuegen(2,new int[] {1,3,4,7,14,16});		
		verbindungEinfuegen(3,new int[] {1,2,4,5,6});		
		verbindungEinfuegen(4,new int[] {2,3,5,16,23,27});		
		verbindungEinfuegen(5,new int[] {3,4,6,27});		
		verbindungEinfuegen(6,new int[] {0,1,3,5});	
		
		verbindungEinfuegen(7,new int[] {2,8,14,15});		
		verbindungEinfuegen(8,new int[] {7,9,11,12,15});		
		verbindungEinfuegen(9,new int[] {8,10,11});		
		verbindungEinfuegen(10,new int[] {9,11,12,13,39});		
		verbindungEinfuegen(11,new int[] {8,9,10,12});	
		verbindungEinfuegen(12,new int[] {8,10,11,13,15});		
		verbindungEinfuegen(13,new int[] {10,12});		
		verbindungEinfuegen(14,new int[] {7,15,16,17,2});		
		verbindungEinfuegen(15,new int[] {7,8,12,14,17,18});		
		verbindungEinfuegen(16,new int[] {2,4,14,17,23,24});		
		verbindungEinfuegen(17,new int[] {14,15,16,18});		
		verbindungEinfuegen(18,new int[] {15,17,20});
		
		verbindungEinfuegen(19,new int[] {20,21,22});		
		verbindungEinfuegen(20,new int[] {18,19,21,22});		
		verbindungEinfuegen(21,new int[] {19,22});
		verbindungEinfuegen(22,new int[] {19,20,21});	
		
		verbindungEinfuegen(23,new int[] {4,16,24,27});		
		verbindungEinfuegen(24,new int[] {16,23,25,26,27,28});		
		verbindungEinfuegen(25,new int[] {24,26,27});		
		verbindungEinfuegen(26,new int[] {24,25,28});		
		verbindungEinfuegen(27,new int[] {4,5,23,24,25,31});		
		verbindungEinfuegen(28,new int[] {24,26});
		
		verbindungEinfuegen(29,new int[] {30,31});		
		verbindungEinfuegen(30,new int[] {29,31,32});		
		verbindungEinfuegen(31,new int[] {27,29,30,32});	
		verbindungEinfuegen(32,new int[] {30,31,33});
		
		verbindungEinfuegen(33,new int[] {32,34,35});		
		verbindungEinfuegen(34,new int[] {33,35,37,38});		
		verbindungEinfuegen(35,new int[] {33,34,36,37});		
		verbindungEinfuegen(36,new int[] {35,37,39,40});		
		verbindungEinfuegen(37,new int[] {34,35,36,38,40,41});		
		verbindungEinfuegen(38,new int[] {34,37,41});		
		verbindungEinfuegen(39,new int[] {10,36,40});		
		verbindungEinfuegen(40,new int[] {36,37,39,41});		
		verbindungEinfuegen(41,new int[] {0,37,38,40});
	}
	
	/**
	 * Erstellt alle Kontinente
	 */
	public void kontinenteErstellen() {
		List<Land> europa = new Vector<Land>();
		
		for(int i = 0;i < 7;i++) {
			europa.add(laenderListe.get(i));
		}
	
		kontinentenListe.add(new Kontinent("Europa",europa));
		List<Land> asien = new Vector<Land>();
		
		for (int i=7;i < 19;i++){
			asien.add(laenderListe.get(i));
		}
		kontinentenListe.add(new Kontinent("Asien",asien));
		List<Land> australien= new Vector<Land>();
		for (int i=19;i < 23;i++){
			australien.add(laenderListe.get(i));
		}
		kontinentenListe.add(new Kontinent("Australien",australien));
		List<Land> afrika = new Vector<Land>();
		for (int i=23;i < 29;i++){
			afrika.add(laenderListe.get(i));
		}
		kontinentenListe.add(new Kontinent("Afrika",afrika));
		List<Land> suedamerika = new Vector<Land>();
		for (int i=29;i < 33;i++){
			suedamerika.add(laenderListe.get(i));
		}
		kontinentenListe.add(new Kontinent("Suedamerika",suedamerika));
		List<Land> nordamerika = new Vector<Land>();
		for (int i=33;i < 42;i++){
			nordamerika.add(laenderListe.get(i));
		}
		kontinentenListe.add(new Kontinent("Nordamerika",nordamerika));
	}
	
	
	/**
	 * @param indexLand1
	 * @param nachbarlaender
	 */
	public void verbindungEinfuegen(int indexLand1,int[] nachbarlaender) {
		for(int nachbarland : nachbarlaender) {
			laenderAufteilung[indexLand1][nachbarland] = true;
			laenderAufteilung[nachbarland][indexLand1] = true;
		}
	}
	
	/**
	 * @param land
	 * @return
	 */
	public int indexVonLand(Land land) {
		for(int i = 0;i < laenderListe.size();i++) {
			if(laenderListe.get(i).getName().equals(land.getName())) {
				return i;
			}
		}
		return 9999;
	}
	
	/**
	 * @param index
	 * @return Land
	 */
	public Land landVonIndex(int index) {
		return laenderListe.get(index);
	}
	
	/**
	 * @param land
	 * @return
	 */
	public ArrayList<Land> getNachbarLaender(Land land) {
		int index = indexVonLand(land);
		ArrayList<Land> nachbarLaender = new ArrayList<Land>();
		
		for (int i = 0;i < laenderAufteilung[index].length;i++) {
			if(laenderAufteilung[index][i] == true) {
				nachbarLaender.add(laenderListe.get(i));
			}
		}
		return nachbarLaender;			
	}
	
	/**
	 * @param indexLand1
	 * @param indexLand2
	 * @return boolean
	 */
	public boolean istNachbarland(int indexLand1, int indexLand2) {
		if (laenderAufteilung[indexLand1][indexLand2] == true) {
			return true;
		}
		return false;
	}
	
	/**
	 * @return List<Land>
	 */
	public ArrayList<Land> getLaenderListe() {
		return laenderListe;
	}

	/**
	 * @param laenderListe
	 */
	public void setLaenderListe(ArrayList<Land> laenderListe) {
		this.laenderListe = laenderListe;
	}

	/**
	 * @return boolean[][]
	 */
	public boolean[][] getLaenderAufteilung() {
		return laenderAufteilung;
	}

	/**
	 * @param laenderAufteilung
	 */
	public void setLaenderAufteilung(boolean[][] laenderAufteilung) {
		this.laenderAufteilung = laenderAufteilung;
	}

	/**
	 * @return List<Kontinent>
	 */
	public List<Kontinent> getKontinentenListe() {
		return kontinentenListe;
	}

	/**
	 * @param angriffsLandString
	 * @return Land
	 */
	public Land stringToLand(String angriffsLandString) {
		for(Land land : laenderListe) {
			if(land.getName().equals(angriffsLandString)) {
				return land;
			}
		}
		return null;
	}
	
	/**
	 * @param spieler
	 * @return List<Land>
	 */
	public ArrayList<Land> besitztLaender(Spieler spieler) {
		ArrayList<Land> laender = new ArrayList<Land>();
		
		for(Land land : this.getLaenderListe()) {
			if(spieler.equals(land.getBesitzer())) {
					laender.add(land);
				}
		}
		return laender;
	}
	
	/**
	 * Listet alle Länder auf mit denen man angreifen kann
	 * @param spieler
	 * @return String
	 */
	public ArrayList<Land> eigeneAngriffsLaender(Spieler spieler){
		ArrayList<Land> rueckgabeLaender = new ArrayList<Land>();
		for(Land land : this.getLaenderListe()) {
			if(spieler.equals(land.getBesitzer()) && land.getEinheiten() > 1) {
				rueckgabeLaender.add(land);
			}
		}
		return rueckgabeLaender;
	}

	/**
	 * Überprüft ob ein Land existiert
	 * @param land
	 * @return boolean
	 * @throws LandExistiertNichtException
	 */
	public boolean landExistiert(String land) throws LandExistiertNichtException{
		if(this.stringToLand(land) == null){
			throw new LandExistiertNichtException(land);
		}else{
			return true;
		}
	}
	
	/**
	 * Überprüft ob ein Land dem Gegner gehört
	 * @param land
	 * @param spieler
	 * @return boolean
	 * @throws KeinGegnerException
	 */
	public boolean istGegner(String land,Spieler spieler) throws KeinGegnerException{
		if(this.stringToLand(land).getBesitzer().equals(spieler)){
			throw new KeinGegnerException(land);
		}else{
			return true;
		}
		
	}
	
	/**
	 * Listet alle Länder auf, auf die man verschieben kann
	 * @param spieler
	 * @param land
	 * @return String
	 */
	public ArrayList<Land> eigeneVerschiebeLaender(Spieler spieler, ArrayList<Land> land){
		ArrayList<Land> rueckgabeLaender = new ArrayList<Land>();
					
		for(Land l : this.getLaenderListe()) {
			if(spieler.equals(l.getBesitzer()) && l.getEinheiten() > 1 && !land.contains(l)) {
				rueckgabeLaender.add(l);
				}
			}
		return rueckgabeLaender;
	}
	
	/**
	 * Gibt die Namen und Einheiten von zwei Länder aus
	 * @param erstesLand
	 * @param zweitesLand
	 * @return String
	 */
	public String einheitenAusgabe(Land erstesLand, Land zweitesLand){
		String ausgabe = "Das Land " + erstesLand.getName() + " hat " + erstesLand.getEinheiten() + " Einheiten \n Das Land " + zweitesLand.getName() + " hat " + zweitesLand.getEinheiten() + " Einheiten";
		return ausgabe;
	}
	
	public Land getLandVonIndex(int index){
		return laenderListe.get(index);
	}
	
	/**
	 * 
	 * @param farbe
	 * @return
	 */
	public String getLandVonFarbcode(String farbe){
		String landstring = "";
		switch(farbe){
		//Alaska
		case "3917935":
			landstring = "Alaska";
			break;
		//Nordwest-Terretorium
		case "1791340":
			landstring = "Nordwest-Territorium";
			break;
		//Alberta
		case "0162179":
			landstring = "Alberta";
			break;
		//Ontario
		case "1791680":
			landstring = "Ontario";
			break;
		//Weststaaten
		case "17917879":
			landstring = "Weststaaten";
			break;
		//Quebeck
		case "241790":
			landstring = "Quebec";
			break;
		//Groenland
		case "1793926":
			landstring = "Groenland";
			break;
		//Oststaaten
		case "117179111":
			landstring = "Oststaaten";
			break;
		//Mittelamerika
		case "1791510":
			landstring = "Mittelamerika";
			break;
		//Venezuela
		case "17915858":
			landstring = "Venezuela";
			break;
		//Peru
		case "179160100":
			landstring = "Peru";
			break;
		//Argentinien
		case "1791160":
			landstring = "Argentinien";
			break;
		//Brasilien
		case "17913757":
			landstring = "Brasilien";
			break;
		//Nordwestafrika
		case "179350":
			landstring = "Nordwestafrika";
			break;
		//Aegypten
		case "1798555":
			landstring = "Aegypten";
			break;
		//Ostafrika
		case "17911097":
			landstring = "Ostafrika";
			break;
		//Kongo
		case "1799764":
			landstring = "Kongo";
			break;
		//Suedafrika
		case "17910042":
			landstring = "Suedafrika";
			break;
		//Madagaskar
		case "179900":
			landstring = "Madagaskar";
			break;
		//Westeuropa
		case "179062":
			landstring = "West-Europa";
			break;
		//Suedeuropa
		case "8750179":
			landstring = "Sued-Europa";
			break;
		//Mitteleuropa
		case "13247179":
			landstring = "Mittel-Europa";
			break;
		//Grossbritannien
		case "17960":
			landstring = "Grossbritannien";
			break;
		//Island
		case "2552442":
			landstring = "Island";
			break;
		//Skandinavien
		case "17931158":
			landstring = "Skandinavien";
			break;
		//Ukraine
		case "13982179":
			landstring = "Ukraine";
			break;
		//Mittlerer Osten
		case "174110179":
			landstring = "Mittlerer Osten";
			break;
		//Indien
		case "52121179":
			landstring = "Indien";
			break;
		//Siam
		case "0128179":
			landstring = "Siam";
			break;
		//China
		case "61168179":
			landstring = "China";
			break;
		//Afghanistan
		case "179135160":
			landstring = "Afghanistan";
			break;
		//Ural
		case "170111179":
			landstring = "Ural";
			break;
		//Mongolei
		case "40179123":
			landstring = "Mongolei";
			break;
		//Sibirien
		case "151121179":
			landstring = "Sibirien";
			break;
		//Irtusk
		case "1031790":
			landstring = "Irtusk";
			break;
		//Jakutien
		case "7017925":
			landstring = "Jakutien";
			break;
		//Kamtschatka
		case "3817950":
			landstring = "Kamtschatka";
			break;
		//Japan
		case "21179158":
			landstring = "Japan";
			break;
		//Indonesien
		case "061179":
			landstring = "Indonesien";
			break;
		//Neuguniea
		case "010179":
			landstring = "Neu-Guinea";
			break;
		//Westaustralien
		case "109107179":
			landstring = "Westaustralien";
			break;
		//Ostaustralien
		case "6765179":
			landstring = "Ostaustralien";
			break;
		}
		return landstring;
	}
}
