package com.jnew528.finalYearProject;

/**
 * Created with IntelliJ IDEA.
 * User: Jared
 * Date: 24/07/13
 * Time: 7:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class GobangMove implements Move {
	public int r;
	public int c;

	GobangMove(int r, int c) {
		this.r = r;
		this.c = c;
	}

	@Override
	public boolean equals(Object other){
		if (other == null) return false;
		if (other == this) return true;
		if (!(other instanceof GobangMove))return false;
		GobangMove otherGobangMove = (GobangMove)other;
		return otherGobangMove.r == this.r && otherGobangMove.c == this.c;
	}

	@Override
	public String toString() {
		return "[row:"+this.r+", col:"+this.c+"]";
	}
}
