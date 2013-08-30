package com.jnew528.finalYearProject.DirectedAcyclicGraph;

import com.jnew528.finalYearProject.GameState;
import com.jnew528.finalYearProject.Move;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Jared
 * Date: 21/08/13
 * Time: 5:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class Policies {
	private static Random random;

	static {
		random = new Random();
	}

	// Edges ONLY store the update path!!!
	// Nodes store estimated values based on all update paths!!!!

	public static Edge uct0SelectChild(Node node) {
		if(node.childEdges.size() == 0) {
			return null;
		}

		// Get parent visits
		double parentVisits = node.visits;

		// Find the highest uct edge from the child edges
		Edge selectedEdge = node.childEdges.get(0);
		double highestUctValue = Double.MIN_VALUE;

		for(Edge e : node.childEdges) {
			Node child = e.head;
			double Qsa = child.wins / (child.visits + 1e-10);
			double biasTerm = Math.sqrt((2*Math.log((double)parentVisits)) / (1e-6 + child.visits)) + random.nextDouble()*1e-6;
			double newUctValue = Qsa + biasTerm;

			if(newUctValue > highestUctValue) {
				highestUctValue = newUctValue;
				selectedEdge = e;
			}
		}

		return selectedEdge;
	}

	// I.e. update path utc variant!!
	public static Edge uct1SelectChild(Node node) {
		if(node.childEdges.size() == 0) {
			return null;
		}

		// Get parent visits
		double parentVisits = node.visits;

		// Find the highest uct edge from the child edges
		Edge selectedEdge = node.childEdges.get(0);
		double highestUctValue = Double.MIN_VALUE;

		for(Edge e : node.childEdges) {
			double Qsa = e.wins / (e.visits + 1e-10);
			double biasTerm = Math.sqrt((Math.log((double)parentVisits)) / (1e-6 + e.visits)) + random.nextDouble()*1e-6;
			double newUctValue = Qsa + biasTerm;

			if(newUctValue > highestUctValue) {
				highestUctValue = newUctValue;
				selectedEdge = e;
			}
		}

		return selectedEdge;
	}

	// I.e. Update all approach
	public static Edge uct2SelectChild(Node node) {
		if(node.childEdges.size() == 0) {
			return null;
		}

		// Get parent visits
		double parentVisits = node.visits;

		// Find the highest uct edge from the child edges
		Edge selectedEdge = node.childEdges.get(0);
		double highestUctValue = Double.MIN_VALUE;

		for(Edge e : node.childEdges) {
			Node child = e.head;
			double Qgsa = child.wins / (child.visits + 1e-10); // This is the main point that differs!
			double biasTerm = Math.sqrt((2*Math.log((double)parentVisits)) / (1e-6 + e.visits)) + random.nextDouble()*1e-6;
			double newUctValue = Qgsa + biasTerm;

			if(newUctValue > highestUctValue) {
				highestUctValue = newUctValue;
				selectedEdge = e;
			}
		}

		return selectedEdge;
	}

	public static Edge uct2bSelectChild(Node node) {
		if(node.childEdges.size() == 0) {
			return null;
		}

		// Get parent visits
		double parentVisits = node.visits;

		// Find the highest uct edge from the child edges
		Edge selectedEdge = node.childEdges.get(0);
		double highestUctValue = Double.MIN_VALUE;

		for(Edge e : node.childEdges) {
			Node child = e.head;
			double Qgsa = child.wins / (child.visits + 1e-10); // This is the main point that differs!
			double biasTerm = Math.sqrt((2*Math.log((double)parentVisits)) / (1e-6 + child.visits)) + random.nextDouble()*1e-6;
			double newUctValue = Qgsa + biasTerm;

			if(newUctValue > highestUctValue) {
				highestUctValue = newUctValue;
				selectedEdge = e;
			}
		}

		return selectedEdge;
	}


	public static Edge uctParameterizedSelectChild(Node node) {
		if(node.childEdges.size() == 0) {
			return null;
		}

		// Find the highest uct edge from the child edges
		Edge selectedEdge = node.childEdges.get(0);
		double highestUctValue = Double.MIN_VALUE;
		int player = node.getGameState().getPlayerToMove();

		for(Edge e : node.childEdges) {
			int d1 = 10000;
			int d2 = 1;
			int d3 = 1;
			double c = 1;

			double newUctValue = mu(d1, e, player) + c * Math.sqrt((Math.log(p(d2,e)))/(n(d3, e) + 1e-10)) + random.nextDouble()*1e-6;

			if(newUctValue > highestUctValue) {
				highestUctValue = newUctValue;
				selectedEdge = e;
			}
		}

		return selectedEdge;
	}

	private static double mu(int depth, Edge e, int player) {
		int playerOnEdge = e.getTail().getGameState().getPlayerToMove();

		if(depth < 1 || e.getHead().getChildEdges().size() < 1) {
			if(player == playerOnEdge) {
				return e.wins/(e.visits + 1e-10);
			} else {
				return 1.0 - e.wins/(e.visits + 1e-10);
			}
		}

		double sumNumerator = 0.0;
		double sumDenominator = 0.0;

		for(Edge childEdge : e.getHead().getChildEdges()) {
			sumNumerator+= (mu(depth-1, childEdge, player) * childEdge.visits);
			sumDenominator += childEdge.visits;
		}

		Node missing = e.getHead();
		if(missing.getGameState().getPlayerJustMoved() == player) {
			sumNumerator = missing.wins + sumNumerator;
		} else {
			sumNumerator = (1.0-missing.wins) + sumNumerator;
		}
		sumDenominator = 1.0 + sumDenominator;

		if(Double.isNaN(sumDenominator) || Double.isNaN(sumNumerator)) {
			System.out.print("NANFOUND!!!");
		}

		return (sumNumerator)/(sumDenominator + 1e-10);
	}

	private static double n(int depth, Edge e) {
		if(depth < 1 || e.getHead().getChildEdges().size() < 1) {
			return e.visits;
		}

		double sum = 0;

		for(Edge child : e.getHead().getChildEdges()) {
			sum += n(depth-1, child);
		}

		return e.getTail().visits + sum;
	}

	private static double p(int depth, Edge e) {
		double sum = 0;

		for(Edge child : e.getTail().getChildEdges()) {
			sum += n(depth, child);
		}

		return sum;
	}



    // Backpropagation

	public static void backpropogateUpPath(Node finalNode, GameState gameState, Vector<Edge> traversedEdges) {
		for(Edge e : traversedEdges) {
			double result = gameState.getResult(e.getHead().getGameState().getPlayerJustMoved(), false);
			e.update(result, 1.0);
		}

		double result = gameState.getResult(finalNode.getGameState().getPlayerJustMoved(), false);
		finalNode.update(result, 1.0);
	}


	public static Move selectRobustRootMove(Node node) {
		Move selectedMove = null;
		Double highestVisitCount = Double.MIN_VALUE;

		for(Edge childEdge : node.getChildEdges()) {
			if(childEdge.visits > highestVisitCount) {
				highestVisitCount = childEdge.visits;
				selectedMove = childEdge.move;
			}
		}

		return selectedMove;
	}
}
