package TicTacToeProject;

/** 
* Tic-Tac-Toe game Project
* 
* @author Said Lfagrouche
* 
* This Tic Tac Toe program is a well-designed and efficient implementation of the classic game. 
* The program utilizes various Java libraries and functions to run the game, handle user input, 
* place game pieces on the board, check for a winner, and handle errors. 
* The program uses an array to represent the game board and two Lists to store the positions of the player and CPU's pieces. 
* Additionally, it employs the Scanner class to read input from the user, and the Random class to generate the CPU's positions.
* The program includes exception handling to ensure the player enters valid input and to catch any errors that may occur during gameplay.
* It also uses a Minimax algorithm to select the best move for the CPU, making the game more challenging for the player.
* The program's user interface is clear and easy to follow, with the game board printed to the console in a visually appealing way.
* Overall, this Tic Tac Toe program is a professional and enjoyable implementation of the classic game.
*/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class TicTacToe {

	// constants for game objects that don't change
	private static final List<Integer> PLAYER_POSITIONS = new ArrayList<>();
	private static final List<Integer> CPU_POSITIONS = new ArrayList<>();
	private static char[][] GAME_BOARD = {
			{ ' ', '|', ' ', '|', ' ' },
			{ '-', '+', '-', '+', '-' },
			{ ' ', '|', ' ', '|', ' ' },
			{ '-', '+', '-', '+', '-' },
			{ ' ', '|', ' ', '|', ' ' }
	};
	private static final Scanner SCANNER = new Scanner(System.in);

	public static void main(String[] args) {

		while (true) {

			// print the initial state of the game board
			printGameBoard();

			while (true) {
				// get player's position and place their piece on the game board
				int playerPos = getPlayerPosition();
				placePiece(playerPos, "player");

				// check if the player has won, and if so, print the result and end the game
				String result = checkWinner();
				if (result.length() > 0) {
					printGameBoard();
					System.out.println(result + "\n\n");
					SCANNER.nextLine();
					break;
				}

				/**
				 * CPU's move selection using Minimax algorithm.
				 * It calls the minimax() method and pass current positions of player and CPU's
				 * pieces,
				 * a depth of 0 and isMaximizing as true.
				 * As a result, it will get the best move for the CPU from the minimax() method.
				 * Then it calls the placePiece() method to place the CPU's piece on the board.
				 */
				int cpuPos = minimax(PLAYER_POSITIONS, CPU_POSITIONS, 0, true);
				placePiece(cpuPos, "cpu");

				// print the updated game board
				printGameBoard();

				// check if the CPU has won, and if so, print the result and end the game
				result = checkWinner();
				if (result.length() > 0) {
					System.out.println(result + "\n\n");
					SCANNER.nextLine();
					break;
				}
			}

			// Ask the user if they want to play again
			System.out.println("Do you want to play again? (yes/no)");
			String playAgain = SCANNER.nextLine();
			while (!playAgain.equalsIgnoreCase("yes") && !playAgain.equalsIgnoreCase("no")) {
				System.out.println("Please enter 'yes' or 'no'.");
				playAgain = SCANNER.nextLine();
			}
			if (playAgain.equalsIgnoreCase("no")) {
				System.out.println("\nThank you for playing!");
				break;
			} else if (playAgain.equalsIgnoreCase("yes")) {
				// Clear the board and the positions
				clearBoard();
				System.out.println();

			}

		}
	}

	/**
	 * Prints the current state of the game board.
	 */
	private static void printGameBoard() {
		for (char[] row : GAME_BOARD) {
			for (char c : row) {
				System.out.print(c);
			}
			System.out.println();
		}
	}

	/**
	 * Prompts the player to enter their position and returns it.
	 * 
	 * @return the player's position
	 */
	private static int getPlayerPosition() {
		while (true) {

			try {
				System.out.println("\nEnter the position you want to place your piece (1-9):");
				int playerPos = SCANNER.nextInt();
				// throws an exception if the number is not between 1 and 9
				if (playerPos < 1 || playerPos > 9)
					throw new InvalidInputException(playerPos);

				// check if the position is already taken
				if (!PLAYER_POSITIONS.contains(playerPos) && !CPU_POSITIONS.contains(playerPos)) {
					return playerPos;
				}
				// if the position is taken, prompt the player to enter a different position
				System.out.println("Please choose an empty position on the game board.\n");

			} catch (InvalidInputException ex) {
				// If the player enters a non-integer value, clear the scanner and prompt the
				// player to enter a valid integer
				SCANNER.nextLine();
				System.out.println(ex.getMessage());
			} catch (Exception ex) {
				SCANNER.nextLine();
				System.out.println("Invalid Input, Please enter a valid integer.\n");
			}

		}
	}

	/**
	 * Implements the minimax algorithm to determine the best move for the CPU.
	 * The method recursively evaluates all possible future game states by
	 * simulating
	 * all possible moves for both the player and the CPU.
	 *
	 * @param playerPositions the current positions of the player's pieces
	 * @param cpuPositions    the current positions of the CPU's pieces
	 * @param depth           the current depth of the search
	 * @param isMaximizing    a boolean indicating whether the CPU or the player is
	 *                        currently making a move
	 * @return the best move for the CPU based on the minimax algorithm
	 */
	private static int minimax(List<Integer> playerPositions, List<Integer> cpuPositions, int depth,
			boolean isMaximizing) {
		// check if the game is over
		String result = checkWinner();
		if (result.equals("Sorry, the CPU won. Better luck next time.")) {
			return 10 - depth;
		} else if (result.equals("Congratulations, you won!")) {
			return depth - 10;
		} else if (result.equals("The game ended in a tie!")) {
			return 0;
		}

		// generate a list of all possible moves
		List<Integer> possibleMoves = new ArrayList<>();
		for (int i = 1; i <= 9; i++) {
			if (!playerPositions.contains(i) && !cpuPositions.contains(i)) {
				possibleMoves.add(i);
			}
		}

		// initialize the best move variable
		int bestMove = 0;
		int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

		// loop through all possible moves
		for (int move : possibleMoves) {
			// make the move
			if (isMaximizing) {
				cpuPositions.add(move);
			} else {
				playerPositions.add(move);
			}

			// get the score of the move
			int score = minimax(playerPositions, cpuPositions, depth + 1, !isMaximizing);

			// undo the move
			if (isMaximizing) {
				cpuPositions.remove(cpuPositions.size() - 1);
			} else {
				playerPositions.remove(playerPositions.size() - 1);
			}

			// update the best score and move
			if (isMaximizing) {
				if (score > bestScore) {
					bestScore = score;
					bestMove = move;
				}
			} else {
				if (score < bestScore) {
					bestScore = score;
					bestMove = move;
				}
			}
		}

		// return the best move if it is the CPU's turn, or the best score if it is the
		// player's turn
		if (isMaximizing) {
			return bestMove;
		} else {
			return bestScore;
		}
	}

	/**
	 * Places the specified user's piece on the game board at the specified
	 * position.
	 * 
	 * @param pos  the position to place the piece at
	 * @param user the user whose piece to place (either "player" or "cpu")
	 */
	private static void placePiece(int pos, String user) {

		char symbol = ' ';

		if (user.equals("player")) {
			symbol = 'X';
			PLAYER_POSITIONS.add(pos);
		} else if (user.equals("cpu")) {
			symbol = 'O';
			CPU_POSITIONS.add(pos);
		}

		// place the piece on the game board
		int row = (pos - 1) / 3;
		int col = (pos - 1) % 3;
		GAME_BOARD[row * 2][col * 2] = symbol;

		// Alternative Version of placing the piece on the game board
		//
		// switch (pos) {
		// case 1:
		// GAME_BOARD[0][0] = symbol;
		// break;
		// case 2:
		// GAME_BOARD[0][2] = symbol;
		// break;
		// case 3:
		// GAME_BOARD[0][4] = symbol;
		// break;
		// case 4:
		// GAME_BOARD[2][0] = symbol;
		// break;
		// case 5:
		// GAME_BOARD[2][2] = symbol;
		// break;
		// case 6:
		// GAME_BOARD[2][4] = symbol;
		// break;
		// case 7:
		// GAME_BOARD[4][0] = symbol;
		// break;
		// case 8:
		// GAME_BOARD[4][2] = symbol;
		// break;
		// case 9:
		// GAME_BOARD[4][4] = symbol;
		// break;
		// default:
		// throw new IllegalArgumentException("Invalid position: " + pos);
		// }

	}

	/**
	 * Check if the game has been won by either the player or the CPU, or if the
	 * game is a draw.
	 * 
	 * @return A message indicating the outcome of the game either "You won!" or
	 *         "CPU won!" or an empty string if the game is still ongoing.
	 */
	private static String checkWinner() {
		// List objects representing each winning combination
		List<Integer> firstRow = Arrays.asList(1, 2, 3);
		List<Integer> secondRow = Arrays.asList(4, 5, 6);
		List<Integer> thirdRow = Arrays.asList(7, 8, 9);
		List<Integer> firstColumn = Arrays.asList(1, 4, 7);
		List<Integer> secondColumn = Arrays.asList(2, 5, 8);
		List<Integer> thirdColumn = Arrays.asList(3, 6, 9);
		List<Integer> diagonal1 = Arrays.asList(1, 5, 9);
		List<Integer> diagona2 = Arrays.asList(3, 5, 7);

		// List of all winning combinations
		List<List<Integer>> winningConditions = Arrays.asList(firstRow, secondRow, thirdRow, firstColumn, secondColumn,
				thirdColumn, diagonal1, diagona2);

		// Check if any winning combination is satisfied
		for (List<Integer> winningCombo : winningConditions) {
			if (PLAYER_POSITIONS.containsAll(winningCombo)) {
				return "Congratulations, you won!";
			} else if (CPU_POSITIONS.containsAll(winningCombo)) {
				return "Sorry, the CPU won. Better luck next time.";
			}
		}

		// Check if the game is a draw
		if (PLAYER_POSITIONS.size() + CPU_POSITIONS.size() == 9) {
			return "The game ended in a tie!";
		}

		// Otherwise, the game is still ongoing
		return "";
	}

	/**
	 * Clears the game board and the positions of the player and CPU pieces.
	 */
	private static void clearBoard() {
		PLAYER_POSITIONS.clear();
		CPU_POSITIONS.clear();
		GAME_BOARD = new char[][] {
				{ ' ', '|', ' ', '|', ' ' },
				{ '-', '+', '-', '+', '-' },
				{ ' ', '|', ' ', '|', ' ' },
				{ '-', '+', '-', '+', '-' },
				{ ' ', '|', ' ', '|', ' ' }
		};
	}

}

/**
 * This class represents an exception that is thrown when a number is out of
 * range(1-9). The exception includes a message that specifies the invalid
 * number.
 * 
 * @author Said Lfagrouche
 */
class InvalidInputException extends Exception {

	InvalidInputException(int num) {
		super("Invalid number, Out of Range: " + num);
	}

	public String getMessage() {
		return super.getMessage();
	}
}
