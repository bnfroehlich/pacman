package bfroehlich.pacman;

import java.awt.Color;
import java.awt.Graphics;

public class PowerPellet extends Food {

	public PowerPellet(Vector v, int d) {
		super(v, d);
	}
	
	public int points() {
		return 50;
	}

	public void paint(Graphics g) {
		g.setColor(new Color(248, 176, 144));
		g.fillOval(-5, -5, 10, 10);
	}
}