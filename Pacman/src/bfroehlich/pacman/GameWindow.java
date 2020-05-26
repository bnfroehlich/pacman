package bfroehlich.pacman;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameWindow extends JFrame {

	private Manager manager;
	private Board board;
	private BoardPanel boardPanel;
	private Engine engine;

	public GameWindow(Manager manager, String name) {
		super(name);
		this.manager = manager;
		this.board = new Board(manager);
		boardPanel = new BoardPanel(board, manager);
		engine = new Engine(board, boardPanel, manager);
		board.setEngine(engine);
		boardPanel.setEngine(engine);
		init();
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public Board getBoard() {
		return board;
	}
	
	public Engine getEngine() {
		return engine;
	}
	
	private void init() {
		setLayout(new BorderLayout());
		add(boardPanel, BorderLayout.CENTER);
	}
}