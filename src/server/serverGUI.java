package server;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.rmi.NotBoundException;
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
import local.domain.exceptions.SpielerGibtEsNichtException;
import local.valueobjects.Angriff;
import local.valueobjects.AngriffRueckgabe;
import local.valueobjects.GameActionEvent;
import local.valueobjects.GameControlEvent;
import local.valueobjects.GameEvent;
import local.valueobjects.GameEventListener;
import local.valueobjects.Land;
import local.valueobjects.Mission;
import local.valueobjects.ServerRemote;
import local.valueobjects.Spieler;
import local.valueobjects.Spielstand;
import net.miginfocom.swing.MigLayout;
import server.AdminPanel.AdminPanelButtons;


public class serverGUI extends UnicastRemoteObject implements ServerRemote, AdminPanelButtons{

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
	private AdminPanel adminPanel;
	private Registry registry;
	private boolean spielGeladen = false;

	public static void main(String[] args) throws RemoteException{
		server = new serverGUI();
	}
	
	public void initialize( )throws RemoteException{
		startBtn = new JButton("Server starten");
		serverConsolePanel = new ConsolePanel();
		adminPanel = new AdminPanel(this);
		frame = new JFrame();
		
		//Läd und setzt Icon
		Image im = null;
	    try {
	    im = ImageIO.read(new File("./Bilder/server.png"));
	    } catch (IOException ex) {
	    }
	    frame.setIconImage(im);
	    
		frame.setLayout(new MigLayout("debug, wrap2", "[][200]", "[][][]"));
		frame.setSize(450, 450);
		frame.setTitle("Server");
		
		try {
			ampelRot = ImageIO.read(new File("./Bilder/ampel_Rot.png"));
			ampelGruen = ImageIO.read(new File("./Bilder/ampel_Gruen.png"));
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
		frame.add(serverConsolePanel,"spany 3");
		frame.add(ampel,"top,growx");
		frame.add(startBtn,"top,growx");
		frame.add(adminPanel,"top,center");
		
		
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		
	}
	
	public serverGUI( ) throws RemoteException{
		listeners = new Vector<>();
		
		initialize();
	}
	
	public void spieleranzahlSetzen(int anzahlSpieler) throws SpielerExistiertBereitsException, RemoteException {
		this.anzahlSpieler = anzahlSpieler;
	}

	public void spielaufbauWennSpieleranzahlErreicht() throws RemoteException, SpielerExistiertBereitsException {
		if(!spielGeladen){
			if(spielerVw.getSpielerList().size() == anzahlSpieler){

				adminPanel.listenSetzen(spielerVw.getSpielerList(), weltVw.getLaenderListe());
				frame.repaint();
				serverConsolePanel.textSetzen("Spiel wird erstellt");
				try {
					weltVw.laenderErstellen();
					weltVw.laenderverbindungenUndKontinenteErstellen();
					missionVw.missionsListeErstellen(weltVw.getLaenderListe(), weltVw.getKontinentenListe(), spielerVw.getSpielerList());
					missionVw.missionenVerteilen(spielerVw.getSpielerList());
					weltVw.laenderAufteilen(spielerVw.getSpielerList());
					spielerVw.farbenVerteilen();
					serverConsolePanel.textSetzen("Spiel wurde erstellt");
					adminPanel.startPanel();
				} catch (IOException e) {}

				listenerBenachrichtigen(new GameControlEvent(spielerVw.getAktiverSpieler(), GameControlEvent.phasen.STARTEN));

			}
		}else{
			anzahlSpieler++;
			if(spielerVw.getSpielerList().size() == anzahlSpieler){
				listenerBenachrichtigen(new GameControlEvent(spielerVw.getAktiverSpieler(), GameControlEvent.phasen.STARTEN));	
			}
		}
	}
	
	public void beiGeladenemSpielNaechstenListener() throws RemoteException {
			listenerBenachrichtigen(new GameControlEvent(spielerVw.getAktiverSpieler(), phaseZuEvent()));
	}
	
	public void spielaufbauMitSpielstand(Spielstand spielstand) throws RemoteException{
		anzahlSpieler++;
		serverConsolePanel.textSetzen("Spiel wird geladen");
		//this.anzahlSpieler = spielstand.getSpielerListe().size();
		
		for(Spieler s:spielstand.getSpielerListe()) {
			spielerVw.getSpielerList().add(s);
		}
		
		frame.repaint();
		
		for(Land land : spielstand.getLaenderListe()) {
			weltVw.getLaenderListe().add(land);
		}		
		weltVw.laenderverbindungenUndKontinenteErstellen();
		
		for(Mission mission : spielstand.getMissionsListe()) {
			missionVw.getMissionsListe().add(mission);
		}
		
		spielerVw.farbenVerteilen();
		spielerVw.setAktiverSpieler(spielstand.getAktiverSpielerNummer());

		
		
		kriegsVw.setTurn(spielstand.getAktuellePhase());

	}

	public int checkAnfangsEinheiten()	{
		return kriegsVw.checkAnfangseinheiten();
	}

	public void setTurn(String phase)	{
		kriegsVw.setTurn(phase);
	}
	
	public phasen getTurn(){
		return kriegsVw.getTurn();
	}

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
		this.spielerVw = new Spielerverwaltung();
		this.weltVw = new Weltverwaltung();
		this.missionVw = new Missionsverwaltung();
		this.einheitenVw = new Einheitenkartenverwaltung();
		this.kriegsVw = new Kriegsverwaltung(spielerVw, weltVw, missionVw);
		String serviceName = "GameServer";
			
			serverConsolePanel.textSetzen("Server starten....");
			try{
				registry = LocateRegistry.createRegistry(4711);
				
			}catch(RemoteException re){
				registry = LocateRegistry.createRegistry(4711);
			}
			registry.rebind(serviceName, server);
			ampelSchalten(true);
			serverConsolePanel.textSetzen("Server gestartet");
		
			startBtn.setEnabled(false);
	}

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

	public int bekommtEinheiten(Spieler spieler) {
		return kriegsVw.bekommtEinheiten(spieler);
	}

	public void nextTurn() throws RemoteException{
		kriegsVw.nextTurn();
		GameControlEvent.phasen phaseEvent = null;
		
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

	public Land stringToLand(String angriffsLandString) {
		return weltVw.stringToLand(angriffsLandString);
	}

	public void einheitenPositionieren(int anzahl, Land land) throws RemoteException {
		kriegsVw.einheitenPositionieren(anzahl, weltVw.stringToLand(land.getName()));
		listenerBenachrichtigen(new GameActionEvent("", spielerVw.getAktiverSpieler(), GameActionEvent.GameActionEventType.VERTEILEN));
	}

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

	public Spielstand spielLaden(String datei) throws IOException, SpielerExistiertBereitsException{
		spielGeladen  = true;
		return kriegsVw.spielLaden(datei);
	}

	public void einheitenKarteZiehen(Spieler spieler) {
		einheitenVw.karteNehmen(kriegsVw.spielerServerVerbindung(spieler));	
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

	public Mission getMissionVonSpieler(Spieler spieler) throws RemoteException {
		return kriegsVw.getMissionVonSpieler(spieler);
	}

	public void spielerBereit() throws RemoteException {
		if(!spielGeladen) {
		bereitZaehler++;
			if(bereitZaehler == spielerVw.getSpielerList().size()){
				setTurn("VERTEILEN");
				listenerBenachrichtigen(new GameControlEvent(spielerVw.getAktiverSpieler(), GameControlEvent.phasen.VERTEILEN));
			}
		}
	}

	public int getAktiverSpielerNummer() throws RemoteException{
		return spielerVw.getAktiverSpielerNummer();
	}

	public void spielerErstellen(String spieler) throws RemoteException, SpielerExistiertBereitsException, SpielerGibtEsNichtException {
		if(spielGeladen) {
			boolean spielerInListe = false;
			for(Spieler s : getSpielerList()) {
				if(s.getName().equals(spieler)) {
					spielerInListe = true;
				}
			}
			if(!spielerInListe) {
				throw new SpielerGibtEsNichtException();
			}
		} else {
			spielerVw.neuerSpieler(spieler);
		}
	}

	public void landErstellen(ArrayList<String> land) throws RemoteException {

		for(Spieler s : spielerVw.getSpielerList()){
			if(s.getName().equals(land.get(1))){
				weltVw.getLaenderListe().add(new Land(land.get(0),s,Integer.parseInt(land.get(2)),land.get(3),Integer.parseInt(land.get(4)),Integer.parseInt(land.get(5))));
			}


		}
	}

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

	
	public void einheitenSetzenBtn(String land, int einheiten) {
		Land landWahl = weltVw.stringToLand(land);
		landWahl.setEinheiten(einheiten);
		spielAktualisieren();
	}
	
	public void spielAktualisieren(){
		listenerBenachrichtigen(new GameControlEvent(spielerVw.getAktiverSpieler(), GameControlEvent.phasen.AKTUALISIEREN));
	}

	public void besitzerSetzenBtn(String land, String spieler) {
		for(Land l : weltVw.getLaenderListe()){
			if(l.getName().equals(land)){
				for(Spieler s : spielerVw.getSpielerList()){
					if(s.getName().equals(spieler)){
						l.setBesitzer(s);
						break;
					}
				}
			}
		}
		spielAktualisieren();
		adminPanel.laenderUndBesitzer();
		
	}


	public void phaseSetzenBtn(String phase) {
		kriegsVw.setTurn(phase);
		GameControlEvent.phasen phaseEvent = phaseZuEvent();
		
		listenerBenachrichtigen(new GameControlEvent(spielerVw.getAktiverSpieler(), phaseEvent));
		
	}


	public void spielBeenden(Spieler spieler) throws RemoteException {
		listenerBenachrichtigen(new GameControlEvent(spieler,GameControlEvent.phasen.BEENDEN));
		
		try {
			registry.unbind("GameServer");
			ampelSchalten(false);
			serverConsolePanel.textSetzen("Der Spieler " + spieler.getName() + " hat das Spiel beendet.");
		} catch (NotBoundException e) {
			serverConsolePanel.textSetzen("Server konnte nicht beendet werden " + e.getMessage());
		}
		
	}


	public void aktiverSpielerSetzenBtn(String spieler) {
		for(Spieler s : spielerVw.getSpielerList()){
			if(s.getName().equals(spieler)){
				int spielerNr = spielerVw.getSpielerList().indexOf(s);
				spielerVw.setAktiverSpieler(spielerNr);
			}
		}
		listenerBenachrichtigen(new GameControlEvent(spielerVw.getAktiverSpieler(), phaseZuEvent()));
		
	}
	
	public GameControlEvent.phasen phaseZuEvent(){
		switch(kriegsVw.getTurn()){
		case STARTPHASE:
			return GameControlEvent.phasen.VERTEILEN;
		case VERSCHIEBEN:
			return GameControlEvent.phasen.VERSCHIEBEN;
		case ANGRIFF:
			return GameControlEvent.phasen.ANGRIFF;
		case VERTEILEN:
			return GameControlEvent.phasen.VERTEILEN;
		default:
			return null;
		}
		
	}
	
	public boolean isSpielGeladen() throws RemoteException{
		return spielGeladen;
	}
}