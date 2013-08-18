package com.jnew528.finalYearProject;

/**
 * Created with IntelliJ IDEA.
 * User: Jared
 * Date: 7/07/13
 * Time: 4:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class HexMove implements Move {
	protected int boardIndex;
	protected int boardSize;

	HexMove(Integer boardIndex) throws Exception{
		this(boardIndex, 11);
	}

	HexMove(Integer boardIndex, Integer boardSize) throws Exception {
		if(boardIndex >= boardSize*boardSize || boardIndex < -1) { // Pie move represented by board index of -1
			throw new Exception("Move specified is invalid");
		}

		this.boardIndex = boardIndex;
		this.boardSize = boardSize;
	}

	public String toString() {
		return "" + boardIndex;
	}

	@Override
	public boolean equals(Object other){
		if (other == null) return false;
		if (other == this) return true;
		if (!(other instanceof HexMove))return false;
		HexMove otherHexMove = (HexMove)other;
		return otherHexMove.boardIndex == this.boardIndex && otherHexMove.boardSize == this.boardSize;
	}
}
