package bfroehlich.pacman;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;

public class Pinky extends Ghost {

	public Pinky(Dimension size, Board board) {
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
				return new Point(12, 14);
			}
		}
		if(mode == Mode.SCATTER) {
			return new Point(2, -4);
		}
		Pacman pacman = board.getPacman();
		Direction pacmanDir = pacman.getDirection();
		Point tile = board.getTile(pacman.getVector(), pacman.getDistance());
		for(int i = 0; i < 4; i++) {
			tile = board.getNeighboringTile(tile, pacmanDir);
		}
		if(pacmanDir == Direction.NORTH) {
			for(int i = 0; i < 4; i++) {
				tile = board.getNeighboringTile(tile, Direction.WEST);
			}
		}
		return tile;
	}
	
	public Color getColor() {
		return Color.pink;
	}
}