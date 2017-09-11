package valueobjects;

import java.io.Serializable;
import java.util.ArrayList;

public class Spieler implements Serializable{
	
	private String farbe;
	private String name;
	private ArrayList<Einheitenkarten> einheitenkarten;
	
	public Spieler(String name){
		this.name = name;
		this.einheitenkarten = new ArrayList<Einheitenkarten>();
	}
	/**Gibt den Spielernamen zurück.
	 * @return
	 */
	public String getName(){
		return this.name;
	}
	/**Setzt Spielernamen.
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**Fügt Karte zu den Einheitenkarten des Spieler hinzu.
	 * @param karte
	 */
	public void karteNehmen(Einheitenkarten karte)
	{
		einheitenkarten.add(karte);
	}
	
	/**Gibt die Einheitenkarten des Spieler zurück.
	 * @return
	 */
	public ArrayList<Einheitenkarten> getEinheitenkarten() {
		return einheitenkarten;
	}
	
	/**Setzt die Einheitenkarten des Spielers.
	 * @param einheitenkarten
	 */
	public void setEinheitenkarten(ArrayList<Einheitenkarten> einheitenkarten) {
		this.einheitenkarten = einheitenkarten;
	}
	
	/**
	 * Überprüft ob zwei Spieler den selben Namen haben.
	 */
	public boolean equals(Object obj) {
		if (obj instanceof Spieler) {
			Spieler andererSpieler = (Spieler) obj;
			return this.name.equals(andererSpieler.getName());
		}
		return false;
	}
	
	/** Setzt die Farbe des Spielers.
	 * @param farbe
	 */
	public void setFarbe(String farbe){
		this.farbe = farbe;
	}
	
	/**Gibt die Farbe des Spielers zurück.
	 * @return
	 */
	public String getFarbe(){
		return this.farbe;
	}
}
