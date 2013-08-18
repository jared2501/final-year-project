package com.jnew528.finalYearProject;

/**
 * Created with IntelliJ IDEA.
 * User: Jared
 * Date: 15/07/13
 * Time: 1:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class LeftRightMove implements Move {
	boolean right;

	LeftRightMove(String type) {
		if(type == "L") {
			this.right = true;
		} else {
			this.right = false;
		}
	}

	public boolean getRight() {
		return this.right;
	}
}
