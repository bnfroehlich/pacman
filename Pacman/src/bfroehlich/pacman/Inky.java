package bfroehlich.pacman;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;

public class Inky extends Ghost {
	
	private Blinky blinky;

	public Inky(Dimension size, Board board, Blinky blinky) {
		super(size, board);
		this.blinky = blinky;
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
				return new Point(8, 14);
			}
		}
		if(mode == Mode.SCATTER) {
			return new Point(26, 31);
		}
		Pacman pacman = board.getPacman();
		Direction pacmanDir = pacman.getDirection();
		Point tile = board.getTile(pacman.getVector(), pacman.getDistance());
		for(int i = 0; i < 2; i++) {
			tile = board.getNeighboringTile(tile, pacmanDir);
		}
		if(pacmanDir == Direction.NORTH) {
			for(int i = 0; i < 2; i++) {
				tile = board.getNeighboringTile(tile, Direction.WEST);
			}
		}
		Point blinkyTile = board.getTile(blinky.getVector(), blinky.getDistance());
		tile = new Point(tile.x + (tile.x - blinkyTile.x), tile.y + (tile.y - blinkyTile.y));
		return tile;
	}
	
	public Color getColor() {
		return Color.blue;
	}
}