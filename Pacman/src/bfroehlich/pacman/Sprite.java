package bfroehlich.pacman;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

public abstract class Sprite extends Component {

	protected Board board;
	protected Direction direction;
	protected Dimension size;
	
	protected Oscillator oscillator;
	
	public Sprite(Dimension size, Board board) {
		this.board = board;
		this.size = size;
		loadImages();
		oscillator = new Oscillator(getNumImages(), getImageDuration());
	}
	
	protected abstract void loadImages();
	
	protected abstract int getNumImages();
	
	protected long getImageDuration() {
		return 35000000;
	}
	
	protected abstract Image getImage();
	
	public Direction getDirection() {
		return direction;
	}
	
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	
	public void update(long nanosElapsed) {
		super.update(nanosElapsed);
		move(getSpeed());
	}
	
	public void move(double value) {
		if(direction.isPositive()) {
			distance += value;
		}
		else if(direction.isNegative()) {
			distance -= value;
		}
	}
	
	public int getPossibleMovement() {
		int speed = ((int) getSpeed());
		if(speed == getSpeed()) {
			return speed;
		}
		return speed + 1;
	}
	
	public double getSpeed() {
		return 1.0*getSpeedMultiplier();
	}
	
	protected double getSpeedMultiplier() {
		return 1.0;
	}
	
	public AffineTransform getTransform(Image image) {
		return new AffineTransform();
	}
	
	public void paint(Graphics g) {
		Image image = getImage();
		if(image != null) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			
			AffineTransform trans = g2.getTransform();
			AffineTransform translate = new AffineTransform();
			translate.translate(-size.width/2, -size.height/2);
			trans.concatenate(translate);
			trans.concatenate(getTransform(image));
			
			g2.setTransform(trans);
			g2.drawImage(image, 0, 0, board);
		}
		else {
			g.setColor(Color.BLUE);
			g.fillOval(-15, -15, 30, 30);
		}
	}
}