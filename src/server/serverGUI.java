package server;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import local.domain.Einheitenkartenverwaltung;
import local.domain.Kriegsverwaltung;
import local.domain.Kriegsverwaltung.phasen;
import local.domain.Missionsverwaltung;
import local.domain.Spielerverwaltung;
import local.domain.Weltverwaltung;
import local.domain.exceptions.KannEinheitenNichtVerschiebenException;
import local.domain.exceptions.KannLandNichtBenutzenException;
import local.domain.exceptions.KeinGegnerException;
import local.domain.exceptions.KeinNachbarlandException;
import local.domain.exceptions.LandBereitsBenutztException;
import local.domain.exceptions.LandExistiertNichtException;
import local.domain.exceptions.NichtGenugEinheitenException;
import local.domain.exceptions.SpielerExistiertBereitsException;
import local.valueobjects.Angriff;
import local.valueobjects.AngriffRueckgabe;
import local.valueobjects.Einheitenkarten;
import local.valueobjects.GameActionEvent;
import local.valueobjects.GameControlEvent;
import local.valueobjects.GameEvent;
import local.valueobjects.GameEventListener;
import local.valueobjects.Land;
import local.valueobjects.Mission;
import local.valueobjects.ServerRemote;
import local.valueobjects.Spieler;


public class serverGUI extends UnicastRemoteObject implements ServerRemote{

	public Spielerverwaltung spielerVw;
	public Weltverwaltung weltVw;
	public Kriegsverwaltung kriegsVw;
	public Missionsverwaltung missionVw;
	public Einheitenkartenverwaltung einheitenVw;
	public phasen Phase;
	private List<GameEventListener> listeners;
	private int bereitZaehler = 0;
	private int anzahlSpieler = 0;

	public static void main(String[] args){
		String serviceName = "GameServer";
		try{
			ServerRemote server = new serverGUI();
			Registry registry;
			try{
				registry = LocateRegistry.createRegistry(4711);
				//				registry = LocateRegistry.getRegistry();
			}catch(RemoteException re){
				registry = LocateRegistry.createRegistry(4711);
			}
			registry.rebind(serviceName, server);
			System.out.println("Server gestartet");
		}catch(RemoteException e){
			e.printStackTrace();
		}
	}
	public serverGUI() throws RemoteException{
		listeners = new Vector<>();
		this.spielerVw = new Spielerverwaltung();
		this.weltVw = new Weltverwaltung();
		this.missionVw = new Missionsverwaltung();
		this.einheitenVw = new Einheitenkartenverwaltung();
		this.kriegsVw = new Kriegsverwaltung(spielerVw, weltVw, missionVw);
	}

	public void addGameEventListener(GameEventListener listener) throws RemoteException {
		listeners.add(listener);

	}

	public void removeGameEventListener(GameEventListener listener) throws RemoteException {
		listeners.remove(listener);

	}
	private void listenerBenachrichtigen(GameEvent event)throws RemoteException{
		for (GameEventListener listener : listeners) {

			Thread t = new Thread(new Runnable() {

				public void run() {
					try {
						listener.handleGameEvent(event);							
					} catch (RemoteException e) {
						//						System.out.println(e.getMessage());
						e.printStackTrace();
					}					
				}
			});
			t.start();
		}
	}

	public void erstelleSpieler(String name,int anzahlSpieler) throws SpielerExistiertBereitsException, RemoteException {
		spielerVw.neuerSpieler(name);
		this.anzahlSpieler = anzahlSpieler;
		if(spielerVw.getSpielerList().size() == anzahlSpieler){
			try {
				laenderErstellen();
				laenderverbindungenUndKontinenteErstellen();
				missionsListeErstellen();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.WARNING_MESSAGE);
			}
			missionenVerteilen();
			laenderAufteilen();

			try {
				farbenVerteilen();
			} catch (RemoteException e) {
				e.printStackTrace();
			}

			listenerBenachrichtigen(new GameControlEvent(spielerVw.getAktiverSpieler(), GameControlEvent.phasen.STARTEN));
		}
	}


	public void geladenesSpielStarten(int anzahlSpieler) throws RemoteException {
		if(spielerVw.getSpielerList().size() == anzahlSpieler){

			try {
				farbenVerteilen();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			listenerBenachrichtigen(new GameControlEvent(spielerVw.getAktiverSpieler(), GameControlEvent.phasen.STARTEN));
		}
	}

	/**
	 * @param anzahlSpieler
	 */
	public void laenderAufteilen() {		
		List<Spieler> spielerListe = spielerVw.getSpielerList();
		weltVw.laenderAufteilen(spielerListe);		
	}

	/**
	 * @param spieler
	 * @return int
	 */
	public int bekommtEinheiten(Spieler spieler) {
		return kriegsVw.bekommtEinheiten(spieler);
	}

	/**
	 * @param spieler
	 * @return List<Land>
	 */
	public ArrayList<Land> besitztLaender(Spieler spieler) {
		return weltVw.besitztLaender(spieler);
	}

	/**
	 * Leitet die Phase aus der Kriegsverwaltung weiter
	 * @return phasen
	 */
	public phasen getTurn(){
		return kriegsVw.getTurn();
	}
	/**
	 * Ruft nextTurn in der KriegsVerwaltung auf
	 * @throws RemoteException 
	 */
	public void nextTurn() throws RemoteException{
		kriegsVw.nextTurn();
		GameControlEvent.phasen phaseEvent = null;

//		switch(kriegsVw.getTurn()){
//		case STARTPHASE:
//			phaseEvent = GameControlEvent.phasen.ANGRIFF;
//			break;
//		case VERSCHIEBEN:
//			phaseEvent = GameControlEvent.phasen.VERTEILEN;
//			break;
//		case ANGRIFF:
//			phaseEvent = GameControlEvent.phasen.VERSCHIEBEN;
//			break;
//		case VERTEILEN:
//			phaseEvent = GameControlEvent.phasen.ANGRIFF;
//			break;
		switch(kriegsVw.getTurn()){
		case STARTPHASE:
			phaseEvent = GameControlEvent.phasen.VERTEILEN;
			break;
		case VERSCHIEBEN:
			phaseEvent = GameControlEvent.phasen.VERSCHIEBEN;
			break;
		case ANGRIFF:
			phaseEvent = GameControlEvent.phasen.ANGRIFF;
			break;
		case VERTEILEN:
			phaseEvent = GameControlEvent.phasen.VERTEILEN;
			break;
		}
		listenerBenachrichtigen(new GameControlEvent(spielerVw.getAktiverSpieler(), phaseEvent));
	}

	/**
	 * Leitet Spieler aus der Spielerverwaltung weiter
	 * @return Spieler
	 */
	public Spieler getAktiverSpieler(){
		return spielerVw.getAktiverSpieler();
	}
	/**
	 * Ruft nächsterSpieler in der Spielerverwaltung auf 
	 */
	public void naechsterSpieler(){
		spielerVw.naechsterSpieler();
	}

	/**
	 * Leitet das Land aus stringToLand weiter
	 * @param angriffsLandString
	 * @return Land
	 */
	public Land stringToLand(String angriffsLandString) {
		return weltVw.stringToLand(angriffsLandString);
	}

	/**
	 * 
	 * @param anzahl
	 * @param land
	 * @throws RemoteException 
	 */
	public void einheitenPositionieren(int anzahl, Land land) throws RemoteException {
		System.out.println(land.getName() + "<-------------------------------");
		kriegsVw.einheitenPositionieren(1, weltVw.stringToLand(land.getName()));
		System.out.println(land.getEinheiten());
		for(Land l : weltVw.getLaenderListe()){
			System.out.println(l.getName() + l.getEinheiten()+"");
		}
		listenerBenachrichtigen(new GameActionEvent("", spielerVw.getAktiverSpieler(), GameActionEvent.GameActionEventType.VERTEILEN));
	}

	/**
	 * 
	 * @param land
	 * @return
	 */
	public ArrayList<Land> moeglicheAngriffsziele(Land land) {
		return kriegsVw.moeglicheAngriffsziele(land);
	}

	/**
	 * 
	 * @param angriff
	 * @return
	 * @throws KeinNachbarlandException
	 * @throws RemoteException 
	 */
	public AngriffRueckgabe befreiungsAktion(Angriff angriff) throws KeinNachbarlandException, RemoteException {
		listenerBenachrichtigen(new GameActionEvent(angriff.getAngriffsland().getBesitzer().getName() + " hat " + angriff.getVerteidigungsland().getBesitzer().getName() + " angegriffen.", spielerVw.getAktiverSpieler(), GameActionEvent.GameActionEventType.ANGRIFF));
		return kriegsVw.befreiungsAktion(angriff);
	}

	public ArrayList<Spieler> getSpielerList() {
		return spielerVw.getSpielerList();
	}

	public boolean istNachbar(Land wahlLand, Land landZiel, Spieler spieler) throws KeinNachbarlandException{
		return kriegsVw.istNachbar(wahlLand ,landZiel, spieler);
	}

	public void eroberungBesetzen(Land aLand, Land vLand, int einheiten)throws RemoteException{
		kriegsVw.eroberungBesetzen(weltVw.stringToLand(aLand.getName()),weltVw.stringToLand(vLand.getName()), einheiten);
		String text = "Der Spieler " + spielerVw.getAktiverSpieler() + " hat das Land " + vLand.getName() + " erobert.";
		listenerBenachrichtigen(new GameActionEvent(text, spielerVw.getAktiverSpieler(), GameActionEvent.GameActionEventType.EROBERT));


	}

	public boolean landWaehlen(String land, Spieler spieler) throws KannLandNichtBenutzenException{
		return kriegsVw.landWaehlen(land,spieler);
	}

	public boolean checkEinheiten(String land, int einheiten) throws NichtGenugEinheitenException{
		return kriegsVw.checkEinheiten(land,einheiten);
	}

	public ArrayList<Land> eigeneAngriffsLaender(Spieler spieler){
		return weltVw.eigeneAngriffsLaender(spieler);
	}

	public boolean landExistiert(String land) throws LandExistiertNichtException{
		return weltVw.landExistiert(land);
	}

	public boolean istGegner(String land,Spieler spieler) throws KeinGegnerException{
		return weltVw.istGegner(land, spieler);
	}

	public ArrayList<Land> moeglicheVerschiebeZiele(Land land, Spieler spieler){
		return kriegsVw.moeglicheVerschiebeZiele(land, spieler);
	}

	public boolean benutzeLaender(Land land) throws LandBereitsBenutztException{
		return kriegsVw.benutzeLaender(land);
	}

	public void landBenutzen(Land land){
		kriegsVw.landBenutzen(land);
	}

	public void benutzteLaenderLoeschen(){
		kriegsVw.benutzteLaenderLoeschen();
	}

	public ArrayList<Land> eigeneVerschiebeLaender(Spieler spieler){
		return weltVw.eigeneVerschiebeLaender(spieler, kriegsVw.getBenutzteLaenderListe());
	}

	public boolean checkEinheitenVerteilen(int einheiten,int veinheiten, Spieler spieler) throws KannEinheitenNichtVerschiebenException{
		return kriegsVw.checkEinheitenVerteilen(einheiten, veinheiten ,spieler);
	}

	public String einheitenAusgabe(Land erstesLand, Land zweitesLand){
		return weltVw.einheitenAusgabe(erstesLand, zweitesLand);
	}

	public void missionenVerteilen(){
		missionVw.missionenVerteilen(spielerVw.getSpielerList());
	}

	public String missionAusgeben(Spieler spieler){
		return missionVw.missionAusgeben(spieler);
	}

	public void missionsListeErstellen() throws IOException{
		missionVw.missionsListeErstellen(weltVw.getLaenderListe(), weltVw.getKontinentenListe(), spielerVw.getSpielerList());
	}

	public ArrayList<Land> getLaenderListe(){
		return weltVw.getLaenderListe();
	}

	public void spielSpeichern(String datei) throws IOException{
		kriegsVw.spielSpeichern(datei);
	}

	public void spielLaden(String datei) throws IOException, SpielerExistiertBereitsException{
		kriegsVw.spielLaden(datei);
	}

	public Einheitenkarten einheitenKarteZiehen(Spieler spieler) {
		return einheitenVw.karteNehmen(kriegsVw.spielerServerVerbindung(spieler));	
	}

	public boolean missionIstAbgeschlossen(Mission mission){
		return mission.istAbgeschlossen();
	}

	public boolean spielerRaus(Spieler spieler){
		return kriegsVw.spielerRaus(spieler);
	}

	public int kartenEinloesen(Spieler spieler, List<String> tauschKarten){
		return einheitenVw.einheitenkartenEinloesen(spieler,tauschKarten);
	}

	public void laenderErstellen() throws IOException{
		weltVw.laenderErstellen();
	}

	public void laenderverbindungenUndKontinenteErstellen(){
		weltVw.erstellen();
	}

	public Mission getSpielerMission(Spieler spieler){
		return missionVw.getSpielerMission(spieler);
	}

	public ArrayList<Mission> getMissionsListe(){
		return missionVw.getMissionsListe();
	}

	public String getLandVonFarbcode(String farbe){
		return weltVw.getLandVonFarbcode(farbe);
	}

	public void setTurn(String phase)	{
		kriegsVw.setTurn(phase);
	}

	public int checkAnfangsEinheiten()	{
		return kriegsVw.checkAnfangseinheiten();
	}

	//	public Mission getMissionVonAktivemSpieler()throws RemoteException	{
	//		return kriegsVw.getMissionVonAktivemSpieler();
	//	}

	public int kartenEinloesen(Spieler spieler, ArrayList<String> tauschKarten) throws RemoteException {
		return 0;
	}
	@Override
	public Spieler getSpielerVonIndex(int index) throws RemoteException {
		return spielerVw.getSpieler(index+1);
	}
	@Override
	public Land getLandVonIndex(int index) throws RemoteException {
		return weltVw.getLandVonIndex(index);
	}
	@Override
	public void setPlayerList(ArrayList<Spieler> liste) throws RemoteException {
		spielerVw.setSpielerList(liste);

	}

	public void farbenVerteilen()throws RemoteException {
		List<String> farben = new Vector<String>();
		farben.add("rot");
		farben.add("gruen");
		farben.add("blau");
		farben.add("gelb");
		farben.add("orange");
		farben.add("cyan");
		for (Spieler s : spielerVw.getSpielerList()) {
			s.setFarbe(farben.get(0));
			farben.remove(0);
		}


	}
	@Override
	public Mission getMissionVonSpieler(Spieler spieler) throws RemoteException {
		return kriegsVw.getMissionVonSpieler(spieler);
	}
	@Override
	public void spielerBereit() throws RemoteException {
		bereitZaehler++;
		if(bereitZaehler == spielerVw.getSpielerList().size()){
			listenerBenachrichtigen(new GameControlEvent(spielerVw.getAktiverSpieler(), GameControlEvent.phasen.ALLE_BEREIT));
		}

	}
	@Override
	public int getAktiverSpielerNummer() throws RemoteException{
		return spielerVw.getAktiverSpielerNummer();
	}
	@Override
	public void spielerErstellen(String spieler) throws RemoteException {
		try {
			spielerVw.neuerSpieler(spieler);
		} catch (SpielerExistiertBereitsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	@Override
	public void landErstellen(ArrayList<String> land) throws RemoteException {

		for(Spieler s : spielerVw.getSpielerList()){
			if(s.getName().equals(land.get(1))){
				weltVw.getLaenderListe().add(new Land(land.get(0),s,Integer.parseInt(land.get(2)),land.get(3),Integer.parseInt(land.get(4)),Integer.parseInt(land.get(5))));
			}


		}
	}
	@Override
	public void setAktiverSpielerNummer(int nummer) throws RemoteException {
		spielerVw.setAktiverSpieler(nummer);
		
	}
	@Override
	public void erstelleSpieler(String name) throws RemoteException, SpielerExistiertBereitsException {
		spielerVw.neuerSpieler(name);
		this.anzahlSpieler = anzahlSpieler;
		if(spielerVw.getSpielerList().size() == anzahlSpieler){
			try {
				laenderErstellen();
				laenderverbindungenUndKontinenteErstellen();
				missionsListeErstellen();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.WARNING_MESSAGE);
			}
			missionenVerteilen();
			laenderAufteilen();

			try {
				farbenVerteilen();
			} catch (RemoteException e) {
				e.printStackTrace();
			}

			listenerBenachrichtigen(new GameControlEvent(spielerVw.getAktiverSpieler(), GameControlEvent.phasen.STARTEN));
		}
		
	}
	




	}