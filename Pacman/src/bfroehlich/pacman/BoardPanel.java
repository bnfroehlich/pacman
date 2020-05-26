package bfroehlich.pacman;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JPanel;

public class BoardPanel extends JPanel {

	private static final int MARGIN = 20;
	private Board board;
	private Engine engine;
	private Manager manager;
	private ArrayList<Fruit> levelSymbols;
	private int highScore;
	
	public BoardPanel(Board board, Manager manager) {
		this.board = board;
		this.manager = manager;
		init();
		highScore = manager.getHighScore();
	}
	
	private void init() {
		setLayout(new BorderLayout());
		JPanel center = new JPanel();
		center.setLayout(new BorderLayout());
		JPanel leftMargin = new JPanel() {
			public void paint(Graphics g) {
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		leftMargin.setPreferredSize(new Dimension(MARGIN, Board.BOARD_SIZE.height));
		center.add(leftMargin, BorderLayout.WEST);
		center.add(board, BorderLayout.CENTER);
		JPanel rightMargin = new JPanel() {
			public void paint(Graphics g) {
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		rightMargin.setPreferredSize(new Dimension(MARGIN, Board.BOARD_SIZE.height));
		center.add(rightMargin, BorderLayout.EAST);
		add(center, BorderLayout.CENTER);
		
		JPanel score = new JPanel() {
			public void paint(Graphics g) {
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, getWidth(), getHeight());
				g.setColor(Color.WHITE);
				g.setFont(new Font("Times New Roman", Font.PLAIN, 16));
				g.drawString("Score: " + engine.getScore(), MARGIN, 15);
				g.drawString("Highscore: " + highScore, getSize().width-140, 15);
			}
		};
		score.setPreferredSize(new Dimension(Board.BOARD_SIZE.width, 25));
		add(score, BorderLayout.NORTH);
		JPanel lives = new JPanel() {
			public void paint(Graphics g) {
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, getWidth(), getHeight());
				int lives = engine.getLivesLeft();
				Image pacman = Pacman.getPacman();
                Graphics g2 = g.create();
				g2.translate(30, 5);
				for(int i = 0; i < lives; i++) {
					g2.drawImage(pacman, 0, 0, this);
					g2.translate(pacman.getWidth(this)+5, 0);
				}
				g.translate(getSize().width-MARGIN, 5);
		        Iterator<Fruit> fit = new ArrayList<Fruit>(levelSymbols).iterator();
		        while (fit.hasNext()) {
		            Fruit f = fit.next();
		            if(f != null) {
		                g.translate(-f.getSize().width, 0);
		                Graphics gtemp = g.create();
		                gtemp.translate(f.getSize().width/2, f.getSize().height/2);
		                f.paint(gtemp);
		            }
		        }
			}
		};
		lives.setPreferredSize(new Dimension(Board.BOARD_SIZE.width, 35));
		add(lives, BorderLayout.SOUTH);
		levelSymbols = new ArrayList<Fruit>();
	}
	
	public void setEngine(Engine engine) {
		this.engine = engine;
	}
	
	public void addLevelSymbol(Fruit fruit) {
		levelSymbols.add(fruit);
	}
	
	public void clearLevelSymbols() {
		levelSymbols.clear();
		highScore = manager.getHighScore();
	}
}