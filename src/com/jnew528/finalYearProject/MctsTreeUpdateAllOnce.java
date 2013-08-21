package com.jnew528.finalYearProject;

import com.jnew528.finalYearProject.DirectedAcyclicGraph.Edge;
import com.jnew528.finalYearProject.DirectedAcyclicGraph.Node;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Jared
 * Date: 5/07/13
 * Time: 4:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class MctsTreeUpdateAllOnce extends MctsTreeUpdateAll {

	@Override
	protected void backpropogate(Node finalNode, GameState gameState, Vector<Edge> traversedEdges) {
		HashSet<Node> seenNodes = new HashSet();
		Deque<Node> stack = new ArrayDeque();

		stack.push(finalNode);

		while(stack.size() > 0) {
			Node current = stack.pop();

			if(!seenNodes.contains(current)) {
				// Update current node ONLY if we havent seen it before
				double result = gameState.getResult(current.getGameState().getPlayerJustMoved(), true);
				current.update(result, 1.0);
				seenNodes.add(current);

				// and add parents to the stack if we havent seen them
				for(Edge e : current.getParentEdges()) {
					Node parent = e.getTail();
					stack.push(parent);
				}
			}
		}

		// Update all the traversed edges with the result
		for(Edge e : traversedEdges) {
			double result = gameState.getResult(e.getHead().getGameState().getPlayerJustMoved(), true);
			e.update(result, 1.0);
		}
	}
}
