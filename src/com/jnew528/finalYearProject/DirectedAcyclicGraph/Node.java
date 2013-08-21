package com.jnew528.finalYearProject.DirectedAcyclicGraph;

import com.jnew528.finalYearProject.GameState;
import com.jnew528.finalYearProject.Move;

import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Jared
 * Date: 12/08/13
 * Time: 1:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class Node {
	protected GameState gameState;
	protected Vector<Move> untriedMoves;
	protected Vector<Edge> parentEdges;
	protected Vector<Edge> childEdges;
	protected double visits;
	protected double wins;

	public Node(GameState gameState) {
		this.gameState = gameState;
		this.untriedMoves = gameState.getChildMoves();
		this.parentEdges = new Vector();
		this.childEdges = new Vector();
		visits = 0;
		wins = 0;
	}


	public boolean hasUntriedMoves() {
		return untriedMoves.size() > 0;
	}

	public boolean hasChildren() {
		return childEdges.size() > 0;
	}

	public Vector<Move> getUntriedMoves() {
		return untriedMoves;
	}

	public GameState getGameState() {
		return gameState;
	}

	public Vector<Edge> getChildEdges() {
		return childEdges;
	}

	public Vector<Edge> getParentEdges() {
		return parentEdges;
	}

	// Adds a child and returns the edges between this node and the child node
	public Edge addChild(Node child, Move move) {
		Edge existingChildEdge = this.getChildEdge(child, move);

		// If the child we're trying to add doesnt exist in the tree, then add it
		if(existingChildEdge == null) {
			Edge newEdge = new Edge(0, 0, this, child, move);
			this.removeUntriedMove(move);
			this.addChildEdge(newEdge);
			child.addParentEdge(newEdge);
			return newEdge;
		} else {
			return existingChildEdge;
		}
	}

	public void update(double win, double visit) {
		this.wins += win;
		this.visits += visit;
	}

	private void addChildEdge(Edge edge) {
		this.childEdges.add(edge);
	}

	private void addParentEdge(Edge edge) {
		this.parentEdges.add(edge);
	}

	private void removeUntriedMove(Move move) {
		this.untriedMoves.remove(move);
	}

	private Edge getChildEdge(Node child, Move move) {
		for(Edge e : childEdges) {
			if(e.head == child && e.move == move) {
				return e;
			}
		}

		return null;
	}
}