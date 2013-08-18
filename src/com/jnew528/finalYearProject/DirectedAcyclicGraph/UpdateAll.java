package com.jnew528.finalYearProject.DirectedAcyclicGraph;

import com.jnew528.finalYearProject.Move;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Jared
 * Date: 16/08/13
 * Time: 1:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateAll {

	private static Random random;

	static {
		random = new Random();
	}

	public static Node uctSelectChild(Node node) {
		if(node.childEdges.size() == 0) {
			return null;
		}

		// Get parent visits
		double parentVisits = node.visits;

		// Find the highest uct edge from the child edges
		Node selectedChild = node.childEdges.get(0).head;
		double highestUctValue = Double.MIN_VALUE;

		for(Edge e : node.childEdges) {
			Node child = e.head;

			double newUctValue = calcUtcValue(parentVisits, child.visits, child.wins);

			if(newUctValue > highestUctValue) {
				highestUctValue = newUctValue;
				selectedChild = child;
			}
		}

		return selectedChild;
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

	private static double calcUtcValue(double parentVisits, double childVists, double childWins) {
		return childWins/(1e-6 + childVists) + Math.sqrt( (2*Math.log(parentVisits)) / (1e-6 + childVists) ) + random.nextDouble()*1e-6;
	}

}
