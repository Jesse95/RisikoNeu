package local.valueobjects;

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
	
	public String hatGewonnen()	{
		if (verlusteVerteidiger < verlusteAngreifer){
			return "V";
		}else if(verlusteVerteidiger > verlusteAngreifer){
			return "A";
		}else{
			return "U";
		}
	}
	
	public int getVerlusteVerteidiger() {
		return verlusteVerteidiger;
	}
	
	public void setVerlusteVerteidiger(int verlusteVerteidiger) {
		this.verlusteVerteidiger = verlusteVerteidiger;
	}
	
	public int getVerlusteAngreifer() {
		return verlusteAngreifer;
	}
	
	public void setVerlusteAngreifer(int verlusteAngreifer) {
		this.verlusteAngreifer = verlusteAngreifer;
	}
	
	public List<Integer> getWuerfelVerteidiger() {
		return wuerfelVerteidiger;
	}
	
	public void setWuerfelVerteidiger(ArrayList<Integer> wuerfelVerteidiger) {
		this.wuerfelVerteidiger = wuerfelVerteidiger;
	}
	
	public ArrayList<Integer> getWuerfelAngreifer() {
		return wuerfelAngreifer;
	}
	
	public void setWuerfelAngreifer(ArrayList<Integer> wuerfelAngreifer) {
		this.wuerfelAngreifer = wuerfelAngreifer;
	}
	
	public boolean isErobert() {
		return erobert;
	}
	
	public void setErobert(boolean erobert) {
		this.erobert = erobert;
	}
}
