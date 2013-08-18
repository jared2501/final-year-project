package com.jnew528.finalYearProject;

import com.jnew528.finalYearProject.DirectedAcyclicGraph.Node;

import java.util.HashMap;
import java.util.Vector;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Jared
 * Date: 24/07/13
 * Time: 7:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class GobangState implements GameState<GobangState, GobangMove> {
	int size;
	int numInRow;
	Integer playerJustMoved;
	// int[rows][cols] of the board
	int[][] board;
	Vector<GobangMove> childMoves;
	// Stores the x and y position of the last place you moved on the board
	int winner;
	boolean isFinalState;


	GobangState(int size, int numInRow) {
		this.size = size;
		this.numInRow = numInRow;
		this.playerJustMoved = 2;
		this.board = new int[size][size];
		this.childMoves = new Vector<GobangMove>();

		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				this.board[i][j] = 0;
				this.childMoves.add(new GobangMove(i,j));
			}
		}

		this.winner = 0;
		this.isFinalState = false;
	}

	GobangState() {
		this(11, 5);
	}

	GobangState(int boardSize) {
		this(boardSize, 4);
	}

	private int[][] deepCopy(int[][] input) {
		int[][] target = new int[input.length][];
		for (int i=0; i <input.length; i++) {
			target[i] = Arrays.copyOf(input[i], input[i].length);
		}
		return target;
	}

	GobangState(GobangState oldState) {
		this.size = oldState.size;
		this.numInRow = oldState.numInRow;
		this.playerJustMoved = oldState.playerJustMoved;
		this.board = deepCopy(oldState.board);
		this.childMoves = (Vector)oldState.childMoves.clone();
		this.winner = oldState.winner;
		this.isFinalState = oldState.isFinalState;
	}

	@Override
	public Integer getPlayerJustMoved() {
		return playerJustMoved;
	}

	@Override
	public Integer getPlayerToMove() {
		return 3 - playerJustMoved;
	}

	@Override
	public boolean isFinalState(boolean quickCheck) {
		return isFinalState;
	}

	@Override
	public Double getResult(Integer playerNumber, boolean quickCheck) {
		if(!isFinalState) {
			return null;
		} else {
			if(winner == playerNumber) {
				return 1.0;
			} else if(winner == 0) {
				return 0.5;
			} else {
				return 0.0;
			}
		}
	}

	@Override
	public Integer getWinner(boolean quickCheck) {
		if(!isFinalState) {
			return null;
		}

		return winner;
	}

	@Override
	public GameState createChildStateFromMove(GobangMove move) {
		if(this.board[move.r][move.c] != 0) {
			System.out.println("Players already moved at this position");
			return null;
		}

		GobangState newState = new GobangState(this);
		newState.playerJustMoved = 3 - newState.playerJustMoved;
		newState.board[move.r][move.c] = newState.playerJustMoved; // Make move
		newState.childMoves.remove(move); // remove the move from possible moves
		newState.determineWinner(move.r, move.c); // See if theres a winner from the last place moved
		newState.isFinalState = newState.winner != 0 || newState.childMoves.size() == 0; // If there is a winner, or theres no more moves, its a finalstate

		return newState;
	}

	@Override
	public Vector<GobangMove> getChildMoves() {
		if(isFinalState) {
			return new Vector<GobangMove>();
		}

		return (Vector)childMoves.clone();
	}

	private int[][] rotateBoardCW90deg(int[][] input) {
		int[][] output = new int[input.length][input.length];

		for(int r = 0; r < input.length; r++) {
			for(int c = 0; c < input.length; c++) {
				output[c][input.length - r - 1] = input[r][c];
			}
		}

		return output;
	}

	private int[][] reflectBoardHorizontal(int[][] input) {
		int[][] output = new int[input.length][input.length];

		for(int i = 0; i < input.length; i++) {
			output[i] = input[input.length - 1 - i].clone();
		}

		return output;
	}

	private int[][] reflectBoardVertical(int[][] input) {
		int[][] output = new int[input.length][input.length];

		for(int i = 0; i < input.length; i++) {
			for(int j = 0; j < input.length; j++) {
				output[i][j] = input[i][input.length - 1 - j];
			}
		}

		return output;
	}

	@Override
	public Node getTransposition(HashMap<GobangState, Node> encounteredGamestates) {
		if(encounteredGamestates.containsKey(this)) {
			return encounteredGamestates.get(this);
		}

		int[][] newBoard = this.board;

		// Rotate 3 times
		for(int i = 0; i < 3; i++) {
			newBoard = rotateBoardCW90deg(newBoard);
			GobangState temp = new GobangState(this);
			temp.board = newBoard;
			GameState test = ((GameState) temp);
			if(encounteredGamestates.get(test) != null) {
				return encounteredGamestates.get(test);
			}
		}

		// Reflect horizontal
		newBoard = reflectBoardHorizontal(this.board);
		GobangState temp = new GobangState(this);
		temp.board = newBoard;
		GameState test = ((GameState) temp);
		if(encounteredGamestates.get(test) != null) {
			return encounteredGamestates.get(test);
		}

		// Reflect vertical
		newBoard = reflectBoardVertical(this.board);
		temp = new GobangState(this);
		temp.board = newBoard;
		test = ((GameState) temp);
		if(encounteredGamestates.get(test) != null) {
			return encounteredGamestates.get(test);
		}

		return null;
	}

	@Override
	public GobangMove convertMove(GobangState transposition, GobangMove move) {
		int[][] markedBoard = deepCopy(transposition.board);
		if(markedBoard[move.r][move.c] != 0) {
			return null;
		}
		markedBoard[move.r][move.c] = 3; // mark the board

//		System.out.println();
//		System.out.println("-----------------------");
//		System.out.println("-----------------------");
//		System.out.println("We want transposition board to look like This board:");
//		System.out.println(printBoard(this.board, this.size));
//		System.out.println("transposition board:");
//		System.out.println(printBoard(markedBoard, this.size));
//		System.out.println("=======================");

		// If the transposition board == this board, then the move doesnt need to be converted
		if(Arrays.deepEquals(transposition.board, this.board)) {
			return move;
		}

		// Flip the board three times, and record how much its flipped by. If we find one that matches up flip the move
		// the opposite direction. Ie CCW instead of CW!
		int[][] newBoard = deepCopy(transposition.board);

		for(int i = 0; i < 3; i++) {
			// Rotate the move 90 degrees CCW
			newBoard = rotateBoardCW90deg(newBoard);
			markedBoard = rotateBoardCW90deg(markedBoard);

//			System.out.println("Flipped transposition board:");
//			System.out.println(printBoard(markedBoard, transposition.size));
//			System.out.println("This board:");
//			System.out.println(printBoard(this.board, this.size));

			if(Arrays.deepEquals(newBoard, this.board)) {
				return findMarker(markedBoard);
			}
		}

//		System.out.println("=======================");

		// Reflect the board and try now
		newBoard = reflectBoardHorizontal(transposition.board);
		if(Arrays.deepEquals(newBoard, this.board)) {
//			System.out.println("Reflected H trans board:");
//			System.out.println(printBoard(newBoard, transposition.size));
//			System.out.println("This board:");
//			System.out.println(printBoard(this.board, this.size));

			return new GobangMove(this.board.length - move.r - 1, move.c);
		}

//		System.out.println("=======================");

		newBoard = reflectBoardVertical(transposition.board);
		if(Arrays.deepEquals(newBoard, this.board)) {
//			System.out.println("Reflected V trans board:");
//			System.out.println(printBoard(newBoard, transposition.size));
//			System.out.println("This board:");
//			System.out.println(printBoard(this.board, this.size));

			return new GobangMove(move.r, this.board.length - move.c - 1);
		}

		// We should never ever get to here!!! retun null if we do so we can error quickly
		return null;
	}

	private GobangMove findMarker(int[][] markedBoard) {
		for(int r = 0; r < markedBoard.length; r++) {
			for(int c = 0; c < markedBoard.length; c++) {
				if(markedBoard[r][c] == 3) {
					return new GobangMove(r,c);
				}
			}
		}
		return null;
	}

	private void determineWinner(int lastR, int lastC) {
		// Edge case: first move
		if(lastR < 0 || lastC < 0) {
			this.winner = 0;
		}

		// Now we search from the position the player that just moved has played and try and fine 5 in a row!
		int[] count = {0,0,0,0, 0,0,0,0}; // U,R,D,L, UL,UR,DR,DL
		boolean[] status = {true,true,true,true, true,true,true,true};
		for(int i = 1; status[0] || status[1] || status[2] || status[3]  ||  status[4] || status[5] || status[6] || status[7]; i++) {
			// Look up
			if(status[0] && lastR-i >= 0 && board[lastR-i][lastC] == playerJustMoved) {
				count[0]++;
			} else {
				status[0] = false;
			}

			// Look right
			if(status[1] && lastC+i < size && board[lastR][lastC+i] == playerJustMoved) {
				count[1]++;
			} else {
				status[1] = false;
			}

			// Look down
			if(status[2] && lastR+i < size && board[lastR+i][lastC] == playerJustMoved) {
				count[2]++;
			} else {
				status[2] = false;
			}

			// Look left
			if(status[3] && lastC-i >= 0 && board[lastR][lastC-i] == playerJustMoved) {
				count[3]++;
			} else {
				status[3] = false;
			}


			// Look UL
			if(status[4] && lastR-i >= 0 && lastC-i >= 0 && board[lastR-i][lastC-i] == playerJustMoved) {
				count[4]++;
			} else {
				status[4] = false;
			}

			// Look UR
			if(status[5] && lastR-i > -1 && lastC+i < size && board[lastR-i][lastC+i] == playerJustMoved) {
				count[5]++;
			} else {
				status[5] = false;
			}

			// Look DR
			if(status[6] && lastR+i < size && lastC+i < size && board[lastR+i][lastC+i] == playerJustMoved) {
				count[6]++;
			} else {
				status[6] = false;
			}

			// Look DL
			if(status[7] && lastR+i < size && lastC-i > -1 && board[lastR+i][lastC-i] == playerJustMoved) {
				count[7]++;
			} else {
				status[7] = false;
			}


			// Check if either up/down or left/right or UL+DR or UR/DL sum to 5!
			if(count[0] + count[2] + 1 >= numInRow || count[1] + count[3] + 1 >= numInRow  ||  count[4] + count[6] + 1 >= numInRow || count[5] + count[7] + 1 >= numInRow) {
				this.winner = playerJustMoved;
				return;
			}
		}

		this.winner = 0;
		return;
	}


	@Override
	public String toString() {
		return printBoard(this.board, size);
	}

	private String printBoard(int[][] board, int size) {
		StringBuilder sb = new StringBuilder(); // default size is 16 chars which shouild be enough

		for(int i = 0; i < size; i++) { // rows
			for(int j = 0; j < size; j++) { // cols
				if(board[i][j] == 1) {
					sb.append("X ");
				} else if(board[i][j] == 2) {
					sb.append("O ");
				} else if(board[i][j] == 3) {
					sb.append("M ");
				} else {
					sb.append("_ ");
				}
			}
			sb.append(System.getProperty("line.separator"));
		}

		sb.append(System.getProperty("line.separator"));
		return sb.toString();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + playerJustMoved.hashCode();
		result = prime * result + Arrays.deepHashCode(board);

		return result;
	}

	@Override
	public boolean equals(Object other){
		if(other == null) return false;
		if(other == this) return true;
		if(this.getClass() != other.getClass()) return false;
		GobangState otherGBState = (GobangState)other;
		return otherGBState.playerJustMoved == this.playerJustMoved
				&& Arrays.deepEquals(otherGBState.board, this.board);
	}
}
