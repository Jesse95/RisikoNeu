package valueobjects;

import java.io.Serializable;

public class Angriff implements Serializable{
	Land angriffsland;
	Land verteidigungsland;
	
	public Angriff(Land angriffsland, Land verteidigungsland) {
		super();
		this.angriffsland = angriffsland;
		this.verteidigungsland = verteidigungsland;
	}
	
	/** Gibt Angriffsland zurück
	 * @return
	 */
	public Land getAngriffsland() {
		return angriffsland;
	}
	
	/**Setzt das Angriffsland
	 * @param angriffsland
	 */
	public void setAngriffsland(Land angriffsland) {
		this.angriffsland = angriffsland;
	}
	
	/**Gibt das Verteidigungsland zurück
	 * @return
	 */
	public Land getVerteidigungsland() {
		return verteidigungsland;
	}
	
	/**Setzt das Vertedigungsland
	 * @param verteidigungsland
	 */
	public void setVerteidigungsland(Land verteidigungsland) {
		this.verteidigungsland = verteidigungsland;
	}
}
