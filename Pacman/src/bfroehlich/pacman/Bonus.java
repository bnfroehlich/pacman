package bfroehlich.pacman;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class Bonus extends Component {
	
	private int value;
	private long startTime;
	private long duration;
	
	public Bonus(Vector vector, double distance, int value, long startTime, long duration) {
		super(vector, distance);
		this.value = value;
		this.startTime = startTime;
		this.duration = duration;
	}
	
	public boolean isExpired() {
		return(System.currentTimeMillis()-startTime > duration);
	}
	
	public void paint(Graphics g) {
		g.setColor(Color.YELLOW);
        if(value < 1000) {
            g.setFont(new Font("Comic Sans", Font.PLAIN, 16));
        	g.drawString(""+value, -15, 7);
        }
        else {
            g.setFont(new Font("Comic Sans", Font.PLAIN, 10));
        	g.drawString(""+value, -13, 5);
        }
	}
}