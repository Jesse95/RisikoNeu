package local.valueobjects;

import java.io.Serializable;
import java.util.List;

public class Kontinent implements Serializable{

	private List<Land> laender;
	private String name;

	public Kontinent(String name, List<Land> laender) {
		this.setName(name);
		this.laender = laender;
	}

	public String toString() {
		return name;
	}


	public List<Land> getLaender() {
		return laender;
	}

	public void setLaender(List<Land> laender) {
		this.laender = laender;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
