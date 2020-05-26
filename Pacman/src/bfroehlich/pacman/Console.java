package bfroehlich.pacman;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Console extends JFrame {

	public static Console console;
	
	private JTextArea text;
	
	public Console() {
		super("It's a league game, Smokey.");
		text = new JTextArea();
		JScrollPane scroll = new JScrollPane(text);
		scroll.setPreferredSize(new Dimension(300, 600));
		scroll.setMinimumSize(new Dimension(300, 600));
		setLayout(new FlowLayout());
		add(scroll);
		pack();
	}
	
	public static void staticPrint(String input) {
		if(console == null) {
			console = new Console();
			console.setVisible(true);
		}
		console.print(input);
	}
	
	public void print(String input) {
		text.setText(text.getText() + input + "\n");
		this.repaint();
	}
}
