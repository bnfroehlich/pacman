package bfroehlich.pacman;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class Pacman extends Sprite {
	
	private static Image half;
	private static Image full;
	private static Image threeQuarters;
	private ArrayList<Double> data;
	private boolean eating;

	public Pacman(Dimension size, Board board) {
		super(size, board);
	}
	
	protected int getNumImages() {
		return 4;
	}
	
	protected void loadImages() {
		if(full == null) {
			full = Manager.loadImage("pacman1.png", this.size);
		}
		if(threeQuarters == null) {
			threeQuarters = Manager.loadImage("pacman2.png", this.size);
		}
		if(half == null) {
			half = Manager.loadImage("pacman3.png", this.size);
		}
	}
	
	public Image getImage() {
		int value = oscillator.retrieve();
		if(value == 0) {
			return full;
		}
		else if(value == 2) {
			return half;
		}
		return threeQuarters;
	}
	
	public Image getDefaultImage() {
		return half;
	}
	
	public AffineTransform getTransform(Image image) {
		AffineTransform at = new AffineTransform();
		at.rotate(this.direction.rotation(), image.getWidth(board)/2, image.getHeight(board)/2);
		return at;
	}
	
	public void setEating(boolean eating) {
		this.eating = eating;
	}
	
	public boolean isEating() {
		return eating;
	}
	
	public void setSpeedData(ArrayList<Double> data) {
		this.data = data;
	}
	
	protected double getSpeedMultiplier() {
		int index = 0;
		if(board.isGhostFrightened()) {
			index = 2;
		}
		if(eating) {
			index++;
		}
		return data.get(index);
	}
	
	public static Image getPacman() {
		return threeQuarters;
	}
}
