package com.jnew528.finalYearProject;

import com.jnew528.finalYearProject.DirectedAcyclicGraph.Edge;
import com.jnew528.finalYearProject.DirectedAcyclicGraph.Node;
import com.jnew528.finalYearProject.DirectedAcyclicGraph.Policies;

import java.util.Random;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Jared
 * Date: 5/07/13
 * Time: 4:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class MctsTreeStd implements MctsTree {
	protected static Random random;

	static {
		random = new Random();
	}

	MctsTreeStd() {}

	public Move search(GameState gameState, int iterationCount) {
		Node root = new Node(gameState);

		for(int i = 0; i < iterationCount; i++) {
			performIteration(root);
		}

		// Select child with the selection policy
		// In this case, the child with the highest number of visits
		return Policies.selectRobustRootMove(root);
	}

	public void performIteration(Node root) {
		Node node = root;

		// Traverse the tree until we reach an expandable node
		// ie a node that has untried moves and non-terminal
		while(!node.hasUntriedMoves() && node.hasChildren()) {
			node = Policies.uct0SelectChild(node).getHead();
		}

		// Expand the node if it has untried moves
		// if it doesnt, do nothing, because it is a terminal state and doesnt need expanding
		if(node.hasUntriedMoves()) {
			Move move = node.getUntriedMoves().get(random.nextInt(node.getUntriedMoves().size()));
			GameState newGameState = node.getGameState().createChildStateFromMove(move);

			// Remove the move that we used to expand the node and add the new node generated to the tree
			Node newNode = new Node(newGameState);
			node.addChild(newNode, move);
			node = newNode;
		}

		// Play a random game from the current node using the default policy
		// in this case, default policy is to select random moves until a final state is reached
		GameState gameState = node.getGameState();
		while(!gameState.isFinalState(true)) {
			Vector<Move> moves = gameState.getChildMoves();
			Move move = moves.get(random.nextInt(moves.size()));
			gameState = gameState.createChildStateFromMove(move);
		}

		// Back propogate the result from the perspective of the player that just moved
		// Were using updateall so update the nodes!!!
		do {
			double result = gameState.getResult(node.getGameState().getPlayerJustMoved(), true);
			node.updateEV(result, 1.0);
			node.incrementVisits();

			// Since each node should only have one parent edge!
			assert(node.getParentEdges().size() == 1 || node.getParentEdges().size() == 0);

			if(node.getParentEdges().size() == 0) {
				break;
			} else {
				// Update the edge as well for shits and giggles
				Edge parentEdge = node.getParentEdges().get(0);
				parentEdge.updateEV(result, 1.0);
				parentEdge.incrementVisits();
				node = parentEdge.getTail();
			}
		} while (true);
	}

	public int getCollisions() {
		return 0;
	}
}
