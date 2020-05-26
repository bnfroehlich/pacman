package bfroehlich.pacman;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class Movie {

	private int movie;
	private ArrayList<Image> images;
	private Board board;

	public Movie(Board board, int movie) {
		this.board = board;
		this.movie = movie;
		images = new ArrayList<Image>();
		images.add(Manager.loadImage("killthepacman.jpg", new Dimension(300, 500)));
	}
	
	public AffineTransform getTransform(Image image) {
		return new AffineTransform();
	}
	
	public void paint(Graphics g) {
		Image image = images.get(0);
		if(image != null) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			
			AffineTransform trans = g2.getTransform();
			AffineTransform translate = new AffineTransform();
			translate.translate(-image.getWidth(board)/2, -image.getHeight(board)/2);
			trans.concatenate(translate);
			trans.concatenate(getTransform(image));
			
			g2.setTransform(trans);
			g2.drawImage(image, 220, 250, board);
		}
		else {
			g.setColor(Color.BLUE);
			g.fillOval(-15, -15, 30, 30);
		}
	}
}
