package valueobjects;

import java.io.Serializable;
import java.util.ArrayList;

public class Kontinent implements Serializable{

	private ArrayList<Land> laender;
	private String name;

	public Kontinent(String name, ArrayList<Land> laender) {
		this.setName(name);
		this.laender = laender;
	}

	public String toString() {
		return name;
	}


	/** Gibt die Länder des Kontintenten zurück
	 * @return
	 */
	public ArrayList<Land> getLaender() {
		return laender;
	}

	/**Setzt die Länder des Kontinenten.
	 * @param laender
	 */
	public void setLaender(ArrayList<Land> laender) {
		this.laender = laender;
	}

	/**Gibt Namen des Kontinenten zurück
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**Setzt den Namen des Kontinenten.
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
}
