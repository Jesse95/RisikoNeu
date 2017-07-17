package local.valueobjects;

import java.rmi.Remote;
import java.util.List;

public interface SpielerRemote extends Remote{
	public String getName();
	
	public void setName(String name);
	
	public void karteNehmen(Einheitenkarten karte);
	
	public List<Einheitenkarten> getEinheitenkarten();
	
	public void setEinheitenkarten(List<Einheitenkarten> einheitenkarten);
	
	public boolean equals(Object obj);
	
	public void setFarbe(String farbe);
	
	public String getFarbe();
}
