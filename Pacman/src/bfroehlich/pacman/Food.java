package bfroehlich.pacman;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Food extends Component {
	
	public Food(Vector vector, int distance) {
		super(vector, distance);
	}
	
	public int points() {
		return 10;
	}
	
	public void paint(Graphics g) {
		g.setColor(new Color(248, 176, 144));
		g.fillOval(-2, -2, 4, 4);
	}
}