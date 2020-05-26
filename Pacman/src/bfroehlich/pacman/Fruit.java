package bfroehlich.pacman;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

public class Fruit extends Food {
	
	private Dimension size;
	private Image image;
	private int points;
	
	public Fruit(Vector v, int dist, Dimension size, String imagePath, int points) {
		super(v, dist);
		this.size = size;
		this.image = Manager.loadImage(imagePath, size);
		this.points = points;
	}
	
	public int points() {
		return points;
	}
	
	public Dimension getSize() {
		return size;
	}
	
	public void paint(Graphics g) {
		if(image != null) {
			g.drawImage(image, -size.width/2, -size.height/2, null);
		}
		else {
			g.setColor(Color.BLUE);
			g.fillOval(-size.width/2, -size.height/2, size.width, size.height);
		}
	}

}
