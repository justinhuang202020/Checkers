package edu.brown.cs.checkers.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import edu.brown.cs.checkers.game.Board;
import edu.brown.cs.checkers.game.Game;
import edu.brown.cs.checkers.game.MatchHandler;
import edu.brown.cs.checkers.game.Player;
import edu.brown.cs.checkers.game.playerRecords;


@WebSocket
/**
 *handles websocket connections and messages. also checks for duplicate connections
 * @author Justin Huang
 *
 */
public class Matches {
	private static List<String> playerList;
	private static final Gson GSON = new Gson();
	private static MatchHandler match;
	private static String connect;
	private static final Queue<Session> SESSIONS = new ConcurrentLinkedQueue<>();

	private List<Game> matches = Collections.synchronizedList(new ArrayList<>());
	private Map<Player, Session> playerToSession = Collections.synchronizedMap(new HashMap<>());
	private Map<Session, Player> sessionToPlayer = Collections.synchronizedMap(new HashMap<>());
	private static playerRecords record;
	private Map<Player, Game> playerToGame = Collections.synchronizedMap(new HashMap<>());

	public Matches() {
		match = new MatchHandler();
		playerList = new ArrayList<>();
		connect = null;
		record = new playerRecords();

	}


/**
 *makes sure that a user's email is not already in the list.
 *If so there is already a current session and the connection is rejected
 * @param email
 * @return
 */
	public synchronized boolean duplicateConnection(String email) {
		if (playerList.contains(email)) {
			return true;
		}
		connect = email;
		return false;
	}

	/**
	 *adds the session/player to the corresponding data structures and sends a message
	 *to the front end that the player is joining a game
	 * @param session
	 */
	@OnWebSocketConnect
	public synchronized void connected(Session session) {
		try {
			playerList.add(connect);
			String userName = record.getUserName(connect);
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
				match.joinGame(session, received, matches, sessionToPlayer, playerToGame, playerToSession);
			}
			if (received.get("type").getAsInt() == MessageType.GETCORDS.ordinal()) {
				Player currPlayer = sessionToPlayer.get(session);
				Game currGame = playerToGame.get(currPlayer);
				Board board = currGame.getBoard();
				int xCord = received.get("pieceXCord").getAsInt();
				int yCord = received.get("pieceYCord").getAsInt();
				match.getCords(xCord, yCord, received, board, session);
			}
			if (received.get("type").getAsInt() == MessageType.MOVE.ordinal()) {
				match.validateAndMove(session, received, playerToSession, playerToGame, sessionToPlayer);

			}
			if (received.get("type").getAsInt() == MessageType.MESSAGE.ordinal()) {
				match.message(session, received, playerToSession, sessionToPlayer);
				
			}
		}

	}



	@OnWebSocketClose
	/**
	 *updates the database accordingly and removes the player and session from the 
	 *corresponding data structures. 
	 * @param session
	 * @param statusCode
	 * @param reason
	 */
	public synchronized void closed(Session session, int statusCode, String reason) {
		match.checkForForfeits(session, sessionToPlayer, playerToSession);
		Player currPlayer = sessionToPlayer.get(session);
		String currUserName = currPlayer.userName();
		playerList.remove(record.getEmail(currUserName));
		sessionToPlayer.remove(session);
		playerToSession.remove(currPlayer);
		Game currGame = playerToGame.remove(currPlayer);
		matches.remove(currGame);
		SESSIONS.remove(session);
	}

	
}
