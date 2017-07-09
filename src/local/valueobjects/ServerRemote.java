package local.valueobjects;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

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
	
	public void erstelleSpieler(String name) throws SpielerExistiertBereitsException, RemoteException;
	

	/**
	 * @param anzahlSpieler
	 */
	public void laenderAufteilen()throws RemoteException;
	
	/**
	 * @param spieler
	 * @return int
	 */
	public int bekommtEinheiten(Spieler spieler)throws RemoteException;
	
	/**
	 * @param spieler
	 * @return List<Land>
	 */
	public List<Land> besitztLaender(Spieler spieler)throws RemoteException;
	
	/**
	 * Leitet die Phase aus der Kriegsverwaltung weiter
	 * @return phasen
	 */
	public phasen getTurn()throws RemoteException;
	/**
	 * Ruft nextTurn in der KriegsVerwaltung auf
	 */
	public void nextTurn()throws RemoteException;
	
	/**
	 * Leitet Spieler aus der Spielerverwaltung weiter
	 * @return Spieler
	 */
	public Spieler getAktiverSpieler()throws RemoteException;
	/**
	 * Ruft nächsterSpieler in der Spielerverwaltung auf 
	 */
	public void naechsterSpieler()throws RemoteException;

	/**
	 * Leitet das Land aus stringToLand weiter
	 * @param angriffsLandString
	 * @return Land
	 */
	public Land stringToLand(String angriffsLandString) throws RemoteException;
	
	/**
	 * 
	 * @param anzahl
	 * @param land
	 */
	public void einheitenPositionieren(int anzahl, Land land)throws RemoteException;
	
	/**
	 * 
	 * @param land
	 * @return
	 */
	public List<Land> moeglicheAngriffsziele(Land land)throws RemoteException;
	
	/**
	 * 
	 * @param angriff
	 * @return
	 * @throws KeinNachbarlandException
	 */
	public AngriffRueckgabe befreiungsAktion(Angriff angriff) throws KeinNachbarlandException, RemoteException;
	
	public List<Spieler> getSpielerList() throws RemoteException;
	
	public boolean istNachbar(Land wahlLand, Land landZiel, Spieler spieler) throws KeinNachbarlandException, RemoteException;
	
	public void eroberungBesetzen(Land aLand, Land vLand, int einheiten)throws RemoteException;
	
	public boolean landWaehlen(String land, Spieler spieler) throws KannLandNichtBenutzenException, RemoteException;
	
	public boolean checkEinheiten(String land, int einheiten) throws NichtGenugEinheitenException, RemoteException;
	
	public List<Land> eigeneAngriffsLaender(Spieler spieler)throws RemoteException;
	
	public boolean landExistiert(String land) throws LandExistiertNichtException, RemoteException;
	
	public boolean istGegner(String land,Spieler spieler) throws KeinGegnerException, RemoteException;
	
	public List<Land> moeglicheVerschiebeZiele(Land land, Spieler spieler)throws RemoteException;
	
	public boolean benutzeLaender(Land land) throws LandBereitsBenutztException, RemoteException;
	
	public void landBenutzen(Land land)throws RemoteException;
	
	public void benutzteLaenderLoeschen()throws RemoteException;
	
	public List<Land> eigeneVerschiebeLaender(Spieler spieler)throws RemoteException;
	
	public boolean checkEinheitenVerteilen(int einheiten,int veinheiten, Spieler spieler) throws KannEinheitenNichtVerschiebenException, RemoteException;
	
	public String einheitenAusgabe(Land erstesLand, Land zweitesLand)throws RemoteException;
	
	public void missionenVerteilen()throws RemoteException;
	
	public String missionAusgeben(Spieler spieler)throws RemoteException;
	
	public void missionsListeErstellen() throws IOException, RemoteException;
	
	public List<Land> getLaenderListe()throws RemoteException;
	
	public void spielSpeichern(String datei) throws IOException, RemoteException;
	
	public void spielLaden(String datei) throws IOException, SpielerExistiertBereitsException, RemoteException;
	
	public Einheitenkarten einheitenKarteZiehen(Spieler spieler)throws RemoteException;
	
	public boolean missionIstAbgeschlossen(Mission mission)throws RemoteException;
	
	public boolean spielerRaus(Spieler spieler)throws RemoteException;

	public int kartenEinloesen(Spieler spieler, List<String> tauschKarten)throws RemoteException;
	
	public void laenderErstellen() throws IOException, RemoteException;
	
	public void laenderverbindungenUndKontinenteErstellen()throws RemoteException;
	
	public Mission getSpielerMission(Spieler spieler)throws RemoteException;
	
	public List<Mission> getMissionsListe()throws RemoteException;
	
	public String getLandVonFarbcode(String farbe)throws RemoteException;
	
	public void setTurn(String phase)throws RemoteException;
	
	public int checkAnfangsEinheiten()throws RemoteException;
	
	public Mission getMissionVonAktivemSpieler()throws RemoteException;

}
