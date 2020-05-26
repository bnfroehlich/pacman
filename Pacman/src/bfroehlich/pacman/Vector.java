package bfroehlich.pacman;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Vector math, such as length, assumes vectors are either straight vertical or horizontal.
 * @author benfroehlich
 *
 */
public class Vector {

	private Point location;
	private Dimension components;
	private ArrayList<Intersection> intersections;
	
	public Vector(Point location, Dimension components) {
		this.location = location;
		this.components = components;
	}
	
	public void setIntersections(ArrayList<Intersection> intersections) {
		this.intersections = intersections;
	}
	
	public ArrayList<Intersection> getIntersections() {
		return intersections;
	}
	
	public void addIntersection(Intersection inter) {
		intersections.add(inter);
	}
	
	public Point getLocation() {
		return location;
	}
	
	public Dimension getComponents() {
		return components;
	}
	
	public boolean isVertical() {
		return components.height != 0;
	}
	
	public int length() {
		return components.width + components.height;
	}
	
	public Point pointAt(int distance) {
		if(isVertical()) {
			return new Point(location.x, location.y + distance);
		}
		return new Point(location.x + distance, location.y);
	}
	
	public Intersection getIntersectionAt(int distance) {
		for(Intersection inter : intersections) {
			if(inter.getDistanceAt() == distance) {
				return inter;
			}
		}
		return null;
	}
	
	public Intersection getIntersectionBetween(double d1, double d2) {
		for(Intersection inter : intersections) {
			if( (inter.getDistanceAt() >= d1 && inter.getDistanceAt() <= d2) 
					|| (inter.getDistanceAt() <= d1 && inter.getDistanceAt() >= d2)) {
				return inter;
			}
		}
		return null;
	}
	
	public Intersection getNextIntersection(int distance, boolean increasing) {
		Intersection closest = null;
		for(int i = 0; i < intersections.size(); i++) {
			Intersection next = intersections.get(i);
			if(next.getDistanceAt() == distance) {
				return next;
			}
			if((next.getDistanceAt() > distance) == increasing) {
				if(closest == null || Math.abs(next.getDistanceAt() - distance) < Math.abs(closest.getDistanceAt() - distance)) {
					closest = next;
				}
			}
		}
		return closest;
	}
}