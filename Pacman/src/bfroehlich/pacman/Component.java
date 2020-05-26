package bfroehlich.pacman;

import java.awt.Point;

public class Component {

	protected Vector vector;
	protected double distance;
	
	public Component(Vector vector, double distance) {
		this.vector = vector;
		this.distance = distance;
	}
	
	public Component() {
		
	}
	
	public Vector getVector() {
		return vector;
	}
	
	public void setVector(Vector myVector) {
		this.vector = myVector;
	}
	
	public int getDistance() {
		return (int) distance;
	}
	
	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	public Point getLocation() {
		return vector.pointAt(getDistance());
	}
	
	public void update(long nanosElapsed) {
		
	}
}