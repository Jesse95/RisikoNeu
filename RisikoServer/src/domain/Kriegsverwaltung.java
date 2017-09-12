package domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import exceptions.KannEinheitenNichtVerschiebenException;
import exceptions.KannLandNichtBenutzenException;
import exceptions.KeinNachbarlandException;
import exceptions.LandBereitsBenutztException;
import exceptions.NichtGenugEinheitenException;
import exceptions.SpielerExistiertBereitsException;
import persistence.FilePersistenceManager;
import valueobjects.Angriff;
import valueobjects.AngriffRueckgabe;
import valueobjects.Einheitenkarten;
import valueobjects.Kontinent;
import valueobjects.Land;
import valueobjects.Mission;
import valueobjects.Phase;
import valueobjects.Phase.phasen;
import valueobjects.Spieler;
import valueobjects.Spielstand;

public class Kriegsverwaltung{

private Spielerverwaltung spielerVw;
private Weltverwaltung weltVw;
private Missionsverwaltung missionVw;
private Phase.phasen Phase = phasen.STARTPHASE;
private ArrayList<Land> benutzteLaender = new ArrayList<Land>();
private FilePersistenceManager pm = new FilePersistenceManager();
	
	/**
	 * Konstruktor Kriegsverwaltung
	 * @param spielerVw
	 * @param weltVw
	 */
	public Kriegsverwaltung(Spielerverwaltung spielerVw, Weltverwaltung weltVw, Missionsverwaltung missionVw) {
		this.spielerVw = spielerVw;
		this.weltVw = weltVw;
		this.missionVw = missionVw;
	}

	/**
	 * Gibt Nachbarlaender die angegriffen werden können zurück
	 * @param land
	 * @param spieler
	 * @return ArrayList<Land>
	 */
	public ArrayList<Land> moeglicheAngriffsziele(Land land) {
		ArrayList<Land> nachbarLaender = this.weltVw.getNachbarLaender(land);	
		Spieler spieler = land.getBesitzer();
		ArrayList<Land> rueckgabe = new ArrayList<Land>();
		for(Land l : nachbarLaender){
			if(!l.getBesitzer().equals(spieler)){
				rueckgabe.add(l);
			}
		}
		return rueckgabe;
	}
	
	/**
	 * Überprüft ob die beiden Länder Nachbarn sind
	 * @param wahlLand
	 * @param landZiel
	 * @param spieler
	 * @return boolean
	 * @throws KeinNachbarlandException
	 */
	public boolean istNachbar(Land wahlLand, Land landZiel) throws KeinNachbarlandException{
		List<Land> nachbarLaender = this.weltVw.getNachbarLaender(wahlLand);
		for(Land l : nachbarLaender){
			if(l.getName().equals(landZiel.getName())){
				return true;
			}
		}
		throw new KeinNachbarlandException(wahlLand.getName());
	}
	
	/**
	 * Würfelt mehrfach und gibt Liste mit Ergebnissen zurück
	 * @param anzahl
	 * @return Vector<Integer>
	 */
	public ArrayList<Integer> wuerfeln(int anzahl) {
		ArrayList<Integer> ergebnisse = new ArrayList<Integer>();
				
		for(int i = 0;i < anzahl;i++) {
			ergebnisse.add((int)(Math.random() * 6) + 1);
		}
		Comparator<Integer> comparator = Collections.reverseOrder();
		Collections.sort(ergebnisse, comparator);
		//jetzt ist die Stelle 0 die höchste Zahl und die Stelle 1 die zweithöchste
		return ergebnisse;
	}
	
	/**
	 *  führt einen Angriff durch
	 * @param angriff
	 * @return AngriffRueckgabe
	 * @throws KeinNachbarlandException
	 */
	public AngriffRueckgabe befreiungsAktion(Angriff angriff) throws KeinNachbarlandException {
		Land angreifendesLand = landServerVerbindung(angriff.getAngriffsland());
		Land verteidigendesLand = landServerVerbindung(angriff.getVerteidigungsland());
		istNachbar(angriff.getAngriffsland(), angriff.getVerteidigungsland());
	
		
		int angreiferEinheiten = angreifendesLand.getEinheiten();
		int verteidigerEinheiten = verteidigendesLand.getEinheiten();
		int angreifendeEinheiten;
		int verteidigendeEinheiten;
		ArrayList<Integer> wuerfeAngreifer;
		ArrayList<Integer> wuerfeVerteidiger;
		ArrayList<Integer> verluste = new ArrayList<Integer>();
		AngriffRueckgabe rueckgabe;
		boolean erobert = false;

		if(angreiferEinheiten < 4) {
			angreifendeEinheiten = angreiferEinheiten - 1;
		} else {
			angreifendeEinheiten = 3;
		}
		
		if(verteidigerEinheiten < 2) {
			verteidigendeEinheiten = verteidigerEinheiten;
		} else {
			verteidigendeEinheiten = 2;
		}
		
		wuerfeAngreifer = wuerfeln(angreifendeEinheiten);
		wuerfeVerteidiger = wuerfeln(verteidigendeEinheiten);
		
		if(wuerfeVerteidiger.size() == 1)	{
			if(wuerfeVerteidiger.get(0) < wuerfeAngreifer.get(0))	{
				//Angreifer gewonnen
				verluste.add(0); //Verluste die der Angreifer macht
				verluste.add(1); //Verluste die der Verteidiger macht
			} else if(wuerfeVerteidiger.get(0) >= wuerfeAngreifer.get(0))	{
				//Verteidiger gewonnen
				verluste.add(1); //Verluste die der Angreifer macht
				verluste.add(0); //Verluste die der Verteidiger macht
			}
		} else if(wuerfeVerteidiger.size() == 2)	{
			if(wuerfeAngreifer.size() >= 2) {
				if((wuerfeVerteidiger.get(0) < wuerfeAngreifer.get(0) && (wuerfeVerteidiger.get(1) < wuerfeAngreifer.get(1))))	{
					//Angreifer gewonnen
					verluste.add(0); //Verluste die der Angreifer macht
					verluste.add(2); //Verluste die der Verteidiger macht
				} else if((wuerfeVerteidiger.get(0) >= wuerfeAngreifer.get(0) && (wuerfeVerteidiger.get(1) >= wuerfeAngreifer.get(1))))	{
					//Verteidiger gewonnen
					verluste.add(2); //Verluste die der Angreifer macht
					verluste.add(0); //Verluste die der Verteidiger macht
				} else	{
					verluste.add(1);
					verluste.add(1);
				}
			} else {
				if(wuerfeVerteidiger.get(0) < wuerfeAngreifer.get(0)) {
					//Angreifer gewonnen
					verluste.add(0); //Verluste die der Angreifer macht
					verluste.add(1); //Verluste die der Verteidiger macht
				} else if(wuerfeVerteidiger.get(0) >= wuerfeAngreifer.get(0))	{
					//Verteidiger gewonnen
					verluste.add(1); //Verluste die der Angreifer macht
					verluste.add(0); //Verluste die der Verteidiger macht
				}
			}
		}
		
		//verluste ist ein ArrayList mit den Angaben: AngreiferVerlust / VerteidigerVerlust
		angreifendesLand.setEinheiten(angreifendesLand.getEinheiten() - verluste.get(0));
		verteidigendesLand.setEinheiten(verteidigendesLand.getEinheiten() - verluste.get(1));

		if(verteidigendesLand.getEinheiten() == 0) {
			erobert = true;
			verteidigendesLand.setBesitzer(angreifendesLand.getBesitzer());
//			angreifendesLand.setEinheiten(angreifendesLand.getEinheiten() - 1);
			verteidigendesLand.setEinheiten(0);
		}
		
		rueckgabe = new AngriffRueckgabe(verluste.get(1), verluste.get(0), wuerfeVerteidiger, wuerfeAngreifer, erobert);
		return rueckgabe;
	}
	
	/**
	 * Setzt eine gewisse Anzahl an Einheiten auf ein Land
	 * @param anzahl
	 * @param land
	 */
	public void einheitenPositionieren(int anzahl, Land land) {
		
		landServerVerbindung(land).setEinheiten(land.getEinheiten() + anzahl);
	}
	
	/**
	 * Bestimmt die Anzahl an Einheiten, die der Spieler bekommt
	 * @param spieler
	 * @return int
	 */
	public int bekommtEinheiten(Spieler spieler) {
		boolean besitztLaenderVonKontinent = true;
		int einheiten = weltVw.besitztLaender(spieler).size() / 3;
		System.out.println(spieler.getName() + "kriegt Einheiten durch Länder(" +  weltVw.besitztLaender(spieler).size() + ") - "+ einheiten);
		if (einheiten < 3) {
			einheiten = 3;
		}
		
			for (Kontinent k : weltVw.getKontinentenListe()){
				besitztLaenderVonKontinent = true;
				for (Land l : k.getLaender()){
					
					if(!l.getBesitzer().getName().equals(spieler.getName())) {
						besitztLaenderVonKontinent = false;
					}
				}
				if (besitztLaenderVonKontinent){
					switch(k.getName()) {
					case "Europa":
						einheiten += 5;
						break;
					case "Asien":
						einheiten += 7;
						break;
					case "Afrika":
						einheiten += 3;
						break;
					case "Suedamerika":
						einheiten += 2;
						break;
					case "Nordamerika":
						einheiten += 5;
						break;
					case "Australien":
						einheiten += 2;
						break;
					}
				System.out.println(k.getName() + " kommt dazu. Jetzt sinds" + einheiten);
			}
			}
			System.out.println("Ende" + einheiten);

			return einheiten;
		}
	
	public int checkAnfangseinheiten(){
		int einheiten = 0;
		switch(spielerVw.getSpielerList().size()+1){
		case 2:
			einheiten = 25;
			break;
		case 3:
			einheiten = 20;
			break;
		case 4:
			einheiten = 20;
			break;
		case 5:
			einheiten = 15;
			break;
		case 6:
			einheiten = 15;
			break;
		default:
			einheiten = 20;
		}
		return einheiten;
	}
	
	/**
	 * Setzt die nächste Phase
	 */
	public void nextTurn(){
		switch(Phase){
			case STARTPHASE:
				Phase = phasen.VERTEILEN;
				break;
			case VERSCHIEBEN:
				Phase = phasen.VERTEILEN;
				spielerVw.naechsterSpieler();
				break;
			case ANGRIFF:
				Phase = phasen.VERSCHIEBEN;
				break;
			case VERTEILEN:
				Phase = phasen.ANGRIFF;
				break;
		}
	}
	
	/**
	 * Gibt die aktuelle Phase zurück
	 * @return Phase
	 */
	public phasen getTurn(){
		return Phase;
	}
	
	/**
	 * setzt Turn
	 * @param phase
	 */
	public void setTurn(String phase){
		switch(phase){
		case "STARTPHASE":
			this.Phase = phasen.STARTPHASE;
			break;
		case "VERSCHIEBEN":
			this.Phase = phasen.VERSCHIEBEN;
			break;
		case "ANGRIFF":
			this.Phase = phasen.ANGRIFF;
			break;
		case "VERTEILEN":
			this.Phase = phasen.VERTEILEN;
			break;
		}
	}
	
	/**
	 * Gibt den Spieler zurück
	 * @param spieler
	 * @return Spieler
	 */
	public Spieler nextSpieler(Spieler spieler){
		return spieler;
	}
	
	/**
	 * Besetzt das eroberte Land
	 * @param aLand
	 * @param vLand
	 * @param einheiten
	 */
	public void eroberungBesetzen(Land aLand, Land vLand, int einheiten){
		this.einheitenPositionieren(einheiten, vLand);
		this.einheitenPositionieren(-einheiten, aLand);
	}
	
	/**
	 * Überprüft ob das Land existiert und dem Spieler gehört
	 * @param land
	 * @param spieler
	 * @return boolean
	 * @throws KannLandNichtBenutzenException
	 */
	public boolean landWaehlen(Land land, Spieler spieler) throws KannLandNichtBenutzenException{
		if(!land.getBesitzer().equals(spieler)){
			throw new KannLandNichtBenutzenException();	
		}else{
			return true;
		}
	}
	
	/**
	 * Überprüft ob die eingegebene Einheitenzahl größer ist, als die Einheitenzahl auf dem Land
	 * @param land
	 * @param einheiten
	 * @return boolean
	 * @throws NichtGenugEinheitenException
	 */
	public boolean checkEinheiten(Land land) throws NichtGenugEinheitenException{
		int landEinheiten = land.getEinheiten();
		
		if(landEinheiten < 2){
			throw new NichtGenugEinheitenException();
		}else{
			return true;
		}
	}
	
	/**
	 * Gibt alle eigenen NAchbarländer als Tabelle zurück
	 * @param land
	 * @param spieler
	 * @return String
	 */
	public ArrayList<Land> moeglicheVerschiebeZiele(Land land, Spieler spieler) {
		ArrayList<Land> nachbarLaender = this.weltVw.getNachbarLaender(land);
		ArrayList<Land> rueckgabe = new ArrayList<Land>();
		
		for(Land l : nachbarLaender) {
			if(spieler.equals(l.getBesitzer())) {
				rueckgabe.add(l);
			}
		}
		return rueckgabe;
	}
	
	/**
	 * Gibt zurück, ob das übergebene Land in der Runde für einen Angriff benutzt wurde
	 * @param land
	 * @return boolean
	 * @throws LandBereitsBenutztException
	 */
	public boolean benutzeLaender(Land land) throws LandBereitsBenutztException {
		if(benutzteLaender.contains(land)){
			throw new LandBereitsBenutztException(land.getName());
		}else{
			return true;
		}
	}
	
	/**
	 * Fügt der liste der benutzten Länder das übergebene Land zu
	 * @param land
	 */
	public void landBenutzen(Land land) {
		benutzteLaender.add(land);
	}
	
	/**
	 * Löscht die Liste, der Länder, die in einer Runde für einen Angriff benutzt wurden
	 */
	public void benutzteLaenderLoeschen() {
		benutzteLaender.clear();
	}
	
	/**
	 * Gibt die Liste der Länder zurück, die für einen Angriff benutzt wurden
	 * @return List<Land>
	 */
	public ArrayList<Land> getBenutzteLaenderListe() {
		return benutzteLaender;
	}
	
	/**
	 * Überprüft ob der Spieler genug Einheiten zum verschieben hat
	 * @param einheiten
	 * @param spieler
	 * @return boolean
	 * @throws KannEinheitenNichtVerschiebenException
	 */
	public boolean checkEinheitenAnzahlVerteilbar(Land land, int einheiten) throws KannEinheitenNichtVerschiebenException{
		int landEinheiten = land.getEinheiten();
		if((landEinheiten - einheiten) < 1 || einheiten < 1){
			throw new KannEinheitenNichtVerschiebenException();
		}else{
			return true;
		}
	}
	
	/**
	 * get Länder von Spieler
	 * @param spieler
	 * @return ArrayList<Land>
	 */
	public ArrayList<Land> getSpielerLaender(Spieler spielerA){
		Spieler spieler = spielerServerVerbindung(spielerA);
		ArrayList<Land> rueckgabeLaender = new ArrayList<Land>();
		for(Land l : weltVw.getLaenderListe()){
			if(l.getBesitzer().equals(spieler)){
				rueckgabeLaender.add(l);
			}
		}
		return rueckgabeLaender;
	}
	
	/**
	 * speichert das Spiel
	 * @param datei
	 * @throws IOException
	 */
	public void spielSpeichern(String datei) throws IOException{
		pm.schreibkanalOeffnen(datei);
		pm.spielSpeichern(weltVw.getLaenderListe(), spielerVw.getSpielerList(), getTurn() + "", spielerVw.getAktiverSpielerNummer(), missionVw.getMissionsListe(), bekommtEinheiten(spielerVw.getAktiverSpieler()));
		pm.close();
	}
	
	/**
	 * läd Spielstand
	 * @param datei
	 * @return
	 * @throws IOException
	 * @throws SpielerExistiertBereitsException
	 */
	public Spielstand spielLaden(String datei) throws IOException, SpielerExistiertBereitsException {
		return pm.spielLaden(datei);
	}
	
	/**
	 * entfernt einen Spieler
	 * @param spieler
	 * @return
	 */
	public boolean spielerRaus(Spieler spielerA) {
		Spieler spieler = spielerServerVerbindung(spielerA);
		for(Land l : weltVw.getLaenderListe()) {
			if(l.getBesitzer().equals(spieler)) {
				return false;
			}
		}
		spielerVw.getSpielerList().remove(spieler);
		return true;
	}
	
	/**
	 * gibt Mission eines Spieler zurück
	 * @return Mission
	 */
	public Mission getMissionVonSpieler(Spieler spielerA){
	Spieler spieler = spielerServerVerbindung(spielerA);
		for(Mission m: missionVw.getMissionsListe()) {
			if(m.getSpieler().equals(spieler)) {
				return m;
			}
		}
	return null;
	}
	
	/**
	 * vergleicht und setzt land aus client im server
	 * @param land
	 * @return Land
	 */
	public Land landServerVerbindung (Land land){
		Land rueckgabe = null;
		for(Land l :weltVw.getLaenderListe()){
			if(land.getName().equals(l.getName())){
				rueckgabe = l;
			}
		}
		return rueckgabe;
	}
	
	/**
	 * vergleicht und setzt Spieler aus client im server
	 * @param spieler
	 * @return Spieler
	 */
	public Spieler spielerServerVerbindung (Spieler spieler){
		Spieler rueckgabe = null;
		for(Spieler s :spielerVw.getSpielerList()){
			if(spieler.getName().equals(s.getName())){
				rueckgabe = s;
			}
		}
		return rueckgabe;
	}
}
