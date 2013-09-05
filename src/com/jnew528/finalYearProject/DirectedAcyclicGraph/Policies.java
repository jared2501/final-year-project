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
		double parentVisits = node.actualVisits;

		// Find the highest uct edge from the child edges
		Edge selectedEdge = node.childEdges.get(0);
		double highestUctValue = -Double.MAX_VALUE;

		for(Edge e : node.childEdges) {
			Node child = e.head;
			double Qsa = child.wins / (child.visits + 1e-10);
			double biasTerm = Math.sqrt((2*Math.log((double)parentVisits)) / (1e-6 + child.actualVisits)) + random.nextDouble()*1e-6;
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
		double parentVisits = node.actualVisits;

		// Find the highest uct edge from the child edges
		Edge selectedEdge = null;
		double highestUctValue = -Double.MAX_VALUE;

		for(Edge e : node.childEdges) {
			double Qsa = e.wins / (e.visits + 1e-10);
			double biasTerm = Math.sqrt((2*Math.log((double)parentVisits)) / (1e-6 + e.actualVisits)) + random.nextDouble()*1e-6;
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
		double parentVisits = node.actualVisits;

		// Find the highest uct edge from the child edges
		Edge selectedEdge = null;
		double highestUctValue = -Double.MAX_VALUE;

		for(Edge e : node.childEdges) {
			Node child = e.head;
			double Qgsa = child.wins / (child.visits + 1e-10); // This is the main point that differs!
			double biasTerm = Math.sqrt((2*Math.log((double)parentVisits)) / (1e-6 + e.actualVisits)) + random.nextDouble()*1e-6;
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
		double parentVisits = node.actualVisits;

		// Find the highest uct edge from the child edges
		Edge selectedEdge = node.childEdges.get(0);
		double highestUctValue = Double.MIN_VALUE;

		for(Edge e : node.childEdges) {
			Node child = e.head;
			double Qgsa = child.wins / (child.visits + 1e-10); // This is the main point that differs!
			double biasTerm = Math.sqrt((2*Math.log((double)parentVisits)) / (1e-6 + child.actualVisits)) + random.nextDouble()*1e-6;
			double newUctValue = Qgsa + biasTerm;

			if(newUctValue > highestUctValue) {
				highestUctValue = newUctValue;
				selectedEdge = e;
			}
		}

		return selectedEdge;
	}


	public static Edge uct3SelectChild(Node node) {
		if(node.childEdges.size() == 0) {
			return null;
		}

		// Get parent visits
		double parentVisits = node.actualVisits;

		// Find the highest uct edge from the child edges
		Edge selectedEdge = node.childEdges.get(0);
		double highestUctValue = Double.MIN_VALUE;

		for(Edge e : node.childEdges) {
			Node child = e.head;
			double Qgsa = uct3(e.getHead(), e.getHead().getGameState().getPlayerJustMoved()); // This is the main point that differs!
			double biasTerm = Math.sqrt((2*Math.log((double)parentVisits)) / (1e-6 + child.actualVisits)) + random.nextDouble()*1e-6;
			double newUctValue = Qgsa + biasTerm;

			if(newUctValue > highestUctValue) {
				highestUctValue = newUctValue;
				selectedEdge = e;
			}
		}

		return selectedEdge;
	}

	private static double uct3(Node node, int playerJustMoved) {
		if(node.getChildEdges().size() < 1) {
			if(playerJustMoved == node.getGameState().getPlayerJustMoved()) {
				return node.wins/(node.visits + 1e-10);
			} else {
				return 1-(node.wins/(node.visits + 1e-10));
			}
		} else {
			double sum = 0.0;

			for(Edge e : node.getChildEdges()) {
				Node child = e.getHead();
				sum = sum + (e.actualVisits*uct3(child, playerJustMoved))/(node.actualVisits + 1e-6);
			}

			return sum;
		}
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
			int d2 = 2;
			int d3 = 2;
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

	public static void backPropagatePath(Node finalNode, GameState gameState, Vector<Edge> traversedEdges) {
		for(Edge e : traversedEdges) {
			double edgeResult = gameState.getResult(e.getHead().getGameState().getPlayerJustMoved(), false);
			e.updateEV(edgeResult, 1.0);
			e.incrementVisits();

			// Update tail since we update the final node below, so we need to update the root
			Node tail = e.getTail();
            double tailResult = gameState.getResult(tail.getGameState().getPlayerJustMoved(), false);
            tail.updateEV(tailResult, 1.0);
            tail.incrementVisits();
		}

		double result = gameState.getResult(finalNode.getGameState().getPlayerJustMoved(), false);
		finalNode.updateEV(result, 1.0);
		finalNode.incrementVisits();
	}


	public static void backPropagatePath_ModifyAggAllNumChild(Node finalNode, GameState gameState, Vector<Edge> traversedEdges) {
		Deque<Node> currentLevelQueue = new ArrayDeque();
		HashMap<Node, Double> currentLevelHash = new HashMap();

		currentLevelQueue.addFirst(finalNode);
		currentLevelHash.put(finalNode, 1.0);

		while(currentLevelQueue.size() > 0) {
			Deque<Node> nextLevelQueue = new ArrayDeque();
			HashMap<Node, Double> nextLevelHash = new HashMap();

			for(Node current : currentLevelQueue) {
				Double currentVists = currentLevelHash.get(current);
				Double currentResult = gameState.getResult(current.getGameState().getPlayerJustMoved(), false);

				// Update the current node
				current.updateEV(currentResult*currentVists, currentVists);

				double numOfParents = (double)current.getParentEdges().size();
				Double parentVisits = currentVists/numOfParents;

				// Go through its parents and dehoover their whatsits for next round
				for(Edge e : current.getParentEdges()) {
					// Update the current nodes parent edges with their parent score!
					e.updateEV(currentResult*parentVisits, parentVisits);

					Node parent = e.getTail();

					if(nextLevelHash.containsKey(parent)) {
						nextLevelHash.put(parent, nextLevelHash.get(parent)+parentVisits);
					} else {
						nextLevelQueue.addFirst(parent);
						nextLevelHash.put(parent, parentVisits);
					}
				}
			}

			currentLevelHash = nextLevelHash;
			currentLevelQueue = nextLevelQueue;
		}

		// Increment visits along path for bias term
		for(Edge e : traversedEdges) {
			e.incrementVisits();
			e.getTail().incrementVisits();
		}
		finalNode.incrementVisits();
	}


	public static void backPropagatePath_ModifyAggAll(Node finalNode, GameState gameState, Vector<Edge> traversedEdges) {
		HashSet<Node> seenNodes = new HashSet();
		Deque<Node> stack = new ArrayDeque();

		stack.push(finalNode);

		while(stack.size() > 0) {
			Node current = stack.pop();

			if(!seenNodes.contains(current)) {
				// Update current node ONLY if we havent seen it before
				double result = gameState.getResult(current.getGameState().getPlayerJustMoved(), true);
				current.updateEV(result, 1.0);
				seenNodes.add(current);

				// and add parents to the stack if we havent seen them
				for(Edge e : current.getParentEdges()) {
					e.updateEV(result, 1.0);
					Node parent = e.getTail();
					stack.push(parent);
				}
			}
		}

		// Increment visits along path for bias term
		for(Edge e : traversedEdges) {
			e.incrementVisits();
			e.getTail().incrementVisits();
		}
		finalNode.incrementVisits();
	}


//	public static void backPropagatePath_UCT3fail(Node finalNode, GameState gameState, Vector<Edge> traversedEdges) {
//
//
//		// Increment visits along path for bias term
//		for(Edge e : traversedEdges) {
//			e.incrementVisits();
//			e.getTail().incrementVisits();
//		}
//		finalNode.incrementVisits();
//
//		// Update aggregations
//		Set<Node> traversedNodesSet = new HashSet();
//		for(Edge e : traversedEdges) {
//			traversedNodesSet.add(e.getTail());
//		}
//		traversedNodesSet.add(finalNode);
//
//		Deque<Node> currentLevelQueue = new ArrayDeque();
//		HashMap<Node, Double> deltaQChildren = new HashMap();
//
//		currentLevelQueue.addFirst(finalNode);
//
//		while(currentLevelQueue.size() > 0) {
//			HashMap<Node, Double> deltaQCurrent = new HashMap<Node, Double>();
//			Deque<Node> nextLevelQueue = new ArrayDeque();
//
//			Set<Node> nextLevelSet = new HashSet<Node>();
//
//			for(Node current : currentLevelQueue) {
//				double deltaQ;
//
//				if(finalNode == current) {
//					deltaQ = gameState.getResult(finalNode.getGameState().getPlayerJustMoved(), false);
//				} else if(traversedNodesSet.contains(current)) {
//					Node gsa = null;
//
//					for(Edge e : traversedEdges) {
//						if(current == e.getTail()) {
//							gsa = e.getTail();
//							break;
//						}
//					}
//
//					deltaQ = (1.0-(gsa.wins/gsa.visits))/current.actualVisits;
//				} else {
//					deltaQ = 0;
//					for(Edge e : current.getChildEdges()) {
//						if(deltaQChildren.containsKey(e.getHead())) {
//							double deltaQgsa = (1-deltaQChildren.get(e.getHead()));
//							deltaQ = deltaQ + (e.actualVisits*deltaQgsa)/current.actualVisits;
//						}
//					}
//				}
//
//				if(current.visits < 0.5) {
//					current.updateEV(deltaQ, 1.0);
//				} else {
//					current.updateEV(deltaQ, 1.0);
//				}
//				deltaQCurrent.put(current, deltaQ);
//
//				// Go through its parents and dehoover their whatsits for next round
//				for(Edge e : current.getParentEdges()) {
//					// Update the current nodes parent edges with their parent score!
//					if(e.visits < 0.5) {
//						e.updateEV(deltaQ, 1.0);
//					} else {
//						e.updateEV(deltaQ, 1.0);
//					}
//
//					Node parent = e.getTail();
//
//					if(!nextLevelSet.contains(parent) && (e.actualVisits > 0 || traversedNodesSet.contains(parent))) {
//						nextLevelSet.add(parent);
//						nextLevelQueue.addFirst(parent);
//					}
//				}
//			}
//
//			deltaQChildren = deltaQCurrent;
//			currentLevelQueue = nextLevelQueue;
//		}
//	}


	public static Move selectRobustRootMove(Node node) {
		Move selectedMove = null;
		int highestVisitCount = -1;

		for(Edge childEdge : node.getChildEdges()) {
			if(childEdge.actualVisits > highestVisitCount) {
				highestVisitCount = childEdge.actualVisits;
				selectedMove = childEdge.move;
			}
		}

		return selectedMove;
	}

    public static Move selectMaxRootMove(Node node) {
        Move selectedMove = null;
        double highestValue = -Double.MAX_VALUE;

        for(Edge childEdge : node.getChildEdges()) {
            if(childEdge.wins/(childEdge.visits + 1e-10) > highestValue) {
                highestValue = childEdge.wins/(childEdge.visits + 1e-10);
                selectedMove = childEdge.move;
            }
        }

        return selectedMove;
    }
}
