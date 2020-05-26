package bfroehlich.pacman;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JPanel;

public class Board extends JPanel {
	
	public static final int PAC_UNIT = 16;
	public static final Dimension BOARD_SIZE = new Dimension(PAC_UNIT*27, PAC_UNIT*30);
	public static final Dimension SIZE = new Dimension(PAC_UNIT*7/4, PAC_UNIT*7/4);
	
	private Manager manager;
	private Engine engine;

	private ArrayList<Vector> path;
	private ArrayList<Sprite> sprites;
	private ArrayList<Food> foods;
	private ArrayList<Bonus> bonuses;
	private Movie movie;

	private Direction currentInput;
	private boolean flashing;
	private Oscillator flasher;
	private boolean movieMode;
	
	public Board(Manager manager) {
		this.manager = manager;
		buildPath();
		init();
		setPreferredSize(BOARD_SIZE);
		foods = new ArrayList<Food>();
		bonuses = new ArrayList<Bonus>();
		
		sprites = new ArrayList<Sprite>();
		Pacman hero = new Pacman(SIZE, this);
		sprites.add(hero);
		
		Blinky blinky = new Blinky(SIZE, this);
		sprites.add(blinky);
		Pinky pinky = new Pinky(SIZE, this);
		sprites.add(pinky);
		Inky inky = new Inky(SIZE, this, blinky);
		sprites.add(inky);
		Clyde clyde = new Clyde(SIZE, this);
		sprites.add(clyde);
		
		resetSpriteLocations();
		flasher = new Oscillator(2, 200000000);
		movieMode = false;
	}
	
	private void init() {
		addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			
			public void keyReleased(KeyEvent e) {}
			
			public void keyPressed(KeyEvent e) {
				int code = e.getKeyCode();
				System.out.println(code);
				if(code == KeyEvent.VK_LEFT) {
					currentInput = Direction.WEST;
				}
				else if(code == KeyEvent.VK_UP) {
					currentInput = Direction.NORTH;
				}
				else if(code == KeyEvent.VK_RIGHT) {
					currentInput = Direction.EAST;
				}
				else if(code == KeyEvent.VK_DOWN) {
					currentInput = Direction.SOUTH;
				}
				else if(code == KeyEvent.VK_P) {
					engine.pause();
				}
				else if(code == KeyEvent.VK_M) {
					engine.mute();
				}
			}
		});
	}
	
	public void setEngine(Engine e) {
		this.engine = e;
	}
	
	public Fruit getFruit(int level) {
		Vector v = path.get(10);
		int dist = PAC_UNIT*9/2;
		Fruit cherry = new Fruit(v, dist, SIZE, "cherry.png", 100);
		Fruit strawberry = new Fruit(v, dist, SIZE, "strawberry.png", 300);
		Fruit peach = new Fruit(v, dist, SIZE, "peach.png", 500);
		Fruit apple = new Fruit(v, dist, SIZE, "apple.png", 700);
		Fruit grape = new Fruit(v, dist, SIZE, "grape.png", 1000);
		Fruit galaxian = new Fruit(v, dist, SIZE, "galaxian.png", 2000);
		Fruit bell = new Fruit(v, dist, SIZE, "bell.png", 3000);
		Fruit key = new Fruit(v, dist, SIZE, "key.png", 5000);
		switch(level) {
			case 1: return cherry;
			case 2: return strawberry;
			case 3: return peach;
			case 4: return peach;
			case 5: return apple;
			case 6: return apple;
			case 7: return grape;
			case 8: return grape;
			case 9: return galaxian;
			case 10: return galaxian;
			case 11: return bell;
			case 12: return bell;
			default: return key;
		}
	}
	
	public ArrayList<Vector> getPath() {
		return path;
	}
	
	public ArrayList<Vector> getForbiddenPaths() {
		ArrayList<Vector> forbidden = new ArrayList<Vector>();
		forbidden.add(path.get(9+21-1));
		forbidden.add(path.get(10+21-1));
		forbidden.add(path.get(16+21-1));
		forbidden.add(path.get(17+21-1));
		return forbidden;
	}
	
	public ArrayList<Vector> getTunnel() {
		ArrayList<Vector> tunnel = new ArrayList<Vector>();
		tunnel.add(path.get(8));
		tunnel.add(path.get(9));
		return tunnel;
	}
	
	public ArrayList<Vector> getHouse() {
		ArrayList<Vector> house = new ArrayList<Vector>();
		for(int i = 45; i < path.size(); i++) {
			house.add(path.get(i));
		}
		return house;
	}
	
	public boolean isTunnel(Vector v, int d) {
		return (v.equals(path.get(8)) && d < PAC_UNIT*5) ||
				(v.equals(path.get(9)) && d > PAC_UNIT*3);
	}
	
	public ArrayList<Sprite> getSprites() {
		return sprites;
	}
	
	public ArrayList<Food> getFoods() {
		return foods;
	}
	
	public Direction getCurrentInput() {
		return currentInput;
	}
	
	public Point getTile(Vector v, int d) {
		int x = (v.pointAt(d).x);
		int y = (v.pointAt(d).y);
		return new Point(x/PAC_UNIT, y/PAC_UNIT);
	}
	
	public Point getTileCenter(Point tile) {
		return new Point(tile.x*PAC_UNIT + PAC_UNIT/2, tile.y*PAC_UNIT + PAC_UNIT/2);
	}
	
	public Point getNeighboringTile(Point tile, Direction direction) {
		Point t = tile;
		if(direction == Direction.NORTH) {
			t = new Point(tile.x, tile.y-1);
		}
		else if(direction == Direction.SOUTH) {
			t = new Point(tile.x, tile.y+1);
		}
		else if(direction == Direction.EAST) {
			t = new Point(tile.x+1, tile.y);
		}
		else if(direction == Direction.WEST) {
			t = new Point(tile.x-1, tile.y);
		}
		return t;
	}
	
	public Point getPacmanTile() {
		Pacman pacman = getPacman();
		return getTile(pacman.getVector(), pacman.getDistance());
	}
	
	public Pacman getPacman() {
		for(Sprite sprite : sprites) {
			if(sprite instanceof Pacman) {
				return ((Pacman) sprite);
			}
		}
		return null;
	}
	
	public ArrayList<Ghost> getGhosts() {
		ArrayList<Ghost> ghosts = new ArrayList<Ghost>();
		for(Sprite sprite : new ArrayList<Sprite>(sprites)) {
			if(sprite instanceof Ghost) {
				ghosts.add((Ghost) sprite);
			}
		}
		return ghosts;
	}
	
	public Blinky getBlinky() {
		for(Sprite sprite : sprites) {
			if(sprite instanceof Blinky) {
				return ((Blinky) sprite);
			}
		}
		return null;
	}
	
	public Pinky getPinky() {
		for(Sprite sprite : sprites) {
			if(sprite instanceof Pinky) {
				return ((Pinky) sprite);
			}
		}
		return null;
	}
	
	public Inky getInky() {
		for(Sprite sprite : sprites) {
			if(sprite instanceof Inky) {
				return ((Inky) sprite);
			}
		}
		return null;
	}
	
	public Clyde getClyde() {
		for(Sprite sprite : sprites) {
			if(sprite instanceof Clyde) {
				return ((Clyde) sprite);
			}
		}
		return null;
	}
	
	public boolean isGhostFrightened() {
		for(Ghost ghost : getGhosts()) {
			if(ghost.getMode() == Mode.FRIGHTENED) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isGhostEyes() {
		for(Ghost ghost : getGhosts()) {
			if(ghost.getMode() == Mode.EYES) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isAllGhostsNormal() {
		for(Ghost ghost : getGhosts()) {
			if(!ghost.getMode().isNormal()) {
				return false;
			}
		}
		return true;
	}
	
	public int tileDistanceSquared(Point p1, Point p2) {
		return (int) Math.pow((p1.x - p2.x), 2) + (int) Math.pow((p1.y - p2.y), 2);
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, BOARD_SIZE.width, BOARD_SIZE.height);
		if(movieMode) {
			if(movie != null) {
				movie.paint(g);
			}
			return;
		}
		if(flashing && flasher.retrieve() == 0) {
			g.setColor(Color.white);
		}
		else {
			g.setColor(new Color(24, 24, 255));
		}
		g.drawRect(0, 0, BOARD_SIZE.width-1, BOARD_SIZE.height-1);
		int[][] walls = {
				{2, 2, 3, 2},
				{7, 2, 4, 2},
				{13, 0, 1, 4},
				{16, 2, 4, 2},
				{22, 2, 3, 2},
				{2, 6, 3, 1},
				{7, 6, 1, 7},
				{8, 9, 3, 1},
				{10, 6, 7, 1},
				{13, 7, 1, 3},
				{19, 6, 1, 7},
				{16, 9, 3, 1},
				{22, 6, 3, 1},
				{0, 9, 5, 4},
				{22, 9, 5, 4},
				{10, 12, 7, 4},
				{0, 15, 5, 4},
				{7, 15, 1, 4},
				{19, 15, 1, 4},
				{22, 15, 5, 4},
				{10, 18, 7, 1},
				{13, 19, 1, 3},
				{2, 21, 3, 1},
				{4, 22, 1, 3},
				{7, 21, 4, 1},
				{16, 21, 4, 1},
				{22, 21, 3, 1},
				{22, 22, 1, 3},
				{0, 24, 2, 1},
				{7, 24, 1, 3},
				{2, 27, 9, 1},
				{10, 24, 7, 1},
				{13, 25, 1, 3},
				{19, 24, 1, 3},
				{16, 27, 9, 1},
				{25, 24, 2, 1}
		};
		for(int i = 0; i < walls.length; i++) {
			int[] w = walls[i];
			g.drawRoundRect(w[0]*PAC_UNIT, w[1]*PAC_UNIT, w[2]*PAC_UNIT-1, w[3]*PAC_UNIT-1, 0, 0);
		}
		g.translate(PAC_UNIT/2, PAC_UNIT/2);
        Iterator<Bonus> bit = new ArrayList<Bonus>(bonuses).iterator();
        while (bit.hasNext()) {
            Bonus b = bit.next();
            if(b != null) {
                Graphics gtemp = g.create();
                gtemp.translate(b.getLocation().x, b.getLocation().y);
                b.paint(gtemp);
            }
        }
        Iterator<Food> fit = new ArrayList<Food>(foods).iterator();
        while (fit.hasNext()) {
            Food f = fit.next();
            if(f != null) {
                Graphics gtemp = g.create();
                gtemp.translate(f.getLocation().x, f.getLocation().y);
                f.paint(gtemp);
            }
        }
        Iterator<Sprite> it = new ArrayList<Sprite>(sprites).iterator();
        while (it.hasNext()) {
            Sprite s = it.next();
            if(s != null) {
                Graphics gtemp = g.create();
                gtemp.translate(s.getLocation().x, s.getLocation().y);
                s.paint(gtemp);
            }
        }
	}
	
	public void resetSpriteLocations() {
		resetPacmanLocation();
		for(Ghost ghost : getGhosts()) {
			resetGhostLocation(ghost);
		}
	}
	
	public void resetGhostLocation(Ghost ghost) {
		ghost.setDirection(Direction.SOUTH);
		if(ghost instanceof Blinky) {
			ghost.setVector(path.get(45));
		}
		else if(ghost instanceof Pinky) {
			ghost.setVector(path.get(46));
		}
		else if(ghost instanceof Inky) {
			ghost.setVector(path.get(47));
		}
		else {
			ghost.setVector(path.get(48));
			ghost.setDirection(Direction.EAST);
		}
		ghost.setDistance(0);
	}
	
	public void resetPacmanLocation() {
		currentInput = null;
		Sprite pacman = getPacman();
		pacman.setVector(path.get(14));
		pacman.setDistance(PAC_UNIT*15/2);
		pacman.setDirection(Direction.WEST);
	}
	
	public void removeFruit() {
        Iterator<Food> fit = foods.iterator();
        while (fit.hasNext()) {
            Food f = fit.next();
            if(f != null && f instanceof Fruit) {
                fit.remove();
            }
        }
	}
	
	public void setFlashing(boolean flashing) {
		this.flashing = flashing;
		if(!flashing) {
			flasher.reset();
		}
	}
	
	public void setMovieMode(boolean movieMode) {
		this.movieMode = movieMode;
	}
	
	public void addBonus(Bonus b) {
		if(b.getVector() == null) {
			b.setVector(path.get(0));
		}
		bonuses.add(b);
	}
	
	public ArrayList<Bonus> getBonuses() {
		return bonuses;
	}
	
	public void setMovie(Movie m) {
		movie = m;
	}
	
	public void addDefaultFood() {
		foods = new ArrayList<Food>();
		ArrayList<Integer> hungryVectors = new ArrayList<Integer>();
		hungryVectors.add(8-1);
		hungryVectors.add(9-1);
		hungryVectors.add(10-1);
		hungryVectors.add(11-1);
		hungryVectors.add(9-1 + 21);
		hungryVectors.add(10-1 + 21);
		hungryVectors.add(11-1 + 21);
		hungryVectors.add(12-1 + 21);
		hungryVectors.add(46-1);
		hungryVectors.add(47-1);
		hungryVectors.add(48-1);
		hungryVectors.add(49-1);
		for(int i = 0; i < path.size(); i++) {
			if(hungryVectors.contains(i)) {
				continue;
			}
			Vector v = path.get(i);
			for(int j = 0; j <= v.length(); j += PAC_UNIT) {
				boolean alreadyFilled = false;
				for(Food food : foods) {
					if(getTile(food.getVector(), food.getDistance()).equals(getTile(v, j))) {
						alreadyFilled = true;
					}
				}
				if(alreadyFilled) {
					continue;
				}
				Food food = null;
				if( ((i == (1+21-1) || i == (8+21-1)) && j == PAC_UNIT*2) || ((i == (14-1) && j == 0) || (i == (16-1)) && j == PAC_UNIT*2) ) {
					food = new PowerPellet(v, j);
				}
				else if(i == 14 && (j == PAC_UNIT*7 || j == PAC_UNIT*8)) {
					//starting location
				}
				else {
					food = new Food(v, j);
				}
				if(food != null) {
					foods.add(food);
				}
			}
		}
	}
	
	private void buildPath() {
		int[][] horizCoors = {
				//x, y, width (measured in pellets)
				{0, 0, 11},
				{14, 0, 11},
				{0, 4, 25},
				{0, 7, 5},
				{8, 7, 3},
				{14, 7, 3},
				{20, 7, 5},
				{8, 10, 9},
				{0, 13, 8},
				{17, 13, 8},
				{8, 16, 9},
				{0, 19, 11},
				{14, 19, 11},
				{0, 22, 2},
				{5, 22, 15},
				{23, 22, 2},
				{0, 25, 5},
				{8, 25, 3},
				{14, 25, 3},
				{20, 25, 5},
				{0, 28, 25}
		};
		int[][] vertCoors = {
				//x, y, height
				{0, 0, 7},
				{5, 0, 25},
				{8, 4, 3},
				{11, 0, 4},
				{14, 0, 4},
				{17, 4, 3},
				{20, 0, 25},
				{25, 0, 7},
				{11, 7, 3},
				{14, 7, 3},
				{8, 10, 9},
				{17, 10, 9},
				{0, 19, 3},
				{2, 22, 3},
				{8, 22, 3},
				{11, 19, 3},
				{14, 19, 3},
				{17, 22, 3},
				{23, 22, 3},
				{25, 19, 3},
				{0, 25, 3},
				{11, 25, 3},
				{14, 25, 3},
				{25, 25, 3},
		};
		
		path = new ArrayList<Vector>();
		Vector[] horiz = new Vector[21];
		for(int i = 0; i < horiz.length; i++) {
			horiz[i] = new Vector(new Point(horizCoors[i][0]*PAC_UNIT + PAC_UNIT/2, horizCoors[i][1]*PAC_UNIT + PAC_UNIT/2), new Dimension(horizCoors[i][2]*PAC_UNIT, 0));
			path.add(horiz[i]);
		}
		Vector[] vert = new Vector[24];
		for(int i = 0; i < vert.length; i++) {
			vert[i] = new Vector(new Point(vertCoors[i][0]*PAC_UNIT + PAC_UNIT/2, vertCoors[i][1]*PAC_UNIT + PAC_UNIT/2), new Dimension(0, vertCoors[i][2]*PAC_UNIT));
			path.add(vert[i]);
		}
		
		int[][][] horizIntCoors = {
				//cross (starting at 1), distanceAt, distanceAlongCross
				{{1, 0, 0}, {2, 5, 0}, {4, 11, 0}},
				{{5, 0, 0}, {7, 6, 0}, {8, 11, 0}},
				{{1, 0, 4}, {2, 5, 4}, {3, 8, 0}, {4, 11, 4}, {5, 14, 4}, {6, 17, 0}, {7, 20, 4}, {8, 25, 4}},
				{{1, 0, 7}, {2, 5, 7}},
				{{3, 0, 3}, {9, 3, 0}},
				{{10, 0, 0}, {6, 3, 3}},
				{{7, 0, 7}, {8, 5, 7}},
				{{11, 0, 0}, {9, 3, 3}, {10, 6, 3}, {12, 9, 0}},
				{{2, 5, 13}, {11, 8, 3}},
				{{12, 0, 3}, {7, 3, 13}},
				{{11, 0, 6}, {12, 9, 6}},
				{{13, 0, 0}, {2, 5, 19}, {11, 8, 9}, {16, 11, 0}},
				{{17, 0, 0}, {12, 3, 9}, {7, 6, 19}, {20, 11, 0}},
				{{13, 0, 3}, {14, 2, 0}},
				{{2, 0, 22}, {15, 3, 0}, {16, 6, 3}, {17, 9, 3}, {18, 12, 0}, {7, 15, 22}},
				{{19, 0, 0}, {20, 2, 3}},
				{{21, 0, 0}, {14, 2, 3}, {2, 5, 25}},
				{{15, 0, 3}, {22, 3, 0}},
				{{23, 0, 0}, {18, 3, 3}},
				{{7, 0, 25}, {19, 3, 3}, {24, 5, 0}},
				{{21, 0, 3}, {22, 11, 3}, {23, 14, 3}, {24, 25, 3}}
		};
		
		ArrayList<ArrayList<Intersection>> vertInt = new ArrayList<ArrayList<Intersection>>(vert.length);
		for(int i = 0; i < vert.length; i++) {
			vertInt.add(new ArrayList<Intersection>());
		}
		for(int i = 0; i < horiz.length; i++) {
			int[][] inters = horizIntCoors[i];
			ArrayList<Intersection> intersections = new ArrayList<Intersection>();
			for(int j = 0; j < inters.length; j++) {
				int[] inter = inters[j];
				inter[0] = inter[0] - 1; //correct for starting at 1
				intersections.add(new Intersection(horiz[i], vert[inter[0]], inter[1]*PAC_UNIT, inter[2]*PAC_UNIT));
				vertInt.get(inter[0]).add(new Intersection(vert[inter[0]], horiz[i], inter[2]*PAC_UNIT, inter[1]*PAC_UNIT));
			}
			//tunnel
			if(i == 8) {
				intersections.add(new Intersection(horiz[8], horiz[9], 0, 8*PAC_UNIT));
			}
			else if(i == 9) {
				intersections.add(new Intersection(horiz[9], horiz[8], 8*PAC_UNIT, 0));
			}
			horiz[i].setIntersections(intersections);
		}
		for(int i = 0; i < vertInt.size(); i++) {
			vert[i].setIntersections(vertInt.get(i));
		}
		double[][] houseComp = {
				{10, 12, 0, 2},
				{12.5, 10, 0, 4},
				{15, 12, 0, 2},
				{10, 13, 5, 0}
		};
		int offset = path.size();
		for(int i = 0; i< houseComp.length; i++) {
			path.add(new Vector(new Point((int) (houseComp[i][0]*PAC_UNIT) +PAC_UNIT/2, (int) (houseComp[i][1]*PAC_UNIT) +PAC_UNIT/2), new Dimension((int) (houseComp[i][2]*PAC_UNIT), (int) (houseComp[i][3]*PAC_UNIT))));
		}
		double[][][] houseIntCoors = {
				{{4, 1, 0}},
				{{-offset + 8, 0, 4.5}}, // we intersect with the main path
				{{4, 1, 5}},
				{{2, 2.5, 3}}
		};
		for(int i = 0; i < houseIntCoors.length; i++) {
			path.get(i+offset).setIntersections(new ArrayList<Intersection>());
		}
		for(int i = 0; i < houseIntCoors.length; i++) {
			double[][] inters = houseIntCoors[i];
			for(int j = 0; j < inters.length; j++) {
				double[] inter = inters[j];
				inter[0] = inter[0] - 1; //correct for starting at 1
				path.get(i+offset).addIntersection((new Intersection(path.get(i+offset), path.get((int) inter[0]+offset), (int) (inter[1]*PAC_UNIT), (int) (inter[2]*PAC_UNIT))));
				path.get((int) inter[0]+offset).addIntersection(new Intersection(path.get((int) inter[0]+offset), path.get(i+offset), (int) inter[2]*PAC_UNIT, (int) (inter[1]*PAC_UNIT)));
			}
		}
	}
}