package local.valueobjects;

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


	public ArrayList<Land> getLaender() {
		return laender;
	}

	public void setLaender(ArrayList<Land> laender) {
		this.laender = laender;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
