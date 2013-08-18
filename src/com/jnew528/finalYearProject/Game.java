package com.jnew528.finalYearProject;

import java.util.Vector;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Jared
 * Date: 14/07/13
 * Time: 11:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class Game implements Callable<Game> {

	private GameState gameState;
	private MctsTree player1;
	private MctsTree player2;
	private Integer iterations;
	private boolean verbose;
	private long duration;
	private Vector<Integer> collisionsPlayer1;
	private Vector<Integer> collisionsPlayer2;
	private String player1Type;
	private String player2Type;
	private Vector<Long> timePerPlayer1Move;
	private Vector<Long> timePerPlayer2Move;

	Game(GameState gameState, MctsTree player1, MctsTree player2, Integer iterations, boolean verbose) {
		this.gameState = gameState;
		this.player1 = player1;
		this.player2 = player2;
		this.iterations = iterations;
		this.verbose = verbose;
		this.collisionsPlayer1 = new Vector();
		this.collisionsPlayer2 = new Vector();
		this.player1Type = player1.getClass().getName();
		this.player2Type = player2.getClass().getName();
		this.timePerPlayer1Move = new Vector();
		this.timePerPlayer2Move = new Vector();
	}

	@Override
	public Game call() {
		if(verbose) {
			System.out.println("Game started");
		}

		long startTime = System.nanoTime();

		while(!gameState.isFinalState(false)) {
			long startMoveTime = System.nanoTime();
			Move move = null;

			if( gameState.getPlayerToMove() ==  1) {
				if(verbose) {
					System.out.println(gameState);
				}

				move = player1.search(gameState, this.iterations);
				collisionsPlayer1.add(player1.getCollisions());
			} else {
				if(verbose) {
					System.out.println(gameState);
				}

				move = player2.search(gameState, this.iterations);
				collisionsPlayer2.add(player2.getCollisions());
			}

			long endMoveTime = System.nanoTime();
			long duration = (endMoveTime - startMoveTime)/1000000;
			if( gameState.getPlayerToMove() ==  1) {
				timePerPlayer1Move.add(duration);
			} else {
				timePerPlayer2Move.add(duration);
			}

			gameState = gameState.createChildStateFromMove(move);
		}

		long endTime = System.nanoTime();
		this.duration = endTime - startTime;

		if(verbose) {
			System.out.println("Game finished");
			System.out.println(gameState);
			System.out.println("Winner is player " + gameState.getWinner(false));
			System.out.println("Duration: " + (double) duration/1000000000);
		}

		// Clear players since this object may stick around and we dont need references to these
		// GC can get rid of these objects
		this.player1 = null;
		this.player2 = null;

		return this;
	}

	public GameState getGameState() {
		return this.gameState;
	}

	public long getDuration() {
		return this.duration;
	}

	public Integer getIterations() {
		return this.iterations;
	}

	public Vector<Integer> getPlayer1Collisions() {
		return this.collisionsPlayer1;
	}

	public Vector<Integer> getPlayer2Collisions() {
		return this.collisionsPlayer2;
	}

	public String getPlayer1Type() {
		return this.player1Type;
	}

	public String getPlayer2Type() {
		return this.player2Type;
	}

	public Vector<Long> getTimePerPlayer1Move() {
		return this.timePerPlayer1Move;
	}

	public Vector<Long> getTimePerPlayer2Move() {
		return this.timePerPlayer2Move;
	}
}
