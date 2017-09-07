package client;

import java.awt.Font;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class ButtonPanel extends JPanel{
	private ButtonClickHandler handler = null;
	private JButton nextTurn;
	private JLabel aLand;
	private JLabel vLand;
	private JButton angreifen;
	private JLabel land1;
	private JLabel land2;
	private JTextField anzahlEinheitenVerschieben;
	private JButton verschieben;
	private JButton verschiebenNA;
	private JLabel anzahlEinheitenVerteilen;
	private Font font;
	
	public interface ButtonClickHandler {
		public void phaseButtonClicked() throws RemoteException;
		public void angriffButtonClicked();
		public void verschiebenButtonClicked(int einheiten);
		public void verschiebenNachAngriffButtonClicked(int einheiten);
	}
	
	public ButtonPanel(ButtonClickHandler handler, Font font){
		this.handler = handler;
		this.font = font;
		initialize();	
	}
	
	public void initialize(){
		this.setLayout(new MigLayout("wrap1","[160]","[][]"));
		
		//Angreifen
		this.aLand = new JLabel("aLand");
		this.vLand = new JLabel("vLand");
		this.angreifen = new JButton("Angreifen");
				
		//Verschieben
		this.land1 = new JLabel("land1");
		this.land2 = new JLabel("land2");
		this.anzahlEinheitenVerschieben = new JTextField();
		this.verschieben = new JButton("Verschieben");
		this.verschiebenNA = new JButton("Verschieben");
		
		//Verteilen
		this.anzahlEinheitenVerteilen = new JLabel("anzahl Einheiten");
		this.anzahlEinheitenVerteilen.setFont(font);
		nextTurn = new JButton("Naechster Spieler");
		
		nextTurn.addActionListener(next -> {
			try {
				handler.phaseButtonClicked();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		angreifen.addActionListener(angriff -> handler.angriffButtonClicked());
		verschieben.addActionListener(verschieben -> handler.verschiebenButtonClicked(Integer.parseInt(anzahlEinheitenVerschieben.getText())));
		verschiebenNA.addActionListener(verschiebenNA -> handler.verschiebenNachAngriffButtonClicked(Integer.parseInt(anzahlEinheitenVerschieben.getText())));
		nextTurn.setEnabled(false);
		//TODO hier muss Anzahl der Einheiten am Anfang rein
//		startphase(3);
	}
	
	public void angreifenAktiv(String angriffsLand,String verteidigungsLand) {
		angreifen.setEnabled(false);
		removeAll();
		this.add(aLand,"left,grow");
		aLand.setText(angriffsLand);
		this.add(vLand,"left,grow");
		vLand.setText(verteidigungsLand);
		this.add(angreifen,"left,grow");
		this.add(nextTurn,"left,grow");
		nextTurn.setText("Naechste Phase");
		this.repaint();
	}
	
	public void verschiebenNachAngreifenAktiv(String erstesLand, String zweitesLand) {
		removeAll();
		this.add(land1,"left,grow");
		land1.setText(erstesLand);
		this.add(land2,"left,grow");
		land2.setText(zweitesLand);
		this.add(anzahlEinheitenVerschieben,"left,grow");
		this.add(verschiebenNA,"left,grow");
	}
	
	public void verschiebenAktiv(String erstesLand, String zweitesLand)	{
		removeAll();
		this.add(land1,"left,grow");
		land1.setText(erstesLand);
		this.add(land2,"left,grow");
		land2.setText(zweitesLand);
		this.add(anzahlEinheitenVerschieben,"left,grow");
		this.add(verschieben,"left,grow");
		this.add(nextTurn,"left,grow");
		nextTurn.setText("Naechste Phase");
		verschieben.setEnabled(false);
		this.repaint();
	}
	
	public void verteilenAktiv(int einheiten) {		
		removeAll();
		System.out.println("verteilen in buttonpanel");
		this.add(anzahlEinheitenVerteilen,"center");
		anzahlEinheitenVerteilen.setText(einheiten + "");
		this.add(nextTurn,"left,grow");
		nextTurn.setText("Naechste Phase");
		this.repaint();
	}
	
	public void phaseDisable() {
		nextTurn.setEnabled(false);
	}
	
	public void phaseEnable() {
		nextTurn.setEnabled(true);
	}
	
	public void angriffEnable() {
		angreifen.setEnabled(true);
	}
	
	public void angriffDisable() {
		angreifen.setEnabled(false);
	}
	
	public void verschiebenEnabled() {
		verschieben.setEnabled(true);
	}
	
	public void verschiebenDisabled() {
		verschieben.setEnabled(false);
	}
	public void resetTextbox() {
		anzahlEinheitenVerschieben.setText("");
	}
	
	public void startphase(int einheiten) {
		removeAll();
		System.out.println("startphase in buttonpanel");
		this.add(anzahlEinheitenVerteilen,"center");
		anzahlEinheitenVerteilen.setText(0 + "");
//		this.add(nextTurn,"left,grow");
//		nextTurn.setEnabled(false);
		this.repaint();
	}
	
	public void setEinheitenVerteilenLab(int einheiten) {
		anzahlEinheitenVerteilen.setText(einheiten + "");
	}

	public void removeAll()	{
		this.remove(nextTurn);
		this.remove(aLand);
		this.remove(vLand);
		this.remove(angreifen);
		this.remove(land1);
		this.remove(land2);
		this.remove(anzahlEinheitenVerschieben);
		this.remove(verschieben);
		this.remove(anzahlEinheitenVerteilen);
		this.remove(verschiebenNA);
		this.repaint();
	}

}
