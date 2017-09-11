package valueobjects;

import java.io.Serializable;

public abstract class Mission implements Serializable{

	protected String beschreibung;
	protected Spieler spieler;
	protected Spieler spieler2;
	private int id;
	private String art;

	public Mission(int id, String beschreibung, Spieler spieler, String art) {
		this.beschreibung = beschreibung;
		this.spieler = spieler;
		this.id = id;
		this.art = art;
	}
	
	/**Gibt Art der Mission zurück.
	 * @return
	 */
	public String getArt() {
		return art;
	}

	/**Setzt die Art der Mission.
	 * @param art
	 */
	public void setArt(String art) {
		this.art = art;
	}

	/**Gibt ID der Mission zurück.
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**Setzt ID der Mission.
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**Überprüft ob Mission abgeschlossen ist.
	 * @return
	 */
	public abstract boolean istAbgeschlossen();
	
	/**Gibt Missionsbeschreibung zurück.
	 * @return
	 */
	public String getBeschreibung() {
		return beschreibung;
	}

	/**Gibt den Spieler, dem die Mission gehört, zurück.
	 * @return
	 */
	public Spieler getSpieler() {
		return spieler;
	}

	/**Setzt den Besitzer der Mission.
	 * @param spieler
	 */
	public void setSpieler(Spieler spieler) {
		this.spieler = spieler;
	}
	
	/**Gibt den Spieler zurück, der bei einer Spielermission auszuschalten ist.
	 * @return
	 */
	public Spieler getSpieler2() {
		return spieler2;
	}
	
	/**Setzt den zweiten Spieler bei einer Spielermission.
	 * @param spieler
	 */
	public void setSpieler2(Spieler spieler) {
		this.spieler2 = spieler;
		
	}
}
