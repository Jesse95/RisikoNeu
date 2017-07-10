package local.valueobjects;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

public class Spieler implements Serializable{
	
	private String farbe;
	private String name;
	private List<Einheitenkarten> einheitenkarten;
	
	public Spieler(String name){
		this.name = name;
		this.einheitenkarten = new Vector<Einheitenkarten>();
	}
	public String getName(){
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public void karteNehmen(Einheitenkarten karte)
	{
		einheitenkarten.add(karte);
	}
	
	public List<Einheitenkarten> getEinheitenkarten() {
		return einheitenkarten;
	}
	
	public void setEinheitenkarten(List<Einheitenkarten> einheitenkarten) {
		this.einheitenkarten = einheitenkarten;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof Spieler) {
			Spieler andererSpieler = (Spieler) obj;
			return this.name.equals(andererSpieler.getName());
		}
		return false;
	}
	
	public void setFarbe(String farbe){
		this.farbe = farbe;
	}
	
	public String getFarbe(){
		return this.farbe;
	}
}
