package local.valueobjects;

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
	
	public ArrayList<Einheitenkarten> getEinheitenkarten() {
		return einheitenkarten;
	}
	
	public void setEinheitenkarten(ArrayList<Einheitenkarten> einheitenkarten) {
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
