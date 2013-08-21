package com.jnew528.finalYearProject.DirectedAcyclicGraph;

import com.jnew528.finalYearProject.Move;

import java.util.Random;

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
			double biasTerm = Math.sqrt((2*Math.log((double)parentVisits)) / (1e-6 + e.visits)) + random.nextDouble()*1e-6;
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

	public static Move selectRobustRootMove(Node node) {
		Move selectedMove = null;
		Double highestVisitCount = Double.MIN_VALUE;

		for(Edge childEdge : node.getChildEdges()) {
			if(childEdge.head.visits > highestVisitCount) {
				highestVisitCount = childEdge.head.visits;
				selectedMove = childEdge.move;
			}
		}

		return selectedMove;
	}
}
