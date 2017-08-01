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
	 private List<Game> matches = Collections.synchronizedList(new
	 ArrayList<>());
	// private Map<UUID, Match> matchIdToClass =
	// Collections.synchronizedMap(new HashMap<>());
	
	private Map<String, Session> playerToSession = Collections.synchronizedMap(new HashMap<>());
	private Map<Session, String> sessionToPlayer = Collections.synchronizedMap(new HashMap<>());
	private Map<String, UUID> playerToGame = Collections.synchronizedMap(new HashMap<>());
	private Map <String, String> emailToPlayerID = Collections.synchronizedMap(new HashMap<>());
	public Matches() {
		playerList = new ArrayList<>();
		connect = null;
//		connection = conn;
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
			System.out.println("connected");
			String userName = getUserName(connect);
			SESSIONS.add(session);
			sessionToPlayer.put(session, userName);
			playerToSession.put(userName, session);
			JsonObject jo = new JsonObject();
			jo.addProperty("type", MessageType.JOINGAME.ordinal());
			jo.addProperty("userName", userName);
			System.out.println("sent");
			 session.getRemote().sendString(jo.toString());
			

		} catch (IOException ex) {
			System.out.println("ERROR: IOexception caught while connecting session.");
		}
	}
	 @OnWebSocketMessage
	  public synchronized void message(Session session, String message) throws IOException {

	    
	      // Get received message
	      JsonObject received = GSON.fromJson(message, JsonObject.class);
	       if (received.get("type").getAsInt() == MessageType.JOINGAME.ordinal()) {
	    	  System.out.println("searching");
	    	  String userName = received.get("userName").getAsString();
	    	  Game currGame = null;
	    	  int size = matches.size();
	    	  System.out.println("size " + size);
	    	  if(matches.size() == 0) {
	    		  System.out.println("if");
	    		  currGame = new Game(UUID.randomUUID());
	    		  currGame.addPlayer(new Player(userName));
	    		  matches.add(currGame);
	    		  
	    	  }
	    	  else  {
	    		  System.out.println("else");
	    		  Game currMatch = matches.get(size-1);
	    		 if (currMatch.maxPlayers()) {
	    			 currGame = new Game(UUID.randomUUID());
	    			 currGame.addPlayer(new Player(userName));
		    		 matches.add(currGame);
	    		 }
	    		 else {
	    			 currGame = currMatch;
	    			 currMatch.addPlayer(new Player(received.get("userName").getAsString()));	    			 
	    		 }
	    	  }
	    	  System.out.println(userName);
	    	  System.out.println(currGame);
	    	  playerToGame.put(userName, currGame.getGameID());
	    	  if (currGame.gameStarted()) {
	    		  System.out.println("game started");
	    		  List<Player> currGamePlayerList = currGame.getPlayerList();
	    		  for (int i = 0; i<currGamePlayerList.size(); i ++) {
	    			  	Player currPlayer = currGamePlayerList.get(i);
	    			  	JsonObject jo = new JsonObject();
	    			  	 jo.addProperty("type", MessageType.STARTGAME.ordinal());
		    			 jo.addProperty("isBlack", currPlayer.isBlack());
		    			 Session currSession = playerToSession.get(currPlayer.userName());
		    			 currSession.getRemote().sendString(jo.toString());
	    		  }
	    	  }
	      }

	 }
	@OnWebSocketClose
	  public synchronized void closed(Session session, int statusCode, String reason) {
	    // Update the lobbies and remove
	    // this player from our list
//	    removePlayer(session);
		System.out.println("remove");
		String curr = sessionToPlayer.get(session);
		playerList.remove(curr);
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
			prep = connection.prepareStatement(
					  "SELECT userName, losses FROM player WHERE email = ?;");
			prep.setString(1, email);
			ResultSet rs = prep.executeQuery();
			while(rs.next()) {
				userName = rs.getString(1);
				
			}
		} catch (SQLException e) {
			System.out.println("Entry not found");
		}
		return userName;
		
	}

}
