package edu.brown.cs.checkers.game;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.jetty.websocket.api.Session;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import edu.brown.cs.checkers.client.MessageType;

/**
 * handles the messages sent by the match class
 * 
 * @author Justin Huang
 *
 */
public class MatchHandler {
	private static final Gson GSON = new Gson();
	private static playerRecords record;

	public MatchHandler() {
		record = new playerRecords();
	}

	/**
	 * adds the current session to the next available game slot.
	 * 
	 * @param session
	 * @param received
	 * @param matches
	 * @param sessionToPlayer
	 * @param playerToGame
	 * @param playerToSession
	 */
	public synchronized void joinGame(Session session, JsonObject received, List<Game> matches,
			Map<Session, Player> sessionToPlayer, Map<Player, Game> playerToGame,
			Map<Player, Session> playerToSession) {

		Game currGame = null;
		// if the match list is empty: create a new game
		int size = matches.size();
		if (matches.size() == 0) {
			currGame = new Game(UUID.randomUUID());
			currGame.addPlayer(sessionToPlayer.get(session));
			matches.add(currGame);

		} else {
			Game currMatch = matches.get(size - 1);
			// if the last Game is full: create a new game
			if (currMatch.maxPlayers()) {
				currGame = new Game(UUID.randomUUID());
				currGame.addPlayer(sessionToPlayer.get(session));
				matches.add(currGame);
			} else {
				// else adds the player to this game
				currGame = currMatch;
				currMatch.addPlayer(sessionToPlayer.get(session));
			}
		}
		playerToGame.put(sessionToPlayer.get(session), currGame);
		if (currGame.gameStarted()) {
			List<Player> currGamePlayerList = currGame.getPlayerList();
			// sends a message to each player that the game has started
			for (int i = 0; i < currGamePlayerList.size(); i++) {
				Player currPlayer = currGamePlayerList.get(i);
				int[] opponentRecord = record.getRecord(record.getEmail(currPlayer.opponent().userName()));
				// this is for the modal on the front end
				Map<String, Object> variables = new HashMap<>();
				variables.put("opponent", currPlayer.opponent().userName());
				variables.put("opponentRecord", opponentRecord);
				variables.put("type", MessageType.STARTGAME.ordinal());
				variables.put("isBlack", currPlayer.isBlack());
				variables.put("isTurn", currPlayer.isTurn());
				Session currSession = playerToSession.get(currPlayer);
				try {
					currSession.getRemote().sendString(GSON.toJson(variables));
				} catch (IOException e) {
					System.out.println("unable to send message");
				}
			}
		}
	}

	/**
	 * makes sure that the info sent constitutes a valid move
	 * 
	 * @param session
	 * @param received
	 * @param playerToSession
	 * @param playerToGame
	 * @param sessionToPlayer
	 */
	public synchronized void validateAndMove(Session session, JsonObject received, Map<Player, Session> playerToSession,
			Map<Player, Game> playerToGame, Map<Session, Player> sessionToPlayer) {
		Player player = sessionToPlayer.get(session);
		Game currGame = playerToGame.get(player);
		Board board = currGame.getBoard();
		boolean isBlack = received.get("isBlack").getAsBoolean();
		int pieceXCord = received.get("pieceXCord").getAsInt();
		int pieceYCord = received.get("pieceYCord").getAsInt();
		int moveToXCord = received.get("moveToXCord").getAsInt();
		int moveToYCord = received.get("moveToYCord").getAsInt();
		Piece currPiece = board.search(pieceXCord, pieceYCord);
		boolean validMove = board.validMove(pieceXCord, pieceYCord, moveToXCord, moveToYCord, currPiece);
		if (validMove && currPiece.isBlack() == isBlack) {
			player.move(currPiece, pieceXCord, pieceYCord, moveToXCord, moveToYCord, board);
			currGame.updateMoves();
			try {
				// if the turn is over, send message to current player that turn
				// is over
				if (!player.isTurn()) {
					JsonObject jo = new JsonObject();
					jo.addProperty("type", MessageType.ENDTURN.ordinal());
					session.getRemote().sendString(jo.toString());
					Player opponent = player.opponent();
					JsonObject jo1 = new JsonObject();
					// send message to other player that it is their turn
					jo1.addProperty("type", MessageType.ISTURN.ordinal());
					Session otherSession = playerToSession.get(opponent);
					otherSession.getRemote().sendString(jo1.toString());
				}
				List<Player> currGamePlayerList = currGame.getPlayerList();
				// send message to both players to update the board visually
				for (int i = 0; i < currGamePlayerList.size(); i++) {
					JsonObject jo1 = new JsonObject();
					jo1.addProperty("type", MessageType.UPDATEBOARD.ordinal());
					jo1.addProperty("pieceXStart", pieceXCord);
					jo1.addProperty("pieceYStart", pieceYCord);
					jo1.addProperty("pieceXEnd", moveToXCord);
					jo1.addProperty("pieceYEnd", moveToYCord);
					jo1.addProperty("isKing", currPiece.isKing());
					jo1.addProperty("moveType", received.get("moveType").getAsString());
					jo1.addProperty("isBlack", isBlack);
					Player currPlayer = currGame.getPlayerList().get(i);
					Session currSession = playerToSession.get(currPlayer);
					currSession.getRemote().sendString(jo1.toString());

				}
				// checks if the game is over after the move
				if (currGame.gameWon()) {
					declareWinner(currGame, playerToSession);
				}
			} catch (IOException e) {
				e.printStackTrace();

			}

		} else {
			// if the move is not valid then take the final cords and treat them
			// as if the
			// user is checking for possible moves
			getCords(moveToXCord, moveToYCord, received, board, session);
		}

	}

	/**
	 * assigns the winner and loser to each player
	 * 
	 * @param game
	 * @param playerToSession
	 */
	public synchronized void declareWinner(Game game, Map<Player, Session> playerToSession) {
		List<Player> currPlayerList = game.getPlayerList();
		String winner = null;
		String loser = null;
		for (int i = 0; i < currPlayerList.size(); i++) {
			Player currPlayer = currPlayerList.get(i);
			// if the currPlayer is the winner
			if (currPlayer.isWinner()) {
				winner = currPlayer.userName();
				sendWinnerMessage(currPlayer, playerToSession);
			} else {
				loser = currPlayer.userName();
				sendLoserMessage(currPlayer, playerToSession);
			}
		}
		// update the database
		record.updateRecord(winner, loser);
	}

	/**
	 * sends the player that won a winner message to the front end
	 * 
	 * @param player
	 * @param playerToSession
	 */
	private synchronized void sendWinnerMessage(Player player, Map<Player, Session> playerToSession) {
		JsonObject jo = new JsonObject();
		jo.addProperty("type", MessageType.WINNER.ordinal());
		try {
			playerToSession.get(player).getRemote().sendString(jo.toString());
		} catch (IOException e) {
			System.out.println("failed to send message");
		}
	}

	/**
	 * sends the player that lost a loser message to the front end
	 * 
	 * @param player
	 * @param playerToSession
	 */
	private synchronized void sendLoserMessage(Player player, Map<Player, Session> playerToSession) {
		JsonObject jo = new JsonObject();
		jo.addProperty("type", MessageType.LOSER.ordinal());
		try {
			playerToSession.get(player).getRemote().sendString(jo.toString());
		} catch (IOException e) {
			System.out.println("failed to send message");
		}
	}

	/**
	 * gets the coordinates of all of the moves of a piece
	 * 
	 * @param xCord
	 * @param yCord
	 * @param received
	 * @param board
	 * @param session
	 */
	public synchronized void getCords(int xCord, int yCord, JsonObject received, Board board, Session session) {
		Piece piece = board.getBoard()[xCord][yCord];
		JsonObject jo = new JsonObject();
		Map<String, Map<Integer, List<Integer>>> map = null;
		// if the coordinate contains a piece
		if (piece != null) {
			// if the player is black
			if (received.get("isBlack").getAsBoolean()) {
				// and if the piece is black
				if (piece.isBlack()) {
					map = board.getMovablePiecesBlack().get(piece);

				}
				// if the player is not black
			} else {
				// and the piece is not black
				if (!piece.isBlack()) {
					map = board.getMovablePiecesRed().get(piece);
				}
			}
			if (map != null) {
				// if the piece's move is a jump move
				if (map.containsKey("jump")) {
					if (map.get("jump").size() != 0) {
						Map<Integer, List<Integer>> innerMap = map.get("jump");
						Map<String, Object> variables = ImmutableMap.of("type", MessageType.GETPOSSIBLEMOVES.ordinal(),
								"moveType", "jump", "pieceXCord", xCord, "pieceYCord", yCord, "cords", innerMap);
						try {
							session.getRemote().sendString(GSON.toJson(variables));
						} catch (IOException e) {
							System.out.println("couldn't send message");
						}
					}
					// if the move is a normal move
				} else if (map.containsKey(("normal"))) {
					if (map.get("normal").size() != 0) {
						jo.addProperty("type", MessageType.GETPOSSIBLEMOVES.ordinal());
						jo.addProperty("moveType", "normal");
						jo.addProperty("cords", map.get("normal").toString());
						jo.addProperty("pieceXCord", xCord);
						jo.addProperty("pieceYCord", yCord);
						Map<Integer, List<Integer>> innerMap = map.get("normal");
						Map<String, Object> variables = ImmutableMap.of("type", MessageType.GETPOSSIBLEMOVES.ordinal(),
								"moveType", "normal", "pieceXCord", xCord, "pieceYCord", yCord, "cords", innerMap);

						try {
							session.getRemote().sendString(GSON.toJson(variables));
						} catch (IOException e) {
							System.out.println("couldn't send message");
						}

					}
				}
			}

		}
	}

	/**
	 * handles chat messages
	 * 
	 * @param session
	 * @param received
	 * @param playerToSession
	 * @param sessionToPlayer
	 */
	public synchronized void message(Session session, JsonObject received, Map<Player, Session> playerToSession,
			Map<Session, Player> sessionToPlayer) {
		JsonObject jo = new JsonObject();
		jo.addProperty("type", MessageType.MESSAGE.ordinal());
		jo.addProperty("isBlack", received.get("isBlack").getAsBoolean());
		jo.addProperty("message", received.get("message").getAsString());
		jo.addProperty("userName", received.get("userName").getAsString());
		try {
			session.getRemote().sendString(jo.toString());
			Player currPlayer = sessionToPlayer.get(session);
			// makes sure that the opponent player is not disconnected
			if (!currPlayer.opponent().hasForfeit()) {
				if (playerToSession.containsKey(currPlayer.opponent())) {
					playerToSession.get(currPlayer.opponent()).getRemote().sendString(jo.toString());
				}
			}
		} catch (IOException e) {
			System.out.println("Couldn't send message");
		}
	}

	/**
	 * checks if a closed session results in a forfeit
	 * 
	 * @param session
	 * @param sessionToPlayer
	 * @param playerToSession
	 */
	public synchronized void checkForForfeits(Session session, Map<Session, Player> sessionToPlayer,
			Map<Player, Session> playerToSession) {
		if (sessionToPlayer.containsKey(session)) {
			Player currPlayer = sessionToPlayer.get(session);
			if (!currPlayer.hasGameEnded()) {
				if (currPlayer.opponent() != null) {
					// if the opponent of the disconnected player has not
					// forfeited/disconnected
					if (!currPlayer.opponent().hasForfeit()) {
						// the current player is forfeiting
						currPlayer.forfeit();
						record.updateForfeit(currPlayer.opponent().userName(), currPlayer.userName());
						JsonObject jo = new JsonObject();
						// send a message to the opponent player that they are
						// forfeiting
						jo.addProperty("type", MessageType.WINBYFORFEIT.ordinal());
						Session otherSession = playerToSession.get(currPlayer.opponent());
						try {
							otherSession.getRemote().sendString(jo.toString());
						} catch (IOException e) {
							System.out.println("message cannot be sent");
						}

					}

				}
			}
		}
	}

}
