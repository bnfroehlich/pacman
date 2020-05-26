package bfroehlich.pacman;

public enum Direction
{
    NORTH, SOUTH, EAST, WEST, STOPPED;
    
    public double rotation() {
    	if(this == EAST) {
    		return 0;
    	}
    	if(this == NORTH) {
    		return Math.PI*3.0/2.0;
    	}
    	if(this == WEST) {
    		return Math.PI;
    	}
    	if(this == SOUTH) {
    		return Math.PI/2.0;
    	}
    	return 0;
    }
    
    public boolean isVertical() {
    	return (this == NORTH || this == SOUTH);
    }
    
    public boolean isHorizontal() {
    	return (this == EAST || this == WEST);
    }
    
    public boolean isPositive() {
    	return (this == EAST || this == SOUTH);
    }
    
    public boolean isNegative() {
    	return (this == NORTH || this == WEST);
    }
    
    public Direction reverse() {
    	if(this == NORTH) {
    		return SOUTH;
    	}
    	else if(this == SOUTH) {
    		return NORTH;
    	}
    	else if(this == EAST) {
    		return WEST;
    	}
    	else if(this == WEST) {
    		return EAST;
    	}
    	else if(this == STOPPED) {
    		return STOPPED;
    	}
    	return null;
    }
}