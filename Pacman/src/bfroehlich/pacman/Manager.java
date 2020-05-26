package bfroehlich.pacman;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.text.ParseException;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Manager {

	private GameWindow window;
	private Board board;
	private Engine engine;
	
	public Manager() {
		window = new GameWindow(this, "His life is in your hands, dude.");
		board = window.getBoard();
		engine = window.getEngine();
		
		window.setVisible(true);
		SoundEngine.staticPlay("victory.wav");
		showMenu();
	}
	
	public static Image loadImage(String name, Dimension size) {
		if(name == null) {
			return null;
		}
		System.out.println(name);
		URL url = Manager.class.getResource("/" + name);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image image = toolkit.getImage(url);
        if(size != null) {
        	image = image.getScaledInstance(size.width, size.height, Image.SCALE_DEFAULT);
        }
        return image;
	}
	
	private void showMenu() {
		int x = JOptionPane.showConfirmDialog(window, new JLabel("I think I can.", new ImageIcon(loadImage("cherry.png", Board.SIZE)), JLabel.CENTER));
		if(x != 0) {
			System.exit(0);
		}
		else {
			engine.loadNewGame();
			engine.start();
		}
	}
	
	public void gameOver(int score) {
		try {
			if(score > getHighScore()) {
				writeFile(new File("highscore.txt"), "" + score);
			}
		}
		catch(Exception e) {
			System.err.println(e.getMessage());
		}
		showMenu();
	}

	public int getHighScore() {
		try {
			Integer highScore = Integer.parseInt(readFile(new File("highscore.txt")));
			if(highScore == null) {
				return 0;
			}
			return (int) highScore;
		}
		catch(Exception e) {
			System.err.println(e.getMessage());
			return 0;
		}
	}
	
	private String readFile(File file) throws FileNotFoundException {
		Scanner in = null;
		try {
			in = new Scanner(file);
			in.useDelimiter("$");
			return in.next();
		}
		finally {
		  in.close();
		}
	}
	
	private void writeFile(File location, String data) throws IOException, ParseException {
		Writer output = null;
		try {
			output = new BufferedWriter(new FileWriter(location));
			output.write(data);
		}
		finally {
		  output.close();
		}
	}
	
//	Vector up = new Vector(new Point(0, 0), new Dimension(100, 0));
//	Vector down = new Vector(new Point(0, 200), new Dimension(100, 0));
//	Vector left = new Vector(new Point(0, 0), new Dimension(0, 200));
//	Vector right = new Vector(new Point(100, 0), new Dimension(0, 200));
//	Vector vert = new Vector(new Point(50, 0), new Dimension(0, 200));
//	Vector horiz = new Vector(new Point(0, 100), new Dimension(100, 0));
//	
//	ArrayList<Intersection> upInt = new ArrayList<Intersection>();
//	upInt.add(new Intersection(left, 0, 0));
//	upInt.add(new Intersection(right, 100, 0));
//	upInt.add(new Intersection(vert, 50, 0));
//	up.setIntersections(upInt);
//	
//	ArrayList<Intersection> downInt = new ArrayList<Intersection>();
//	downInt.add(new Intersection(left, 0, 200));
//	downInt.add(new Intersection(right, 100, 200));
//	downInt.add(new Intersection(vert, 50, 200));
//	down.setIntersections(downInt);
//	
//	ArrayList<Intersection> leftInt = new ArrayList<Intersection>();
//	leftInt.add(new Intersection(up, 0, 0));
//	leftInt.add(new Intersection(down, 200, 0));
//	leftInt.add(new Intersection(horiz, 100, 0));
//	left.setIntersections(leftInt);
//	
//	ArrayList<Intersection> rightInt = new ArrayList<Intersection>();
//	rightInt.add(new Intersection(up, 0, 100));
//	rightInt.add(new Intersection(down, 200, 100));
//	rightInt.add(new Intersection(horiz, 100, 100));
//	right.setIntersections(rightInt);
//
//	ArrayList<Intersection> vertInt = new ArrayList<Intersection>();
//	vertInt.add(new Intersection(up, 0, 50));
//	vertInt.add(new Intersection(down, 200, 50));
//	vertInt.add(new Intersection(horiz, 100, 50));
//	vert.setIntersections(vertInt);
//	
//	ArrayList<Intersection> horizInt = new ArrayList<Intersection>();
//	horizInt.add(new Intersection(left, 0, 100));
//	horizInt.add(new Intersection(right, 100, 100));
//	horizInt.add(new Intersection(vert, 50, 100));
//	horiz.setIntersections(horizInt);
//	
//	path = new ArrayList<Vector>();
//	path.add(up);
//	path.add(down);
//	path.add(left);
//	path.add(right);
//	path.add(vert);
//	path.add(horiz);
	
}