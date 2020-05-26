package bfroehlich.pacman;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;

public class Clyde extends Ghost {
	
	public Clyde(Dimension size, Board board) {
		super(size, board);
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
				return new Point(17, 14);
			}
		}
		Point scatterTile = new Point(-1, 31);
		if(mode == Mode.SCATTER) {
			return scatterTile;
		}
		Pacman pacman = board.getPacman();
		Point pmTile = board.getTile(pacman.getVector(), pacman.getDistance());
		Point myTile = board.getTile(getVector(), getDistance());
		int away = board.tileDistanceSquared(myTile, pmTile);
		if(away < 64) {
			return scatterTile;
		}
		return pmTile;
	}
	
	public Color getColor() {
		return Color.orange;
	}
}
