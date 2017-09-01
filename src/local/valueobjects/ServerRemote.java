package local.valueobjects;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import local.domain.Kriegsverwaltung.phasen;
import local.domain.exceptions.KannEinheitenNichtVerschiebenException;
import local.domain.exceptions.KannLandNichtBenutzenException;
import local.domain.exceptions.KeinGegnerException;
import local.domain.exceptions.KeinNachbarlandException;
import local.domain.exceptions.LandBereitsBenutztException;
import local.domain.exceptions.LandExistiertNichtException;
import local.domain.exceptions.NichtGenugEinheitenException;
import local.domain.exceptions.SpielerExistiertBereitsException;

public interface ServerRemote extends Remote{
	
	public void addGameEventListener(GameEventListener listener) throws RemoteException;
	
	public void removeGameEventListener(GameEventListener listener) throws RemoteException;
	
	public void spieleranzahlSetzen(int anzahlSpieler) throws SpielerExistiertBereitsException, RemoteException;
	
	public void geladenesSpielStarten(int anzahlSpieler) throws RemoteException;

	public int bekommtEinheiten(Spieler spieler)throws RemoteException;
	
	public phasen getTurn()throws RemoteException;

	public void nextTurn()throws RemoteException;
	
	public Spieler getAktiverSpieler()throws RemoteException;

	public Land stringToLand(String angriffsLandString) throws RemoteException;
	
	public void einheitenPositionieren(int anzahl, Land land)throws RemoteException;

	public AngriffRueckgabe befreiungsAktion(Angriff angriff) throws KeinNachbarlandException, RemoteException;
	
	public ArrayList<Spieler> getSpielerList() throws RemoteException;
	
	public boolean istNachbar(Land wahlLand, Land landZiel) throws KeinNachbarlandException, RemoteException;
	
	public void eroberungBesetzen(Land aLand, Land vLand, int einheiten)throws RemoteException;
	
	public boolean landWaehlen(Land land, Spieler spieler) throws KannLandNichtBenutzenException, RemoteException;
	
	public boolean checkEinheiten(Land land, int einheiten) throws NichtGenugEinheitenException, RemoteException;
	
	public ArrayList<Land> eigeneAngriffsLaender(Spieler spieler)throws RemoteException;
	
	public boolean landExistiert(String land) throws LandExistiertNichtException, RemoteException;
	
	public boolean istGegner(Land land,Spieler spieler) throws KeinGegnerException, RemoteException;
	
	public ArrayList<Land> moeglicheVerschiebeZiele(Land land, Spieler spieler)throws RemoteException;
	
	public boolean benutzeLaender(Land land) throws LandBereitsBenutztException, RemoteException;
	
	public void landBenutzen(Land land)throws RemoteException;
	
	public void benutzteLaenderLoeschen()throws RemoteException;
	
	public ArrayList<Land> eigeneVerschiebeLaender(Spieler spieler)throws RemoteException;
	
	public boolean checkEinheitenVerteilen(int einheiten,int veinheiten, Spieler spieler) throws KannEinheitenNichtVerschiebenException, RemoteException;
	
	public String einheitenAusgabe(Land erstesLand, Land zweitesLand)throws RemoteException;
	
	public String missionAusgeben(Spieler spieler)throws RemoteException;
	
	public ArrayList<Land> getLaenderListe()throws RemoteException;
	
	public void spielSpeichern(String datei) throws IOException, RemoteException;
	
	public Spielstand spielLaden(String datei) throws IOException, SpielerExistiertBereitsException, RemoteException;
	
	public void einheitenKarteZiehen(Spieler spieler)throws RemoteException;
	
	public boolean missionIstAbgeschlossen(Mission mission)throws RemoteException;
	
	public boolean spielerRaus(Spieler spieler)throws RemoteException;

	public int kartenEinloesen(Spieler spieler, ArrayList<String> tauschKarten)throws RemoteException;
	
	public Mission getSpielerMission(Spieler spieler)throws RemoteException;
	
	public ArrayList<Mission> getMissionsListe()throws RemoteException;
	
	public String getLandVonFarbcode(String farbe)throws RemoteException;
	
	public void setTurn(String phase)throws RemoteException;
	
	public int checkAnfangsEinheiten()throws RemoteException;
	
	public Mission getMissionVonSpieler(Spieler spieler)throws RemoteException;
	
	public void spielerBereit()throws RemoteException;
	
	public int getAktiverSpielerNummer()throws RemoteException;
	
	public void spielerErstellen(String spieler)throws RemoteException, SpielerExistiertBereitsException;
	
	public void landErstellen(ArrayList<String> land)throws RemoteException;
	
	public void setAktiverSpielerNummer(int nummer)throws RemoteException;

	public void spielaufbauWennSpieleranzahlErreicht()throws RemoteException, SpielerExistiertBereitsException;

	public void serverStarten() throws RemoteException;
	
	public void serverBenachrichtigung(String nachricht) throws RemoteException;
	
	public void spielaufbauMitSpielstand(Spielstand spielstand) throws RemoteException;

}
