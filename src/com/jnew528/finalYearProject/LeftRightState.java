package com.jnew528.finalYearProject;

import com.jnew528.finalYearProject.DirectedAcyclicGraph.Node;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Jared
 * Date: 15/07/13
 * Time: 1:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class LeftRightState implements GameState<LeftRightState, LeftRightMove> {
	private Integer playerJustMoved;
	private Integer size;
	private Integer player1R;
	private Integer player2R;
	private Integer iteration;

	LeftRightState() {
		this(300);
	}

	LeftRightState(Integer size) {
		this.playerJustMoved = 2;
		this.size = size;

		this.iteration = 0;
		this.player1R = 0;
		this.player2R = 0;
	}

	LeftRightState(LeftRightState clone) {
		this.playerJustMoved = clone.playerJustMoved;
		this.size = clone.size;

		this.iteration = clone.iteration;
		this.player1R = clone.player1R;
		this.player2R = clone.player2R;
	}

	@Override
	public Integer getPlayerJustMoved() {
		return this.playerJustMoved;
	}

	@Override
	public Integer getPlayerToMove() {
		return 3 - this.playerJustMoved;
	}

	@Override
	public boolean isFinalState(boolean quickCheck) {
		return this.iteration >= size;
	}

	@Override
	public Double getResult(Integer playerNumber, boolean quickCheck) {
		if(!isFinalState(quickCheck)) {
			return null;
		}

		if(this.player1R > this.player2R) {
			if(playerNumber == 1) {
				return 1.0;
			} else {
				return 0.0;
			}
		} else if(this.player1R < this.player2R) {
			if(playerNumber == 2) {
				return 1.0;
			} else {
				return 0.0;
			}
		}

		return 0.5;
	}

	@Override
	public Integer getWinner(boolean quickCheck) {
		if(!isFinalState(quickCheck)) {
			return null;
		}

		if(this.player1R > this.player2R) {
			return 1;
		} else if(this.player1R < this.player2R) {
			return 2;
		}

		return 0;
	}

	@Override
	public GameState createChildStateFromMove(LeftRightMove move) {
		if(isFinalState(true)) {
			System.out.println("Game of left right has already ended...");
			return null;
		}

		LeftRightState gameState = new LeftRightState(this);
		gameState.playerJustMoved = 3 - gameState.playerJustMoved;

		// Even iterations = player 1
		// Else if Odd iterations = player 2
		if(gameState.iteration % 2 == 0 && move.getRight()) {
			gameState.player1R++;
		} else if(move.getRight()) {
			gameState.player2R++;
		}

		gameState.iteration++;

		return gameState;
	}

	@Override
	public Vector<LeftRightMove> getChildMoves() {
		Vector<LeftRightMove> output = new Vector<LeftRightMove>();

		if(isFinalState(true)) {
			return output;
		}

		output.add(new LeftRightMove("L"));
		output.add(new LeftRightMove("R"));
		return output;
	}

	@Override
	public Node getTransposition(HashMap<LeftRightState, Node> set) {
		if(set.containsKey(this)) {
			return set.get(this);
		}

		return null;
	}

	@Override
	public LeftRightMove convertMove(LeftRightState transposition, LeftRightMove move) {
		return move;  // Since transpositions dont affect the move
	}


	@Override
	public String toString() {
		return "Size: " + size + "; Player1R: " + player1R + "; Player2R: " + player2R + "; Iteration: " + iteration;
	}

	@Override
	public boolean equals(Object other){
		if(other == null) return false;
		if(other == this) return true;
		if(this.getClass() != other.getClass()) return false;
		LeftRightState otherLeftRightState = (LeftRightState)other;
		return otherLeftRightState.playerJustMoved == this.playerJustMoved
				&& otherLeftRightState.size == this.size
				&& otherLeftRightState.player1R == this.player1R
				&& otherLeftRightState.player2R == this.player2R
				&& otherLeftRightState.iteration == this.iteration;
	}

	@Override
	public int hashCode() {
		final int prime = 17;
		int result = 1;

		// Dont need player just moved in hashcode since board implicitly has this in it
		result = prime * result + playerJustMoved.hashCode();
		result = prime * result + size.hashCode();
		result = prime * result + player1R.hashCode();
		result = prime * result + player2R.hashCode();
		result = prime * result + iteration.hashCode();

		return result;
	}
}
