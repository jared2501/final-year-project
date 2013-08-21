package com.jnew528.finalYearProject;

import java.util.*;

import com.jnew528.finalYearProject.DirectedAcyclicGraph.Edge;
import com.jnew528.finalYearProject.DirectedAcyclicGraph.Node;
import com.jnew528.finalYearProject.DirectedAcyclicGraph.Policies;

/**
 * Created with IntelliJ IDEA.
 * User: Jared
 * Date: 12/08/13
 * Time: 1:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class MctsTreeUpdatePath implements MctsTree {
	protected static Random random;
	protected int collisions;

	static {
		random = new Random();
	}

	public MctsTreeUpdatePath() {}

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

		// Select child with the selection policy
		// In this case, the child with the highest number of visits
		return Policies.selectRobustRootMove(root);
	}

	public void performIteration(Node root, HashMap<GameState, Node> encounteredGameStates) {
		Node node = root;
		Vector<Edge> traversedEdges = new Vector();

		// Traverse the tree until we reach a node on the edge of the current tree
		// i.e. it has untried moves or is a state with no children (i.e. terminal game state)
		select: do {
			node = utcSelect(node, traversedEdges);

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
					Edge edgeBetweenNodeAndTransposition = node.addChild(transposition, move);
					traversedEdges.add(edgeBetweenNodeAndTransposition);
					node = transposition;
					continue select;
				} else {
					Node newNode = new Node(newGameState);
					Edge newEdge = node.addChild(newNode, move);
					traversedEdges.add(newEdge);
					node = newNode;
					encounteredGameStates.put(newGameState, newNode);
				}
			}

			GameState startingGameState = node.getGameState();
			GameState finalGameState = defaultPolicy(node, startingGameState);

			// Back propogate the result from the perspective of the player that just moved
			backpropogate(traversedEdges, root, finalGameState);
			break;
		} while(true);
	}

	private Node utcSelect(Node node, Vector<Edge> traversedEdges) {
		while(!node.hasUntriedMoves() && node.hasChildren()) {
			Edge edge = Policies.uct1SelectChild(node);
			traversedEdges.add(edge);
			node = edge.getHead();
		}

		return node;
	}

	private GameState defaultPolicy(Node node, GameState gameState) {
		// Play a random game from the current node using the default policy
		// in this case, default policy is to select random moves until a final state is reached
		while(!gameState.isFinalState(true)) {
			Vector<Move> moves = gameState.getChildMoves();
			Move move = moves.get(random.nextInt(moves.size()));
			gameState = gameState.createChildStateFromMove(move);
		}
		return gameState;
	}

	private void backpropogate(Vector<Edge> traversedEdges, Node root, GameState gameState) {
		for(Edge edge : traversedEdges) {
			double result = gameState.getResult(edge.getHead().getGameState().getPlayerJustMoved(), true);
			edge.update(result, 1.0);
			edge.getHead().update(result, 1.0);
		}

		// Since were updating the heads the root node has no head pointing to it!
		double result = gameState.getResult(root.getGameState().getPlayerJustMoved(), true);
		root.update(result, 1.0);
	}

	@Override
	public int getCollisions() {
		return this.collisions;
	}
}
