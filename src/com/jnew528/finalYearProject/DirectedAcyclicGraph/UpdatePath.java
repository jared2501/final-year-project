package com.jnew528.finalYearProject.DirectedAcyclicGraph;

import com.jnew528.finalYearProject.Move;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Jared
 * Date: 12/08/13
 * Time: 1:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class UpdatePath {

	private static Random random;

	static {
		random = new Random();
	}

	public static Edge uctSelectChild(Node node) {
		if(node.childEdges.size() == 0) {
			return null;
		}

		// Get parent visits
		double parentVisits = node.visits;

		// Find the highest uct edge from the child edges
		Edge selectedEdge = node.childEdges.get(0);
		double highestUctValue = Double.MIN_VALUE;
		for(Edge e : node.childEdges) {
			double newUctValue = calcUtcValue(parentVisits, e.visits, e.wins);
			if(newUctValue > highestUctValue) {
				highestUctValue = newUctValue;
				selectedEdge = e;
			}
		}

		return selectedEdge;
	}

	public static Move selectRobustRootMove(Node node) {
		Move selectedMove = null;
		int highestVisitCount = Integer.MIN_VALUE;

		for(Edge childEdge : node.getChildEdges()) {
			if(childEdge.visits > highestVisitCount) {
				highestVisitCount = childEdge.visits;
				selectedMove = childEdge.move;
			}
		}

		return selectedMove;
	}

	private static double calcUtcValue(double parentVisits, int childVists, double childWins) {
		return childWins/(1e-6 + childVists) + Math.sqrt( (2*Math.log((double)parentVisits)) / (1e-6 + childVists) ) + random.nextDouble()*1e-6;
	}

}
