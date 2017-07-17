package server;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

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
import net.miginfocom.swing.MigLayout;


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
	private JFrame frame;
	private ConsolePanel serverConsolePanel;
	JLabel ampel = null;
	BufferedImage ampelRot;
	BufferedImage ampelGruen;
	private static ServerRemote server;
	private JButton startBtn;
	
	public static void main(String[] args) throws RemoteException{
		server = new serverGUI();
		
		
	}
	
	public void initialize( )throws RemoteException{
		startBtn = new JButton("Server starten");
		serverConsolePanel = new ConsolePanel();
		frame = new JFrame();
		frame.setLayout(new MigLayout("debug, wrap2", "[][]", "[][][]"));
//		frame.setSize(450,600);
		
		
		try {
			ampelRot = ImageIO.read(new File("./ampel_Rot.png"));
			ampelGruen = ImageIO.read(new File("./ampel_Gruen.png"));
			ampel = new JLabel(new ImageIcon(ampelRot.getScaledInstance(50, 100, Image.SCALE_FAST)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		startBtn.addActionListener(starten -> {
			try {
				serverStarten();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		frame.add(serverConsolePanel,"spany 2");
		frame.add(ampel,"top,growx");
		frame.add(startBtn,"top,growx");	
		
		frame.pack();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		
	}
	
	public serverGUI( ) throws RemoteException{
		listeners = new Vector<>();
		this.spielerVw = new Spielerverwaltung();
		this.weltVw = new Weltverwaltung();
		this.missionVw = new Missionsverwaltung();
		this.einheitenVw = new Einheitenkartenverwaltung();
		this.kriegsVw = new Kriegsverwaltung(spielerVw, weltVw, missionVw);
		initialize();
	}
	
	public void erstelleErstenSpieler(String name,int anzahlSpieler) throws SpielerExistiertBereitsException, RemoteException {
		spielerVw.neuerSpieler(name);
		this.anzahlSpieler = anzahlSpieler;
	}

	public void erstelleWeiterenSpielerUndSpielaufbau(String name) throws RemoteException, SpielerExistiertBereitsException {
		spielerVw.neuerSpieler(name);
		if(spielerVw.getSpielerList().size() == anzahlSpieler){
			serverConsolePanel.textSetzen("Spiel wird erstellt");
			try {
				weltVw.laenderErstellen();
				weltVw.laenderverbindungenUndKontinenteErstellen();
				missionVw.missionsListeErstellen(weltVw.getLaenderListe(), weltVw.getKontinentenListe(), spielerVw.getSpielerList());
				missionVw.missionenVerteilen(spielerVw.getSpielerList());
				weltVw.laenderAufteilen(spielerVw.getSpielerList());
				spielerVw.farbenVerteilen();
			} catch (IOException e) {}
			
			listenerBenachrichtigen(new GameControlEvent(spielerVw.getAktiverSpieler(), GameControlEvent.phasen.STARTEN));
		}
	}

	public int checkAnfangsEinheiten()	{
		return kriegsVw.checkAnfangseinheiten();
	}

	public void setTurn(String phase)	{
		kriegsVw.setTurn(phase);
	}
	
	/**
	 * Leitet die Phase aus der Kriegsverwaltung weiter
	 * @return phasen
	 */
	public phasen getTurn(){
		return kriegsVw.getTurn();
	}

	/**
	 * Leitet Spieler aus der Spielerverwaltung weiter
	 * @return Spieler
	 */
	public Spieler getAktiverSpieler(){
		return spielerVw.getAktiverSpieler();
	}
	
	public ArrayList<Spieler> getSpielerList() {
		return spielerVw.getSpielerList();
	}

	public ArrayList<Land> getLaenderListe(){
		return weltVw.getLaenderListe();
	}

	public void addGameEventListener(GameEventListener listener) throws RemoteException {
		listeners.add(listener);

	}
	
	public void serverStarten() throws RemoteException{
		String serviceName = "GameServer";
			Registry registry;
			serverConsolePanel.textSetzen("Server starten....");
			try{
				registry = LocateRegistry.createRegistry(4711);
				//				registry = LocateRegistry.getRegistry();
			}catch(RemoteException re){
				registry = LocateRegistry.createRegistry(4711);
			}
			registry.rebind(serviceName, server);
			ampelSchalten(true);
			serverConsolePanel.textSetzen("Server gestartet");
			startBtn.setEnabled(false);
	}
	
	//-------------Bis hier auf jedenn Fall alles genutzt---------------
	
	public void removeGameEventListener(GameEventListener listener) throws RemoteException {
		listeners.remove(listener);
	}
	
	private void listenerBenachrichtigen(GameEvent event){
		for (GameEventListener listener : listeners) {

			Thread t = new Thread(new Runnable() {

				public void run() {
					try {
						listener.handleGameEvent(event);
					} catch (RemoteException e) {}				
				}
			});
			t.start();
		}
	}

	public void geladenesSpielStarten(int anzahlSpieler) throws RemoteException {
		if(spielerVw.getSpielerList().size() == anzahlSpieler){
			serverConsolePanel.textSetzen("Spiel wird geladen");
			spielerVw.farbenVerteilen();

			listenerBenachrichtigen(new GameControlEvent(spielerVw.getAktiverSpieler(), GameControlEvent.phasen.STARTEN));
		}
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
		kriegsVw.einheitenPositionieren(anzahl, weltVw.stringToLand(land.getName()));
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

	public boolean istNachbar(Land wahlLand, Land landZiel) throws KeinNachbarlandException{
		return kriegsVw.istNachbar(wahlLand ,landZiel);
	}

	public void eroberungBesetzen(Land aLand, Land vLand, int einheiten)throws RemoteException{
		kriegsVw.eroberungBesetzen(weltVw.stringToLand(aLand.getName()),weltVw.stringToLand(vLand.getName()), einheiten);
		String text = "Der Spieler " + spielerVw.getAktiverSpieler() + " hat das Land " + vLand.getName() + " erobert.";
		listenerBenachrichtigen(new GameActionEvent(text, spielerVw.getAktiverSpieler(), GameActionEvent.GameActionEventType.EROBERT));


	}

	public boolean landWaehlen(Land land, Spieler spieler) throws KannLandNichtBenutzenException{
		return kriegsVw.landWaehlen(land,spieler);
	}

	public boolean checkEinheiten(Land land, int einheiten) throws NichtGenugEinheitenException{
		return kriegsVw.checkEinheiten(land,einheiten);
	}

	public ArrayList<Land> eigeneAngriffsLaender(Spieler spieler){
		return weltVw.eigeneAngriffsLaender(spieler);
	}

	public boolean landExistiert(String land) throws LandExistiertNichtException{
		return weltVw.landExistiert(land);
	}

	public boolean istGegner(Land land,Spieler spieler) throws KeinGegnerException{
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

	public String missionAusgeben(Spieler spieler){
		return missionVw.missionAusgeben(spieler);
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

	public int kartenEinloesen(Spieler spieler, ArrayList<String> tauschKarten){
		return einheitenVw.einheitenkartenEinloesen(kriegsVw.spielerServerVerbindung(spieler),tauschKarten);
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
	
	@Override
	public Mission getMissionVonSpieler(Spieler spieler) throws RemoteException {
		return kriegsVw.getMissionVonSpieler(spieler);
	}

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
	
	public void serverBenachrichtigung(String nachricht){
		serverConsolePanel.textSetzen(nachricht);
	}
	
	public void ampelSchalten(Boolean ampelAn) {
		if(ampelAn){
			ampel.setIcon(new ImageIcon(ampelGruen.getScaledInstance(50, 100, Image.SCALE_FAST)));
		} else {
			ampel.setIcon(new ImageIcon(ampelRot.getScaledInstance(50, 100, Image.SCALE_FAST)));
		}
		frame.repaint();
		frame.revalidate();
	}

}