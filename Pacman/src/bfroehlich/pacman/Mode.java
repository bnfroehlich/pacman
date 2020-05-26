package bfroehlich.pacman;

public enum Mode {

	CHASE, SCATTER, FRIGHTENED, EYES;
	
	public Mode standardSwitch() {
		if(this == CHASE) {
			return SCATTER;
		}
		else if(this == SCATTER) {
			return CHASE;
		}
		System.out.println("ouch");
		return FRIGHTENED;
	}
	
	public boolean isNormal() {
		return (this == CHASE || this == SCATTER);
	}
}
