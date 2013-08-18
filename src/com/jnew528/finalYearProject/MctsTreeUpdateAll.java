package com.jnew528.finalYearProject;

import com.jnew528.finalYearProject.DirectedAcyclicGraph.Edge;
import com.jnew528.finalYearProject.DirectedAcyclicGraph.Node;
import com.jnew528.finalYearProject.DirectedAcyclicGraph.UpdateAll;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Jared
 * Date: 5/07/13
 * Time: 4:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class MctsTreeUpdateAll implements MctsTree {
	protected static Random random;
	protected int collisions;

	static {
		random = new Random();
	}

	public MctsTreeUpdateAll() {}

	@Override
	public Move search(GameState gameState, int iterationCount) {
		// We want to start a-fresh!
		HashMap<GameState, Node> encounteredGameStates = new HashMap();
		Node root = new Node(gameState);
		collisions = 0;

		encounteredGameStates.put(gameState, root);

		for(int i = 0; i < iterationCount; i++) {
			performIteration(root, encounteredGameStates);
		}

		return UpdateAll.selectRobustRootMove(root);
	}

	public void performIteration(Node root, HashMap<GameState, Node> encounteredGameStates) {
		Node node = root;

		// Traverse the tree until we reach a node on the edge of the current tree
		// i.e. it has untried moves or is a state with no children (i.e. terminal game state)
		select: do {
			node = utcSelect(node);

			// Expand the node if it has untried moves
			// if it doesnt, do nothing, because it is a terminal state and doesnt need expanding
			if(node.hasUntriedMoves()) {
				Move move = node.getUntriedMoves().get(random.nextInt(node.getUntriedMoves().size()));
				GameState newGameState = node.getGameState().createChildStateFromMove(move);

				// Check if there are any transpositions in the encountered states
				Node transposition = newGameState.getTransposition(encounteredGameStates);

				// If we have found a transposition, then make it a child of the current node and continue our iteration
				// from the new transposition
				if(transposition != null) {
					collisions++;
					node.addChild(transposition, move);
					node = transposition;
					continue select;
				} else {
					Node newNode = new Node(newGameState);
					Edge newEdge = node.addChild(newNode, move);
					node = newNode;
					encounteredGameStates.put(newGameState, newNode);
				}
			}

			GameState startingGameState = node.getGameState();
			GameState finalGameState = defaultPolicy(node, startingGameState);

			// Back propogate the result from the perspective of the player that just moved
			backpropogate(node, finalGameState);
			break;
		} while(true);
	}

	protected Node utcSelect(Node node) {
		while(!node.hasUntriedMoves() && node.hasChildren()) {
			node = UpdateAll.uctSelectChild(node);
		}

		return node;
	}

	protected GameState defaultPolicy(Node node, GameState gameState) {
		// Play a random game from the current node using the default policy
		// in this case, default policy is to select random moves until a final state is reached
		while(!gameState.isFinalState(true)) {
			Vector<Move> moves = gameState.getChildMoves();
			Move move = moves.get(random.nextInt(moves.size()));
			gameState = gameState.createChildStateFromMove(move);
		}
		return gameState;
	}

	protected void backpropogate(Node finalNode, GameState gameState) {
		Deque<Node> currentLevelQueue = new ArrayDeque();
		HashMap<Node, Double> currentLevelHash = new HashMap();

		currentLevelQueue.addFirst(finalNode);
		currentLevelHash.put(finalNode, 1.0);

		while(currentLevelQueue.size() > 0) {
			Deque<Node> nextLevelQueue = new ArrayDeque();
			HashMap<Node, Double> nextLevelHash = new HashMap();

			for(Node current : currentLevelQueue) {
				Double currentVists = currentLevelHash.get(current);

				// Update the current node
				current.update(gameState.getResult(current.getGameState().getPlayerJustMoved(), false)*currentVists, currentVists);

				double numOfParents = (double)current.getParentEdges().size();
				Double parentScore = currentVists/numOfParents;

				// Go through its parents and dehoover their whatsits for next round
				for(Edge e : current.getParentEdges()) {
					Node parent = e.getTail();

					if(nextLevelHash.containsKey(parent)) {
						nextLevelHash.put(parent, nextLevelHash.get(parent)+parentScore);
					} else {
						nextLevelQueue.addFirst(parent);
						nextLevelHash.put(parent, parentScore);
					}
				}
			}

			currentLevelHash = nextLevelHash;
			currentLevelQueue = nextLevelQueue;
		}
	}

	@Override
	public int getCollisions() {
		return this.collisions;
	}
}
