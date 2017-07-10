package local.valueobjects;

import java.io.Serializable;

public class Angriff implements Serializable{
	Land angriffsland;
	Land verteidigungsland;
	
	public Angriff(Land angriffsland, Land verteidigungsland) {
		super();
		this.angriffsland = angriffsland;
		this.verteidigungsland = verteidigungsland;
	}
	
	public Land getAngriffsland() {
		return angriffsland;
	}
	
	public void setAngriffsland(Land angriffsland) {
		this.angriffsland = angriffsland;
	}
	
	public Land getVerteidigungsland() {
		return verteidigungsland;
	}
	
	public void setVerteidigungsland(Land verteidigungsland) {
		this.verteidigungsland = verteidigungsland;
	}
}
