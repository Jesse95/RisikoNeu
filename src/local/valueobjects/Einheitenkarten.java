package local.valueobjects;

public class Einheitenkarten {
	private String kartenwert;

	public Einheitenkarten(String kartenwert) {
		this.kartenwert = kartenwert;
	}

	public String getKartenwert() {
		return kartenwert;
	}

	public void setKartenwert(String kartenwert) {
		this.kartenwert = kartenwert;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof Einheitenkarten) {
			Einheitenkarten andereKarte = (Einheitenkarten) obj;
			return this.kartenwert == andereKarte.kartenwert;
		}
		return false;
	}
}
