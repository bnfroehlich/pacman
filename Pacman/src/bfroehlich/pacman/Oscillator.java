package bfroehlich.pacman;

import java.util.ArrayList;

public class Oscillator {
	
	private int arguments;
	private long duration;
	private long startTime;

	public Oscillator(int arguments, long duration) {
		this.arguments = arguments;
		this.duration = duration;
	}
	
	public int retrieve() {
		if(startTime == 0) {
			startTime = System.nanoTime();
		}
		long time = System.nanoTime();
		long phase = (time-startTime) % (duration*((long) arguments));
		return (int) (phase/duration);
	}
	
	public void reset() {
		startTime = 0;
	}
}