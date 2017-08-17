package edu.brown.cs.checkers.game;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import edu.brown.cs.checkers.client.MessageType;

//import edu.brown.cs.jhbgbssg.Client.Match;

@WebSocket
public class Matches {
	private static List<String> playerList;
	private static final Gson GSON = new Gson();
	private static String connect;
	private static final Queue<Session> SESSIONS = new ConcurrentLinkedQueue<>();
	private static Connection connection;
	private List<Game> matches = Collections.synchronizedList(new ArrayList<>());
	// private Map<UUID, Match> matchIdToClass =
	// Collections.synchronizedMap(new HashMap<>());
	private Map<Player, Session> playerToSession = Collections.synchronizedMap(new HashMap<>());
	private Map<Session, Player> sessionToPlayer = Collections.synchronizedMap(new HashMap<>());
	private Map<Player, Game> playerToGame = Collections.synchronizedMap(new HashMap<>());

	public Matches() {
		playerList = new ArrayList<>();
		connect = null;
		// connection = conn;
	}

	public void setConnection(Connection conn) {
		connection = conn;
	}

	public synchronized boolean duplicateConnection(String email) {
		if (playerList.contains(email)) {
			return true;
		}
		connect = email;
		return false;
	}

	//
	@OnWebSocketConnect
	public synchronized void connected(Session session) {
		try {
			playerList.add(connect);
			String userName = getUserName(connect);
			SESSIONS.add(session);
			Player player = new Player(userName);
			sessionToPlayer.put(session, player);
			playerToSession.put(player, session);
			JsonObject jo = new JsonObject();
			jo.addProperty("type", MessageType.JOINGAME.ordinal());
			jo.addProperty("userName", userName);
			session.getRemote().sendString(jo.toString());

		} catch (IOException ex) {
			System.out.println("ERROR: IOexception caught while connecting session.");
		}
	}

	@OnWebSocketMessage
	public synchronized void message(Session session, String message) throws IOException {

		// Get received message
		JsonObject received = GSON.fromJson(message, JsonObject.class);
		if (received.get("type") != null) {
			if (received.get("type").getAsInt() == MessageType.JOINGAME.ordinal()) {
				joinGame(session, received);
			}
			if (received.get("type").getAsInt() == MessageType.GETCORDS.ordinal()) {
				Player currPlayer = sessionToPlayer.get(session);
				Game currGame = playerToGame.get(currPlayer);
				Board board = currGame.getBoard();
				int xCord = received.get("pieceXCord").getAsInt();
				int yCord = received.get("pieceYCord").getAsInt();
				getCords(xCord, yCord, received, board, session);
			}
			if (received.get("type").getAsInt() == MessageType.MOVE.ordinal()) {
				validateAndMove(session, received);

			}
			if (received.get("type").getAsInt() == MessageType.MESSAGE.ordinal()) {
				JsonObject jo = new JsonObject();
				jo.addProperty("type", MessageType.MESSAGE.ordinal());
				jo.addProperty("isBlack", received.get("isBlack").getAsBoolean());
				jo.addProperty("message", received.get("message").getAsString());
				jo.addProperty("userName", received.get("userName").getAsString());
				session.getRemote().sendString(jo.toString());
				Player currPlayer = sessionToPlayer.get(session);
				if (!currPlayer.opponent().hasForfeit()) {
					if (playerToSession.containsKey(currPlayer.opponent())) {
						playerToSession.get(currPlayer.opponent()).getRemote().sendString(jo.toString());
					}
				}
				
			}
		}

	}

	private synchronized void validateAndMove(Session session, JsonObject received) {
		Player player = sessionToPlayer.get(session);
		Game currGame = playerToGame.get(player);
		Board board = currGame.getBoard();
		boolean isBlack = received.get("isBlack").getAsBoolean();
		String moveType = received.get("moveType").getAsString();
		int pieceXCord = received.get("pieceXCord").getAsInt();
		int pieceYCord = received.get("pieceYCord").getAsInt();
		int moveToXCord = received.get("moveToXCord").getAsInt();
		int moveToYCord = received.get("moveToYCord").getAsInt();
		Piece currPiece = board.search(pieceXCord, pieceYCord);
		boolean validMove = board.validMove(pieceXCord, pieceYCord, moveToXCord, moveToYCord, currPiece);
		if (validMove && currPiece.isBlack() == isBlack) {
			System.out.println("line 121 " + player.isTurn());
			player.move(currPiece, pieceXCord, pieceYCord, moveToXCord, moveToYCord, board);
			System.out.println("line 123 " + player.isTurn());
			currGame.updateMoves();
			try {
				System.out.println("line 126 " + player.isTurn());
				if (!player.isTurn()) {
					System.out.println("turned ended? ");
					JsonObject jo = new JsonObject();
					jo.addProperty("type", MessageType.ENDTURN.ordinal());
					session.getRemote().sendString(jo.toString());
					Player opponent = player.opponent();
					JsonObject jo1 = new JsonObject();
					jo1.addProperty("type", MessageType.ISTURN.ordinal());
					Session otherSession =playerToSession.get(opponent);
					otherSession.getRemote().sendString(jo1.toString());
				}
				List<Player> currGamePlayerList = currGame.getPlayerList();
				for (int i = 0; i < currGamePlayerList.size(); i++) {
					JsonObject jo1 = new JsonObject();
					jo1.addProperty("type", MessageType.UPDATEBOARD.ordinal());
					jo1.addProperty("moveType", moveType);
					jo1.addProperty("pieceXStart", pieceXCord);
					jo1.addProperty("pieceYStart", pieceYCord);
					jo1.addProperty("pieceXEnd", moveToXCord);
					jo1.addProperty("pieceYEnd", moveToYCord);
					jo1.addProperty("isBlack", isBlack);
					Player currPlayer = currGame.getPlayerList().get(i);
					Session currSession = playerToSession.get(currPlayer);
					currSession.getRemote().sendString(jo1.toString());
					
				}
				if (currGame.gameWon()) {
					declareWinner(currGame);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}

		} else {
			getCords(moveToXCord, moveToYCord, received, board, session);
		}

	}

	private synchronized void declareWinner(Game game) {
		List<Player> currPlayerList = game.getPlayerList();
		boolean blackWon = game.blackWon();
		String winner = null;
		String loser = null;
		for (int i = 0; i<currPlayerList.size(); i ++) {
			Player currPlayer = currPlayerList.get(i);
			if (currPlayer.isBlack() == blackWon) {
				winner = currPlayer.userName();
				sendWinnerMessage(currPlayer);
			}
			else if (currPlayer.isBlack() != blackWon) {
				loser = currPlayer.userName();
				sendLoserMessage(currPlayer);
			}
		}
		
		updateRecord(winner, loser);
	}
	private synchronized void sendWinnerMessage(Player player) {
		JsonObject jo = new JsonObject();
		jo.addProperty("type", MessageType.WINNER.ordinal());
		try {
			playerToSession.get(player).getRemote().sendString(jo.toString());
		} catch (IOException e) {
			System.out.println("failed to send message");
		}
	}
	private synchronized void sendLoserMessage(Player player) {
		JsonObject jo = new JsonObject();
		jo.addProperty("type", MessageType.LOSER.ordinal());
		try {
			playerToSession.get(player).getRemote().sendString(jo.toString());
		} catch (IOException e) {
			System.out.println("failed to send message");
		}
	}
	

	private synchronized void joinGame(Session session, JsonObject received) {
		Game currGame = null;
		int size = matches.size();
		if (matches.size() == 0) {
			currGame = new Game(UUID.randomUUID());
			currGame.addPlayer(sessionToPlayer.get(session));
			matches.add(currGame);

		} else {
			Game currMatch = matches.get(size - 1);
			if (currMatch.maxPlayers()) {
				currGame = new Game(UUID.randomUUID());
				currGame.addPlayer(sessionToPlayer.get(session));
				matches.add(currGame);
			} else {
				currGame = currMatch;
				currMatch.addPlayer(sessionToPlayer.get(session));
			}
		}
		playerToGame.put(sessionToPlayer.get(session), currGame);
		if (currGame.gameStarted()) {
			List<Player> currGamePlayerList = currGame.getPlayerList();
			Player player1 = null;
			Player player2 = null;
			for (int i = 0; i < currGamePlayerList.size(); i++) {
				if (i == 0) {
					player1 = currGamePlayerList.get(i);
				} else if (i == 1) {
					player2 = currGamePlayerList.get(i);
				}
			}
			for (int i = 0; i < currGamePlayerList.size(); i++) {
				Player currPlayer = currGamePlayerList.get(i);

				JsonObject jo = new JsonObject();
				if (player1 == currPlayer) {
					jo.addProperty("opponent", player2.userName());
				} else {
					jo.addProperty("opponent", player1.userName());
				}
				jo.addProperty("type", MessageType.STARTGAME.ordinal());
				jo.addProperty("isBlack", currPlayer.isBlack());
				jo.addProperty("isTurn", currPlayer.isTurn());
				Session currSession = playerToSession.get(currPlayer);
				try {
					currSession.getRemote().sendString(jo.toString());
				} catch (IOException e) {
					System.out.println("unable to send message");
				}
			}
		}
	}

	private synchronized void getCords(int xCord, int yCord, JsonObject received, Board board, Session session) {
		Piece piece = board.getBoard()[xCord][yCord];
		JsonObject jo = new JsonObject();
		Map<String, Map<Integer, List<Integer>>> map = null;
		// Map<String, Object> variables = ImmutableMap.of("success", success);
		if (piece != null) {
			if (received.get("isBlack").getAsBoolean()) {
				if (piece.isBlack()) {
					map = board.getMovablePiecesBlack().get(piece);

				}
			} else {
				if (!piece.isBlack()) {
					map = board.getMovablePiecesRed().get(piece);
				}
			}
			if (map != null) {
				if (map.containsKey("jump")) {
					if (map.get("jump").size() != 0) {
						Map<Integer, List<Integer>> innerMap = map.get("jump");
						Map<String, Object> variables = ImmutableMap.of("type", MessageType.GETPOSSIBLEMOVES.ordinal(),
								"moveType", "jump", "pieceXCord", xCord, "pieceYCord", yCord, "cords", innerMap);
						jo.addProperty("type", MessageType.GETPOSSIBLEMOVES.ordinal());
						jo.addProperty("moveType", "jump");
						jo.addProperty("cords", map.get("jump").toString());
						jo.addProperty("pieceXCord", xCord);
						jo.addProperty("pieceYCord", yCord);
						try {
							session.getRemote().sendString(GSON.toJson(variables));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("couldn't send message");
						}
					}
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
							// TODO Auto-generated catch block
							System.out.println("couldn't send message");
						}

					}
				}
			}

		}

	}

	@OnWebSocketClose
	public synchronized void closed(Session session, int statusCode, String reason) {
		System.out.println("remove");
		if (sessionToPlayer.containsKey(session)) {
			Player currPlayer = sessionToPlayer.get(session);
			if (currPlayer.opponent() != null) {
				if (!currPlayer.opponent().hasForfeit()) {
					System.out.println("forfeit");
					currPlayer.forfeit();
					updateForfeit(currPlayer.opponent().userName(), currPlayer.userName());
					JsonObject jo = new JsonObject();
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
		Player currPlayer = sessionToPlayer.get(session);
		String currUserName = currPlayer.userName();
		playerList.remove(getEmail(currUserName));
		sessionToPlayer.remove(session);
		playerToSession.remove(currPlayer);
		Game currGame = playerToGame.remove(currPlayer);
		matches.remove(currGame);
		SESSIONS.remove(session);
	}

	private synchronized String getUserName(String email) {
		if (connection == null) {
			System.out.println("database is not connected");
			return "";
		}
		PreparedStatement prep;
		String userName = null;
		try {
			prep = connection.prepareStatement("SELECT userName FROM player WHERE email = ?;");
			prep.setString(1, email);
			ResultSet rs = prep.executeQuery();
			while (rs.next()) {
				userName = rs.getString(1);

			}
		} catch (SQLException e) {
			System.out.println("Entry not found");
		}
		return userName;

	}
	private synchronized void updateForfeit(String winner, String loser) {
		updateRecord(winner, loser);
		PreparedStatement prep;
		int forfeitWins = 0;
		int forfeits = 0;
		try {
			prep = connection.prepareStatement("SELECT winsByForfeit FROM player WHERE userName = ?;");
			prep.setString(1, winner);
			ResultSet rs = prep.executeQuery();
			while (rs.next()) { 
				forfeitWins = rs.getInt(1);

			}
			prep.close();
			prep = connection.prepareStatement("UPDATE player SET winsByForfeit =? WHERE userName = ?;");
			prep.setInt(1, forfeitWins + 1);
			prep.setString(2, winner);
			prep.executeUpdate();
			prep.close();
			prep = connection.prepareStatement("SELECT forfeits FROM player WHERE userName = ?;");
			prep.setString(1, loser);
			rs = prep.executeQuery();
			while (rs.next()) {
				forfeits = rs.getInt(1);

			}
			prep.close();
			prep = connection.prepareStatement("UPDATE player SET forfeits =? WHERE userName = ?;");
			prep.setInt(1, forfeits + 1);
			prep.setString(2, loser);
			prep.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Entry not found");
		}
	}
	private synchronized String getEmail(String userName) {
		if (connection == null) {
			System.out.println("database is not connected");
			return "";
		}
		PreparedStatement prep;
		String email = null;
		try {
			prep = connection.prepareStatement("SELECT email FROM player WHERE userName = ?;");
			prep.setString(1, userName);
			ResultSet rs = prep.executeQuery();
			while (rs.next()) {
				email = rs.getString(1);

			}
		} catch (SQLException e) {
			System.out.println("Entry not found");
		}
		return email;

	}

	private synchronized void updateRecord(String winner, String loser) {
		PreparedStatement prep;
		int wins = 0;
		int losses = 0;
		try {
			prep = connection.prepareStatement("SELECT wins FROM player WHERE userName = ?;");
			prep.setString(1, winner);
			ResultSet rs = prep.executeQuery();
			while (rs.next()) {
				wins = rs.getInt(1);

			}
			prep.close();
			System.out.println(wins);
			prep = connection.prepareStatement("UPDATE player SET wins =? WHERE userName = ?;");
			prep.setInt(1, wins + 1);
			prep.setString(2, winner);
			prep.executeUpdate();
			prep.close();
			prep = connection.prepareStatement("SELECT losses FROM player WHERE userName = ?;");
			prep.setString(1, loser);
			rs = prep.executeQuery();
			while (rs.next()) {
				losses = rs.getInt(1);

			}
			prep.close();
			prep = connection.prepareStatement("UPDATE player SET losses =? WHERE userName = ?;");
			prep.setInt(1, losses + 1);
			prep.setString(2, loser);
			prep.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Entry not found");
		}
	}
}
