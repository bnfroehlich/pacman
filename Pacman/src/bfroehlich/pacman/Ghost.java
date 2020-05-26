package bfroehlich.pacman;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public abstract class Ghost extends Sprite {
	
	protected Board board;
	protected Mode mode;
	protected Random rand;
	protected Oscillator warningOscillator;
	protected boolean released;
	
	protected ArrayList<Double> data;
	protected ArrayList<Double> modeChangeTimes;
	
	protected long nanosPassed;
	protected long lastUpdateNanos;
	protected long frightenedNanosRemaining;
	protected Mode unfrightenedMode;
	protected long frightenTime;
	
	protected static HashMap<String, Image> frightenedImages;
	protected HashMap<String, Image> images;
	
	public Ghost(Dimension size, Board board) {
		super(size, board);
		this.board = board;
		rand = new Random();
	}
	
	protected void loadImages() {
		if(frightenedImages == null) {
			frightenedImages = new HashMap<String, Image>();
			String[] paths = {"white1", "white2", "blue1", "blue2", "eyes_north", "eyes_south", "eyes_east", "eyes_west"};
			for(int i = 0; i < paths.length; i++) {
				if(frightenedImages.get(paths[i].toUpperCase()) == null) {
					String path = paths[i] + ".png";
					frightenedImages.put(paths[i].toUpperCase(), Manager.loadImage(path, this.size));
				}
			}
		}
		images = new HashMap<String, Image>();
		String[] names = {"north1", "north2", "south1", "south2", "west1", "west2", "east1", "east2"};
		for(int i = 0; i < names.length; i++) {
			if(images.get(names[i].toUpperCase()) == null) {
				String path = getClass().getSimpleName().toLowerCase() + "_" + names[i] + ".png";
				images.put(names[i].toUpperCase(), Manager.loadImage(path, this.size));
			}
		}
	}
	
	public Direction calculateNextDirection() {
		Intersection nextIntersection = vector.getNextIntersection(getDistance(), direction.isPositive());
		if(nextIntersection != null) {
			ArrayList<Direction> open = nextIntersection.getOpenDirections();
			open.remove(direction.reverse());
			Direction[] ranking = {Direction.NORTH, Direction.WEST, Direction.SOUTH, Direction.EAST};
			if(mode == Mode.FRIGHTENED && !board.getHouse().contains(vector)) {
				int random = rand.nextInt(4);
				if(open.contains(ranking[random])) {
					return ranking[random];
				}
				for(int i = 0; i < ranking.length; i++) {
					if(open.contains(ranking[i])) {
						return ranking[i];
					}
				}
				return null;
			}
			Point tile = board.getTile(nextIntersection.getVector(), nextIntersection.getDistanceAt());
			Point targetTile = getTargetTile();
			ArrayList<Direction> shortestPaths = new ArrayList<Direction>();
			int minDistSquared = 10000000;
			for(int i = 0; i < open.size(); i++) {
				Point neighbor = board.getNeighboringTile(tile, open.get(i));
				int distSquared = board.tileDistanceSquared(neighbor, targetTile);
				if(shortestPaths.size() == 0 || distSquared == minDistSquared) {
					minDistSquared = distSquared;
					shortestPaths.add(open.get(i));
				}
				else if(distSquared < minDistSquared) {
					minDistSquared = distSquared;
					shortestPaths.clear();
					shortestPaths.add(open.get(i));
				}
			}
			if(shortestPaths.size() == 1) {
				return shortestPaths.get(0);
			}
			//else: we have a tie
			for(int i = 0; i < ranking.length; i++) {
				if(shortestPaths.contains(ranking[i])) {
					return ranking[i];
				}
			}
		}
		if((direction.isPositive() && distance == vector.length()) || (!direction.isPositive() && distance == 0)) {
			return direction.reverse();
		}
		return direction;
	}
	
	protected Point getTargetTile() {
		if(mode == Mode.EYES){
			return new Point(13, 11);
		};
		return null;
	}
	
	protected double getSpeedMultiplier() {
		double multiplier = data.get(0);
		if(mode == Mode.FRIGHTENED) {
			multiplier = data.get(1);
		}
		else if(mode == Mode.EYES) {
			multiplier*=2;
		}
		else if(board.isTunnel(vector, (int) distance)) {
			multiplier = data.get(2);
		}
		return multiplier;
	}
	
	public void update(long nanosElapsed) {
		super.update(nanosElapsed);
		if(board.isAllGhostsNormal()) {
			if(modeChangeTimes.size() > 0) {
				//timer pauses while frightened
				nanosPassed += nanosElapsed;
				if((nanosPassed/1000000000) > modeChangeTimes.get(0)) {
					nanosPassed = 0;
					modeChangeTimes.remove(0);
					setMode(mode.standardSwitch());
					//System.out.println(getClass().getSimpleName() + " " + mode);
				}
			}
		}
		else if(mode == Mode.FRIGHTENED) {
			frightenedNanosRemaining -= nanosElapsed;
			if(frightenedNanosRemaining <= 0) {
				unfrighten();
			}
			else if(frightenedNanosRemaining < 2000000000 && warningOscillator == null) {
				warningOscillator = new Oscillator(2, 200000000);
			}
		}
		else if(mode == Mode.EYES) {
			if(board.getTile(vector, (int) distance).equals(getTargetTile())) {
				unfrighten();
				released = false;
			}
		}
	}
	
	public void setSpeedData(ArrayList<Double> data) {
		this.data = data;
	}
	
	public void setModeChangeTimes(ArrayList<Double> times) {
		this.modeChangeTimes = times;
		mode = Mode.SCATTER;
		nanosPassed = 0;
	}
	
	public Mode getMode() {
		return mode;
	}
	
	public void setMode(Mode newMode) {
		warningOscillator = null;
		if(newMode != mode) {
			if(mode.isNormal()) {
				setDirection(direction.reverse());
			}
			this.mode = newMode;
		}
	}
	
	public void frighten(long seconds) {
		frightenedNanosRemaining = seconds*1000000000;
		if(mode.isNormal()) {
			unfrightenedMode = mode;
		}
		setMode(Mode.FRIGHTENED);
	}
	
	public void capture() {
		if(mode == Mode.FRIGHTENED) {
			setMode(Mode.EYES);
		}
		frightenedNanosRemaining = 0;
	}
	
	public void unfrighten() {
		if(mode == Mode.FRIGHTENED || mode == Mode.EYES) {
			setMode(unfrightenedMode);
		}
		frightenedNanosRemaining = 0;
	}
	
	public void setReleased(boolean released) {
		this.released = released;
	}
	
	public boolean isReleased() {
		return released;
	}
	
	protected int getNumImages() {
		return 2;
	}
	
	protected Image getImage() {
		int phase = oscillator.retrieve()+1;
		if(mode == Mode.FRIGHTENED) {
			String key;
			if(warningOscillator != null && warningOscillator.retrieve() == 0) {
				key = "WHITE";
			}
			else {
				key = "BLUE";
			}
			return frightenedImages.get(key + phase);
		}
		else if(mode == Mode.EYES) {
			return frightenedImages.get("EYES_" + direction);
		}
		else {
			return images.get("" + direction + phase);
		}
	}
	
	public Color getColor() {
		return Color.blue;
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		Point target = getTargetTile();
		int pu = Board.PAC_UNIT;
		Point us = getLocation();
		g.setColor(getColor());
		//g.drawRect(target.x*pu - us.x + pu, target.y*pu - us.y + pu, pu, pu);
	}
}