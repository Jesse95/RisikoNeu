package valueobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AngriffRueckgabe implements Serializable{
	private int verlusteVerteidiger;
	private int verlusteAngreifer;
	private ArrayList<Integer> wuerfelVerteidiger;
	private ArrayList<Integer> wuerfelAngreifer;
	private boolean erobert;

	public AngriffRueckgabe(int verlusteVerteidiger, int verlusteAngreifer, ArrayList<Integer> wuerfelVerteidiger, ArrayList<Integer> wuerfelAngreifer, boolean erobert) {
		this.verlusteVerteidiger = verlusteVerteidiger;
		this.verlusteAngreifer = verlusteAngreifer;
		this.wuerfelVerteidiger = wuerfelVerteidiger;
		this.wuerfelAngreifer = wuerfelAngreifer;
		this.erobert = erobert;
	}
	
	/** Gibt zurück ob Angreifer oder Verteidiger gewonnen hat, oder ob es unentschieden ausging.
	 * @return
	 */
	public String hatGewonnen()	{
		if (verlusteVerteidiger < verlusteAngreifer){
			return "V";
		}else if(verlusteVerteidiger > verlusteAngreifer){
			return "A";
		}else{
			return "U";
		}
	}
	
	/**Gibt die Verluste des Verteidigers zurück.
	 * @return
	 */
	public int getVerlusteVerteidiger() {
		return verlusteVerteidiger;
	}
	
	/**Setzt die Verluste des Verteidigers.
	 * @param verlusteVerteidiger
	 */
	public void setVerlusteVerteidiger(int verlusteVerteidiger) {
		this.verlusteVerteidiger = verlusteVerteidiger;
	}
	
	/**Gibt die Verluste des Angreifers zurück.
	 * @return
	 */
	public int getVerlusteAngreifer() {
		return verlusteAngreifer;
	}
	
	/**Setzt die Verluste des Angreifers.
	 * @param verlusteAngreifer
	 */
	public void setVerlusteAngreifer(int verlusteAngreifer) {
		this.verlusteAngreifer = verlusteAngreifer;
	}
	
	/**Gibt die Würfelergebnisse des Verteidigers zurück.
	 * @return
	 */
	public List<Integer> getWuerfelVerteidiger() {
		return wuerfelVerteidiger;
	}
	
	/**Setzt die Würfelergebnisse des Verteidigers.
	 * @param wuerfelVerteidiger
	 */
	public void setWuerfelVerteidiger(ArrayList<Integer> wuerfelVerteidiger) {
		this.wuerfelVerteidiger = wuerfelVerteidiger;
	}
	
	/**Gibt die Würfelergebnisse des Angreifers zurück.
	 * @return
	 */
	public ArrayList<Integer> getWuerfelAngreifer() {
		return wuerfelAngreifer;
	}
	
	/**Setzt die Würfelergebnisse des Angreifers.
	 * @param wuerfelAngreifer
	 */
	public void setWuerfelAngreifer(ArrayList<Integer> wuerfelAngreifer) {
		this.wuerfelAngreifer = wuerfelAngreifer;
	}
	
	/**Boolean ob ein Land erobert wurde.
	 * @return
	 */
	public boolean isErobert() {
		return erobert;
	}
	
	/**Setzt den Eroberungsstatus eines Landes.
	 * @param erobert
	 */
	public void setErobert(boolean erobert) {
		this.erobert = erobert;
	}
}
