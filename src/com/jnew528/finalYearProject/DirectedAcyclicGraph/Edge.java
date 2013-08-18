package com.jnew528.finalYearProject.DirectedAcyclicGraph;

import com.jnew528.finalYearProject.Move;

/**
 * Created with IntelliJ IDEA.
 * User: Jared
 * Date: 12/08/13
 * Time: 1:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class Edge {
	protected int visits;
	protected double wins;
	protected Node tail;
	protected Node head;
	protected Move move;

	public Edge(int visits, double wins, Node tail, Node head, Move move) {
		this.visits = visits;
		this.wins = wins;
		this.tail = tail;
		this.head = head;
		this.move = move;
	}

	public void update(double result) {
		this.visits++;
		this.wins += result;
	}

	public Node getTail() {
		return tail;
	}

	public Node getHead() {
		return head;
	}
}