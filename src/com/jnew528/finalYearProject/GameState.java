package com.jnew528.finalYearProject;

import com.jnew528.finalYearProject.DirectedAcyclicGraph.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

// All implementations of this interface should be immutable
public interface GameState<G extends GameState, M extends Move> {
	// Get the player number that has just moved (1 or 2)
	public Integer getPlayerJustMoved();

	// Get the player whos turn it is to move
	public Integer getPlayerToMove();


	// Determines if the current game state is a final state
	// If inDefaultPolicy is true the game state does quick checking
	public boolean isFinalState(boolean quickCheck);

	// Gets the result of a final board state
	// Will return null if the board has no winner/looser/tie
	public Double getResult(Integer playerNumber, boolean quickCheck);

	// Returns the player number that has won for printing reasons
	// Will return null if the board has no winner/looser/tie
	public Integer getWinner(boolean quickCheck);



	// Creates a new game state with a move made from the opposite player to getPlayerJustMoved()
	// Returns null if an illegal move is passed in
	public GameState createChildStateFromMove(M move);

	// Get all legal child moves of the current state
	// NB/ Will be empty if there are no current possible moves. This usually indicates a final board state.
	// Can return child moves even in final state if the board allows this to happen (e.g. Hex)
	public Vector<M> getChildMoves();


	public Node getTransposition(HashMap<G, Node> set);

	// Converts the move from the transposition into a move on this board
	public M convertMove(G transposition, M m);
}