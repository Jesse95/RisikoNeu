package server;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;


public class serverGUI extends JFrame{


	
	public static void main(String[] args){
	//serverGUI gui = new serverGUI();

	}
	

	
	public serverGUI(){
		JFrame frame = new JFrame("Spiel starten");
		JPanel panel = new JPanel(new MigLayout("wrap2","[50][150]","[][]"));
		frame.setLocationRelativeTo(null);
		frame.setSize(200,100);
		frame.setBackground(Color.GRAY);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//Objekte erstellen
		JButton serverStart = new JButton("Server starten");
		JLabel portLab = new JLabel("Port:");
//		portText = new JTextField();
		
//		serverStart.addActionListener(start -> serverStarten(Integer.parseInt(portText.getText()),frame));
		
		panel.add(portLab,"right");
//		panel.add(portText,"left,growx");
		panel.add(serverStart,"center,spanx 2");
		frame.add(panel);
		frame.setVisible(true);
	}
	public void serverStarten(int port,JFrame frameStart){
		frameStart.dispose();
		JFrame frame = new JFrame("Server gestartet");
		frame.setLayout(new MigLayout("","[]","[]"));
		frame.setSize(100, 100);
		frame.setBackground(Color.GREEN);
		JLabel startText = new JLabel("Server gestartet");
		
		frame.add(startText);
		frame.setVisible(true);
		RisikoServer server = new RisikoServer(port);
		
	}


}
