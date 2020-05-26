package bfroehlich.pacman;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

public class Engine implements Runnable {
	
	public static int TICK_LENGTH_NANOS = 6200000;
	public static int BG_LOOP_SHORTEN_NANOS = 85000000;
	public static int F_LOOP_SHORTEN_NANOS = 55000000;
	
	public static double[][][] data = {
		//pacman
		{{.80, .71, .90, .79},
		{.90, .79, .95, .83},
		{1.00, .87, 1.00, .87},
		{.90, .79, 0, 0}},
		
		//ghosts
		{{.75, .50, .40},
		{.85, .55, .45},
		{.95, .60, .50},
		{.95, .00, .50}},
		//mode times
		{{7, 20, 7, 20, 5, 7, 5},
		{7, 20, 7, 20, 5, 1033, 1},
		{5, 20, 5, 20, 5, 1037, 1},
		{5, 20, 5, 20, 5, 1037, 1}},
		//frighten time
		{{6, 5, 4, 3, 2, 5, 2, 2, 1, 5, 2, 1, 1, 3, 1, 1, 0, 1, 0, 0}},
		//elroy triggers
		{{20, 10}, {30, 15}, {40, 20}, {40, 20}, {40, 20}, {50, 25}, {50, 25}, {50, 25},
			{60, 30}, {60, 30}, {60, 30}, {80, 40}, {80, 40}, {80, 40}, {100, 50}, {100, 50},
			{100, 50}, {100, 50}, {100, 50}, {120, 60}, {120, 60}, {120, 60}},
		//elroy speeds
		{{.80, .85}, {.90, .95}, {1.00, 1.05}, {1.00, 1.05}},
		//dot counters
		{{0, 30, 60}, {0, 0, 50}, {0, 0, 0}}
	};
	
	private Board board;
	private BoardPanel boardPanel;
	private Manager manager;
	
	private int level;
	private int score;
	private int livesLeft;
	private Point lastPacmanTile;
	
	private Thread spirit;
	private boolean gameRunning;
	private boolean playerPaused;
	private long lastUpdateNanos;
	private long downNanosSinceLastUpdate;
	private long pauseTimeNanos;
	
	private int ghostsCaptured;
	private boolean extraLifeFired;
	private boolean food1Fired;
	private boolean food2Fired;
	private int dotCounter;
	private long fruitStart;
	//private long fruitBonusStart;
	private int[] dotCounters;
	private int globalDotCounter;
	private boolean globalDotCounterOn;
	private long lastEating;
	
	private long lastBgSoundNanos;
	private long bgSoundDurationNanos;
	private long lastFrightenedSoundNanos;
	private long frightenedSoundDurationNanos;
	private boolean bgIsWakaWaka;
	private boolean frightenedSoundIsEyes;
	private SoundEngine soundfx;
	private SoundEngine bgSound;
	private SoundEngine frightenedSound;
	
	private ArrayList<Long> timesPassed;
	private long over;
	
	public Engine(Board board, BoardPanel boardPanel, Manager manager) {
		this.board = board;
		this.boardPanel = boardPanel;
		this.manager = manager;
		timesPassed = new ArrayList<Long>();
		soundfx = new SoundEngine();
		bgSound = new SoundEngine();
		frightenedSound = new SoundEngine();
	}

	public void start() {
        board.requestFocusInWindow();
        
        if(!playerPaused) {
        	this.gameRunning = true;
        }
        
        if(spirit == null || !spirit.isAlive()) {
        	this.spirit = new Thread(this);
        	this.spirit.start();
        }
    }

    public void stop() {
        this.gameRunning = false;
    }
    
    public void pause() {
    	playerPaused = !playerPaused;
    	if(gameRunning || !playerPaused) {
    		startstop();
    	}
    }
    
    public void startstop() {
    	if(gameRunning) {
    		pauseTimeNanos = System.nanoTime();
    		stop();
    	}
    	else {
    		downNanosSinceLastUpdate += System.nanoTime() - pauseTimeNanos;
    		pauseTimeNanos = 0;
    		start();
    	}
    }

    public void run() {
        while (this.gameRunning) {
        	try {
        		tick();
	    	}
			catch(Exception e) {
				Console.staticPrint("" + e.getClass());
				Console.staticPrint(e.getMessage());
			}
        }
    }
    
    public int getScore() {
    	return score;
    }
    
    public int getLivesLeft() {
    	return livesLeft;
    }
    
    public int getLevel() {
    	return level;
    }
    
    private void gameOver() {
    	manager.gameOver(score);
    }
    
    private void interlude(final String music) {
    	bgSound.stop();
    	lastBgSoundNanos = 0;
    	frightenedSound.stop();
		long millis = (long) ((float) soundfx(music)*1000);
		freeze(millis);
    }
    
    private void freeze(final long millis) {
    	if(gameRunning) {
    		startstop();
    	}
    	Thread picasso = new Thread(new Runnable() {
			public void run() {
				while(!gameRunning) {
					boardPanel.repaint();
				}
			}
		});
    	picasso.start();
		try {
			Thread.sleep(millis);
		}
		catch(InterruptedException e) {};
		if(!gameRunning && !playerPaused) {
			startstop();
		}
    }
    
    public void loadNewGame() {
    	score = 0;
		livesLeft = 2;
		extraLifeFired = false;
    	boardPanel.clearLevelSymbols();
    	loadLevel(1);
    }
    
    private void loadLevel(int level) {
    	if(level > 1) {
    		board.setFlashing(true);
    		freeze(1800);
    		board.setFlashing(false);
    	}
		boardPanel.addLevelSymbol(board.getFruit(level));
    	this.level = level;
    	dotCounter = 0;
    	dotCounters = new int[4];
    	globalDotCounterOn = false;
    	globalDotCounter = 0;
    	lastEating = System.nanoTime();
    	food1Fired = false;
    	food2Fired = false;
    	board.addDefaultFood();
    	board.resetSpriteLocations();
    	for(Ghost ghost : board.getGhosts()) {
    		resetGhost(ghost);
    	}
    	resetPacman(board.getPacman());
    	if(!timesPassed.isEmpty()) {
	    	long total = 0;
	    	for(int i = 0; i < timesPassed.size(); i++) {
	    		total += timesPassed.get(i);
	    	}
//	    	System.out.println("" + (total/((long) timesPassed.size())));
//	    	System.out.println("" + over + ", " + timesPassed.size());
    	}
    	if(level % 2 == 1 && level > 1) {
    		board.setMovieMode(true);
    		board.setMovie(new Movie(board, 1));
    		interlude("intermission.wav");
    		board.setMovie(null);
    		board.setMovieMode(false);
    	}
    	interlude("opening.wav");
    }
    
    private void die() {
    	interlude("die.wav");
    	livesLeft--;
		if(livesLeft < 0) {
			gameOver();
		}
		else {
	    	if(!globalDotCounterOn) {
	    		globalDotCounter = 0;
	    	}
	    	globalDotCounterOn = true;
	    	board.resetSpriteLocations();
	    	for(Ghost ghost : board.getGhosts()) {
	    		resetGhost(ghost);
	    	}
	    	board.removeFruit();
	    	board.getBonuses().clear();
	    	if(livesLeft >= 0) {
	    		interlude("opening.wav");
	    	}
		}
    }
    
    private void resetPacman(Pacman pacman) {
    	int levelIndex = shortLevelIndex();
    	pacman.setSpeedData(toArrayList(data[0][levelIndex]));
    }
    
    private void resetGhost(Ghost ghost) {
    	int levelIndex = shortLevelIndex();
    	ghost.setSpeedData(toArrayList(data[1][levelIndex]));
    	ghost.setModeChangeTimes(toArrayList(data[2][levelIndex]));
    	if(ghost instanceof Blinky) {
    		((Blinky) ghost).setElroy(0);
    		((Blinky) ghost).setElroyData(toArrayList(data[5][shortLevelIndex()]));
    	}
    	ghost.setReleased(false);
    }
    
    private int shortLevelIndex() {
    	int index = 0;
    	if(level >= 2 && level <= 4) {
    		index = 1;
    	}
    	else if(level >= 5 && level <= 20) {
    		index = 2;
    	}
    	else if(level >= 21) {
    		index = 3;
    	}
    	return index;
    }
    
    private int longLevelIndex() {
    	if(level >= 21) {
    		return 20;
    	}
    	return level-1;
    }
    
    private ArrayList<Double> toArrayList(double[] arr) {
    	ArrayList<Double> data = new ArrayList<Double>();
    	for(int i = 0; i < arr.length; i++) {
    		data.add(arr[i]);
    	}
    	return data;
    }
    
    private Float soundfx(String name) {
    	Float length = soundfx.play(name);
    	System.out.println(length);
    	return length;
    }
    
    private Float bgSound(String name) {
    	return bgSound.play(name);
    }
    
    private Float frightenedSound(String name) {
    	return frightenedSound.play(name);
    }
    
    public void mute() {
    	soundfx.mute();
    	bgSound.mute();
    	frightenedSound.mute();
    }
    
    private void tick() {
    	long nanoTime = System.nanoTime();
    	if(lastUpdateNanos == 0) {
    		lastUpdateNanos = nanoTime;
    	}
    	long timeElapsed = nanoTime - lastUpdateNanos - downNanosSinceLastUpdate;
    	if(timeElapsed < 0) {
    		timeElapsed = 0;
    	}
    	downNanosSinceLastUpdate = 0;
    	lastUpdateNanos = nanoTime;
    	board.requestFocusInWindow();
    	try {
    		Point pacmanTile = board.getPacmanTile();
	    	updateSprites(timeElapsed);
	    	boolean eatenThisTick = runIntoStuff(pacmanTile);
	    	fireLevelEvents(pacmanTile, eatenThisTick);

	    	long nanosPassed = System.nanoTime() - nanoTime;
    		timesPassed.add(nanosPassed);
            if(nanosPassed < TICK_LENGTH_NANOS) {
            	Thread.sleep((TICK_LENGTH_NANOS - nanosPassed)/1000000, (int) ((TICK_LENGTH_NANOS - nanosPassed)%1000000));
            }
            else {
            	over++;
            }
            bgSound(nanoTime, board.getPacman().isEating());
        } catch (InterruptedException ie) {
            System.err.println("World: sleep() interrupted: "
                    + ie.getMessage());
        }
        boardPanel.repaint();
    }
    
    private void updateSprites(long timeElapsed) {
    	ArrayList<Sprite> sprites = board.getSprites();
    	Pacman pacman = board.getPacman();
    	for(Sprite sprite : sprites) {
    		Direction currentInput = null;
    		if(sprite.equals(pacman)) {
    			currentInput = board.getCurrentInput();
    		}
    		else if(sprite instanceof Ghost) {
    			currentInput = ((Ghost) sprite).calculateNextDirection();
    		}
			if(currentInput == sprite.getDirection().reverse()) {
    			//reversing direction: easy
    			sprite.setDirection(currentInput);
			}
    		sprite.update(timeElapsed);
    		if(sprite.getDistance() > sprite.getVector().length()) {
    			sprite.setDistance(sprite.getVector().length());
    			if(sprite.equals(pacman)) {
    				//running into a wall
    				pacman.setEating(false);
    			}
    		}
    		if(sprite.getDistance() < 0.0) {
    			sprite.setDistance(0);
    			if(sprite.equals(pacman)) {
    				//running into a wall
    				pacman.setEating(false);
    			}
    		}
    		Intersection passed = sprite.getVector().getIntersectionAt(sprite.getDistance());
    		if(passed != null) {
    			computeIntersection(sprite, 0, passed, currentInput);
    		}
    		else if(sprite.equals(pacman)) {
    			//cornering
    			if(!board.isTunnel(sprite.getVector(), sprite.getDistance())) {
	    			Intersection next = sprite.getVector().getIntersectionBetween(sprite.getDistance()-5, sprite.getDistance()+5);
	    			if(next != null) {
	    				computeIntersection(sprite, 0, next, currentInput);
	    			}
    			}
    		}
    		else if(((Ghost) sprite).getMode() == Mode.EYES) {
    			//making sure speedy eyes don't pass intersections
    			boolean inc = sprite.getDirection().isPositive();
    			Intersection next = null;
    			if(inc) {
    				next = sprite.getVector().getIntersectionBetween(sprite.getDistance()-sprite.getSpeed(), sprite.getDistance());
    			}
    			else {
    				next = sprite.getVector().getIntersectionBetween(sprite.getDistance(), sprite.getDistance()+sprite.getSpeed());
    			}
    			if(next != null) {
    				computeIntersection(sprite, 0, next, currentInput);
    			}
    		}
    	}
    }
    
    private boolean runIntoStuff(Point pacmanTile) throws InterruptedException {
    	ArrayList<Food> foods = board.getFoods();
    	Iterator<Food> fit = foods.iterator();
    	boolean justEaten = false;
    	while(fit.hasNext()) {
    		Food food = fit.next();
    		Point foodTile = board.getTile(food.getVector(), food.getDistance());
    		if(foodTile.equals(pacmanTile)) {
    			if(food instanceof PowerPellet) {
    				ghostsCaptured = 0;
    				for(Ghost ghost : board.getGhosts()) {
    					if(ghost.getMode() == Mode.EYES){
    						ghost.setDirection(ghost.getDirection().reverse());
    					}
    					else {
    						ghost.frighten((int) data[3][0][longLevelIndex()]);
    					}
    				}
    				justEaten = true;
    			}
    			else if(food.getClass().getSimpleName().equals("Food")) {
    				justEaten = true;
    			}
    			else {
    				board.addBonus(new Bonus(food.getVector(), food.getDistance(), food.points(), System.currentTimeMillis(), 3000));
    				soundfx("fruit.wav");
    			}
    			score += food.points();
    			fit.remove();
    			if(foods.isEmpty()) {
    				board.getBonuses().clear();
    				bgSound.stop();
    				frightenedSound.stop();
    				freeze(1000);
    				loadLevel(level + 1);
    			}
    		}
    		else if(food instanceof Fruit) {
				if(Math.log10(System.nanoTime() - fruitStart) >= 10) {
					fit.remove();
				}
			}
    	}
    	for(Ghost ghost : board.getGhosts()) {
    		if(board.getTile(ghost.getVector(), ghost.getDistance()).equals(pacmanTile)) {
    			Mode mode = ghost.getMode();
    			if(mode.isNormal()) {
    				bgSound.stop();
    				freeze(500);
    				die();
    			}
    			else if(mode == Mode.FRIGHTENED) {
    				interlude("eatghost.wav");
    				ghostsCaptured++;
    				int points = (int) Math.pow(2, ghostsCaptured)*100;
    				board.addBonus(new Bonus(ghost.getVector(), ghost.getDistance(), points, System.currentTimeMillis(), 3000));
    				score += points;
    				ghost.capture();
    			}
    		}
    	}
    	if(justEaten){
    		//only applies to pellets, power pellets
			dotCounter++;
			if(globalDotCounterOn) {
				globalDotCounter++;
			}
			lastEating = System.nanoTime();
    	}
    	return justEaten;
    }
    
    private void bgSound(long nanoTime, boolean eating) {
    	if(!gameRunning) {
    		bgSound.pause();
    		return;
    	}
    	long difference = nanoTime - lastBgSoundNanos;
    	if(difference > bgSoundDurationNanos-BG_LOOP_SHORTEN_NANOS || eating != bgIsWakaWaka) {
    		float length = 0;
    		bgSound.pause();
    		if(eating) {
    			length = bgSound("waka.wav");
    			bgIsWakaWaka = true;
    		}
    		else {
    			length = bgSound("siren.wav");
    			bgIsWakaWaka = false;
    		}
			lastBgSoundNanos = nanoTime;
			bgSoundDurationNanos = (long) (length*1000000000);
    	}
    	difference = nanoTime - lastFrightenedSoundNanos;
    	if(!board.isAllGhostsNormal()) {
    		boolean eyes = board.isGhostEyes();
    		if((difference > frightenedSoundDurationNanos-F_LOOP_SHORTEN_NANOS) || (eyes!=frightenedSoundIsEyes)) {
    			float length = 0;
    			frightenedSound.pause();
        		if(eyes) {
        			length = frightenedSound("return to home.wav");
    				frightenedSound.setVolume(100);
        			frightenedSoundIsEyes = true;
        		}
        		else {
        			length = frightenedSound("turn to blue.wav");
    				frightenedSound.setVolume(55);
        			frightenedSoundIsEyes = false;
        		}
	    		lastFrightenedSoundNanos = nanoTime;
				frightenedSoundDurationNanos = (long) (length*1000000000);
    		}
    	}
    	else {
    		frightenedSound.stop();
    	}
    }
    
    private void fireLevelEvents(Point pacmanTile, boolean eating) {
    	Blinky blinky = board.getBlinky();
    	Pinky pinky = board.getPinky();
    	Inky inky = board.getInky();
    	Clyde clyde = board.getClyde();
    	Ghost[] ranking = {blinky, pinky, inky, clyde};
    	if(!globalDotCounterOn) {
    		blinky.setReleased(true);
    		pinky.setReleased(true);
    		if(level == 1) {
    			if(dotCounters[2] >= 30) {
    				inky.setReleased(true);
    			}
    			if(dotCounters[3] >= 60) {
    				clyde.setReleased(true);
    			}
    		}
    		else if(level == 2) {
    			inky.setReleased(true);
    			if(dotCounters[3] >= 50) {
    				clyde.setReleased(true);
    			}
    		}
    		else {
    			inky.setReleased(true);
    			clyde.setReleased(true);
    		}
    	}
    	else {
    		blinky.setReleased(true);
    		if(globalDotCounter == 7) {
    			pinky.setReleased(true);
    		}
    		else if(globalDotCounter == 17) {
    			inky.setReleased(true);
    		}
    		else if(globalDotCounter == 32) {
    			if(!clyde.isReleased() && board.getHouse().contains(clyde.getVector())) {
    				globalDotCounterOn = false;
    				globalDotCounter = 0;
    			}
    		}
    	}
		for(int i = 0; i < ranking.length; i++) {
			if(!ranking[i].isReleased()) {
		    	if((System.nanoTime() - lastEating)/1000000000 >= 5) {
					ranking[i].setReleased(true);
					lastEating = System.nanoTime();
		    	}
		    	else if(eating) {
					dotCounters[i]++;
		    	}
		    	break;
			}
		}
    	if(score >= 10000 && !extraLifeFired) {
			soundfx("extralife.wav");
    		extraLifeFired = true;
    		livesLeft++;
    	}
    	if(dotCounter == 70 && !food1Fired) {
    		board.getFoods().add(board.getFruit(level));
    		fruitStart = System.nanoTime();
    		food1Fired = true;
    	}
    	if(dotCounter == 170 && !food2Fired) {
    		board.getFoods().add(board.getFruit(level));
    		fruitStart = System.nanoTime();
    		food2Fired = true;
    	}
		if(lastPacmanTile == null) {
			lastPacmanTile = pacmanTile;
		}
		else if(!pacmanTile.equals(lastPacmanTile)) {
			//if we changed tiles
			board.getPacman().setEating(eating);
			lastPacmanTile = pacmanTile;
		}
		int remaining = 244 - dotCounter;
		if(remaining <= data[4][longLevelIndex()][1]) {
			board.getBlinky().setElroy(2);
		}
		else if(remaining <= data[4][longLevelIndex()][0]) {
			board.getBlinky().setElroy(1);
		}
		Iterator<Bonus> bit = board.getBonuses().iterator();
		while(bit.hasNext()) {
			Bonus b = bit.next();
			if(b.isExpired()) {
				bit.remove();
			}
		}
    }
    
    private void computeIntersection(Sprite sprite, int past, Intersection intersection, Direction input) {
    	if(input == null) {
    		return;
    	}
    	Vector cross = intersection.getCross();
    	if(board.getGhosts().contains(sprite)) {
    		if(board.getForbiddenPaths().contains(cross) && input == Direction.NORTH && ((Ghost) sprite).getMode().isNormal()) {
    			//forbidden pathways
    			return;
    		}
    		if(board.getHouse().contains(cross) && !board.getHouse().contains(intersection.getVector()) && ((Ghost) sprite).getMode() != Mode.EYES) {
    			//entrance to ghost house
    			return;
    		}
    	}
    	else if(board.getHouse().contains(cross) && !board.getHouse().contains(intersection.getVector())) {
    		return;
    	}
    	ArrayList<Vector> tunnel = board.getTunnel();
    	if((input.isVertical() == cross.isVertical() && intersection.getOpenDirections().contains(input))) {
    		sprite.setVector(cross);
    		sprite.setDistance(intersection.getDistanceAlongCross());
    		sprite.setDirection(input);
    		sprite.move(past);
    		if(past == 0) {
    			sprite.move(1);
    		}
    	}
    	else if (tunnel.contains(cross) && tunnel.contains(sprite.getVector())) {
    		sprite.setVector(cross);
    		sprite.setDistance(intersection.getDistanceAlongCross());
    		//no change of direction
    		sprite.move(past);
    		if(past == 0) {
    			sprite.move(1);
    		}
    	}
    }
}