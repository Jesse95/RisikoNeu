package valueobjects;

import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;

import javax.swing.JLabel;

public class Land implements Serializable{
	private String name;
	private Spieler besitzer;
	private int einheiten;
	private String kurzel;
	private int fahneX;
	private int fahneY;
	private JLabel fahne;
	private JLabel einheitenLab = new JLabel();

	public Land(String name, Spieler besitzer, int einheiten, String kurzel, int fahneX, int fahneY) {
		this.name = name;
		this.besitzer = besitzer;
		this.einheiten = einheiten;
		this.kurzel = kurzel;
		this.fahneX = fahneX;
		this.fahneY = fahneY;
	}

	/**Gibt den Namen des Landes zurück.
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**Setzt den Namen des Landes.
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**Gibt den Besitzer des Landes zurück.
	 * @return
	 */
	public Spieler getBesitzer() {
		return besitzer;
	}

	/**Setzt den Besitzer des Landes.
	 * @param besitzer
	 */
	public void setBesitzer(Spieler besitzer) {
		this.besitzer = besitzer;
	}

	/**Gibt die Einheiten des Landes zurück.
	 * @return
	 */
	public int getEinheiten() {
		return einheiten;
	}

	/**Setzt die Einheiten des Landes.
	 * @param einheiten
	 */
	public void setEinheiten(int einheiten) {
		this.einheiten = einheiten;
	}
	/**Gibt Kürzel des Landes zurück.
	 * @return
	 */
	public String getKuerzel(){
		return kurzel;
	}
	
	/**Setzt die Fahne des Landes.
	 * @param x
	 * @param y
	 */
	public void setFahne(int x, int y){
		this.fahneX = x;
		this.fahneY = y;
	}
	
	/**Gibt die X-Koordinate der Fahne zurück.
	 * @return
	 */
	public int getFahneX(){
		return fahneX;
	}
	
	/**Gibt die Y-Koordinate der Fahne zurück.
	 * @return
	 */
	public int getFahneY(){
		return fahneY;
	}
	
	/**Zeigt auf der Fahne die Anzahl der Einheiten an.
	 * @return
	 */
	public JLabel getEinheitenLab(){
		einheitenLab.setText(einheiten + "");
		if(einheiten < 10){
			einheitenLab.setBounds(fahneX +30, fahneY -42, 40, 25);
		}else if(einheiten < 100){
			einheitenLab.setBounds(fahneX +23, fahneY -42, 40, 25);
		}else{
			einheitenLab.setBounds(fahneX +18, fahneY -42, 40, 25);
		}
		einheitenLab.setFont(new Font("Courier New", Font.BOLD, 20));
	    einheitenLab.setForeground(Color.WHITE);
		return einheitenLab;
	}
	
	/**Getter der Fahne.
	 * @return
	 */
	public JLabel getFahne(){
		return this.fahne;
	}
}
