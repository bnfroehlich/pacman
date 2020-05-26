package bfroehlich.pacman;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

public class Blinky extends Ghost {
	
	private static HashMap<String, Image> images;
	
	private int elroy;
	private ArrayList<Double> elroyData;

	public Blinky(Dimension size, Board board) {
		super(size, board);
	}
	
	public void setElroy(int elroy) {
		this.elroy = elroy;
	}
	
	public void setElroyData(ArrayList<Double> elroyData) {
		this.elroyData = elroyData;
	}
	
	protected double getSpeedMultiplier() {
		if(elroy != 0 && mode.isNormal() && !board.getTunnel().contains(vector)) {
			return elroyData.get(elroy-1);
		}
		return super.getSpeedMultiplier();
	}
	
	public Point getTargetTile() {
		if(super.getTargetTile() != null) {
			return super.getTargetTile();
		}
		if(board.getHouse().contains(vector)) {
			if(released) {
				return new Point(11, 10);
			}
			else {
				return new Point(13, 14);
			}
		}
		if(mode == Mode.SCATTER && elroy == 0) {
			return new Point(24, -4);
		}
		Pacman pacman = board.getPacman();
		return board.getTile(pacman.getVector(), pacman.getDistance());
	}
	
	public Color getColor() {
		return Color.red;
	}
}