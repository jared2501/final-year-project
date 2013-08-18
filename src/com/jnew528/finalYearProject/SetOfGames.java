package com.jnew528.finalYearProject;

import java.io.PrintWriter;
import java.util.Vector;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Jared
 * Date: 11/08/13
 * Time: 8:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class SetOfGames implements Callable<Vector<Double>> {
	int games;
	int iterations;
	int boardSize;
	String gameType;
	PrintWriter writer;

	SetOfGames(int games, int iterations, String gameType, int boardSize, PrintWriter writer) {
		this.games = games;
		this.iterations = iterations;
		this.gameType = gameType;
		this.boardSize = boardSize;
		this.writer = writer;
	}

	@Override
	public Vector<Double> call() throws Exception {
		long startTime = System.nanoTime();
		Vector<Double> output = new Vector<Double>();

		MctsTree playerControl = new MctsTreeStd();
		MctsTree playerTesting = new MctsTreeUpdateAll  ();

		// Write the results to a file as we get them
		double extendedPlayerWins = 0;

		writer.println("1) Game type:");
		writer.println(gameType);
		writer.println("2) Board size:");
		writer.println(boardSize);
		writer.println("3) Number of MCTS iterations per-player:");
		writer.println(iterations);
		writer.println();
		writer.flush();

		for(int i = 0; i < games; i++) {
			int testingPlayerNum = i < (games / 2) ? 1 : 2;
			GameState startingGameState;

			if(gameType == "LeftRight") {
				startingGameState = new LeftRightState(boardSize);
			} else if(gameType == "Hex") {
				startingGameState = new HexState(boardSize);
			} else if(gameType == "Gobang") {
				startingGameState = new GobangState(boardSize);
			} else {
				throw new Exception("Unknown game type");
			}

			Callable game;

			if(testingPlayerNum == 1) {
				game = new Game(startingGameState, playerTesting, playerControl, iterations, false);
			} else {
				game = new Game(startingGameState, playerControl, playerTesting, iterations, false);
			}

			Game finishedGame = (Game)game.call();
			GameState finishedGameState = finishedGame.getGameState();

			if(testingPlayerNum == finishedGameState.getWinner(false)) {
				output.add(1.0);
				extendedPlayerWins += 1;
			} else if(0 == finishedGameState.getWinner(false)) {
				output.add(0.5);
				extendedPlayerWins += 0.5;
			} else {
				output.add(0.0);
			}

			System.out.println(iterations + " Game " + (i + 1) + " finished out of " + games);
			System.out.println("Extended player wins: " + extendedPlayerWins + " out of " + (i + 1));

			writer.println("Game " + (i + 1));
			writer.println("1) Extended player number:");
			writer.println(testingPlayerNum);
			writer.println("2) Extended player collisions:");
			if(1 == testingPlayerNum) {
				writer.println(finishedGame.getPlayer1Collisions());
			} else {
				writer.println(finishedGame.getPlayer2Collisions());
			}
			writer.println("3) Final game state:");
			writer.println(finishedGameState);
			writer.println("4) Winning player:");
			writer.println(finishedGameState.getWinner(false));
			writer.println("5) Duration in seconds:");
			writer.println((double) finishedGame.getDuration() / 1e9);
			writer.println("6) Player 1 type:");
			writer.println(finishedGame.getPlayer1Type());
			writer.println("7) Player 2 type:");
			writer.println(finishedGame.getPlayer2Type());
			writer.println("6) Player 1 time per move (ms):");
			writer.println(finishedGame.getTimePerPlayer1Move());
			writer.println("7) Player 2 time per move (ms):");
			writer.println(finishedGame.getTimePerPlayer2Move());
			writer.println();
			writer.flush();
		}

		long endTime = System.nanoTime();
		long duration = endTime - startTime;

		writer.println("4) Extended player wins:");
		writer.println(extendedPlayerWins + " out of " + games);
		writer.println("5) Total duration:");
		writer.println((double) duration/1000000000);

		writer.close();

		return output;
	}
}
