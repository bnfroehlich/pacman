package bfroehlich.pacman;

import java.awt.Point;
import java.util.ArrayList;

public class Intersection {

	private Vector us;
	private Vector cross;
	private int distanceAt;
	private int distanceAlongCross;
	
	public Intersection(Vector us, Vector cross, int distanceAt, int distanceAlongCross) {
		this.us = us;
		this.cross = cross;
		this.distanceAt = distanceAt;
		this.distanceAlongCross = distanceAlongCross;
	}
	
	public Vector getVector() {
		return us;
	}

	public Vector getCross() {
		return cross;
	}

	public int getDistanceAt() {
		return distanceAt;
	}

	public int getDistanceAlongCross() {
		return distanceAlongCross;
	}
	
	public ArrayList<Direction> getOpenDirections() {
		ArrayList<Direction> open = new ArrayList<Direction>();
		if(us.isVertical()) {
			if(distanceAt > 0) {
				open.add(Direction.NORTH);
			}
			if(distanceAt < us.length()) {
				open.add(Direction.SOUTH);
			}
			if(distanceAlongCross > 0) {
				open.add(Direction.WEST);
			}
			if(distanceAlongCross < cross.length()) {
				open.add(Direction.EAST);
			}
		}
		else {
			if(distanceAt > 0) {
				open.add(Direction.WEST);
			}
			if(distanceAt < us.length()) {
				open.add(Direction.EAST);
			}
			if(distanceAlongCross > 0) {
				open.add(Direction.NORTH);
			}
			if(distanceAlongCross < cross.length()) {
				open.add(Direction.SOUTH);
			}
		}
		return open;
	}
}
