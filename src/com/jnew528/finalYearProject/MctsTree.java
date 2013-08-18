package com.jnew528.finalYearProject;

/**
 * Created with IntelliJ IDEA.
 * User: Jared
 * Date: 11/08/13
 * Time: 4:04 PM
 * To change this template use File | Settings | File Templates.
 */
public interface MctsTree {
	public Move search(GameState gameState, int iterationCount);
	public int getCollisions();
}
