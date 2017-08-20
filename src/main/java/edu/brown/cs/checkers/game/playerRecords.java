package edu.brown.cs.checkers.game;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 *handles all all database commands
 * @author Justin Huang
 *
 */
public class playerRecords {
	private static Connection conn;
	public playerRecords() {
	}
	public void setConnection(Connection connection) {
		if (connection!=null) {
			conn = connection;
		}
	}
	/**
	 *gets the record of the player based on their email
	 * @param email
	 * @return
	 */
	public int[] getRecord(String email) {
		int[] record = new int[4];
		if (conn == null) {
			System.out.println("database is not connected");
			return null;
		}
		PreparedStatement prep;
		try {
			prep = conn.prepareStatement(
					  "SELECT wins, losses, winsByForfeit, forfeits FROM player WHERE email = ?;");
				prep.setString(1, email);
				ResultSet rs = prep.executeQuery();

				while (rs.next()) {
				  int wins = rs.getInt(1);
				  int losses = rs.getInt(2);
				  int forfeitWins = rs.getInt(3);
				  int forfeits = rs.getInt(4);
				  record[0] = wins;
				  record[1] = losses;
				  record[2] = forfeitWins;
				  record[3] = forfeits;
				}
				rs.close();
		} catch (SQLException e) {
			System.out.println("exception handler");
			return null;
		}
		return record;
	}
	/**
	 * adds a player to the database when they sign up for their login
	 * @param email
	 * @param firstName
	 * @param lastName
	 * @param userName
	 * @return
	 */
	public boolean addPlayer(String email, String firstName, String lastName, String userName) {
		if (conn == null) {
			System.out.println("database is not connected");
			return false;
		}
		PreparedStatement prep;
		try {
			prep = conn.prepareStatement("CREATE TABLE IF NOT EXISTS player("
			    + "email TEXT,"
			    + "firstName TEXT,"
			    + "lastName TEXT,"
			    + "wins INTEGER,"
			    + "losses INTEGER,"
			    + "forfeits INTEGER,"
			    + "userName TEXT,"
			    + "winsByForfeit INTEGER,"
			    + "PRIMARY KEY (email));");
			 prep.executeUpdate();
			 // this section makes sure the userName is unique
			 prep = conn.prepareStatement( "SELECT wins FROM player WHERE userName = ?;");
			 prep.setString(1, userName);
			 ResultSet rs = prep.executeQuery();
			 while (rs.next()) {
				 return false;
			 }
			 rs.close();
			 prep = conn.prepareStatement(
					  "INSERT INTO player VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
					prep.setString(1, email);
					prep.setString(2, firstName);
					prep.setString(3, lastName);
					prep.setInt(4, 0);
					prep.setInt(5, 0);
					prep.setInt(6, 0);
					prep.setString(7, userName);
					prep.setInt(8, 0);
					prep.addBatch();
					prep.executeBatch();
					prep.close();

		} catch (SQLException e) {
			System.out.println("Couldn't create new entry");
			return false;
		}
		return true;
	}
	/**
	 *gets the userName of a player based on their email
	 * @param email
	 * @return
	 */
	public synchronized String getUserName(String email) {
		if (conn == null) {
			System.out.println("database is not connected");
			return "";
		}
		PreparedStatement prep;
		String userName = null;
		try {
			prep = conn.prepareStatement("SELECT userName FROM player WHERE email = ?;");
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
	/**
	 *if a player forfeits, this method is called
	 * @param winner userName
	 * @param loser or player that forfeit's userName
	 */
	protected synchronized void updateForfeit(String winner, String loser) {
		updateRecord(winner, loser);
		PreparedStatement prep;
		int forfeitWins = 0;
		int forfeits = 0;
		try {
			// gets the winsByForfeit from the current player
			prep = conn.prepareStatement("SELECT winsByForfeit FROM player WHERE userName = ?;");
			prep.setString(1, winner);
			ResultSet rs = prep.executeQuery();
			while (rs.next()) { 
				forfeitWins = rs.getInt(1);

			}
			prep.close();
			//increments winsByForfeit by 1
			prep = conn.prepareStatement("UPDATE player SET winsByForfeit =? WHERE userName = ?;");
			prep.setInt(1, forfeitWins + 1);
			prep.setString(2, winner);
			prep.executeUpdate();
			prep.close();
			//gets forfeits from the player that forfeited
			prep = conn.prepareStatement("SELECT forfeits FROM player WHERE userName = ?;");
			prep.setString(1, loser);
			rs = prep.executeQuery();
			while (rs.next()) {
				forfeits = rs.getInt(1);

			}
			prep.close();
			//increments forfeits by 1
			prep = conn.prepareStatement("UPDATE player SET forfeits =? WHERE userName = ?;");
			prep.setInt(1, forfeits + 1);
			prep.setString(2, loser);
			prep.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Entry not found");
		}
	}
	/**
	 *
	 * @param userName
	 * @return email based on userName
	 */
	public synchronized String getEmail(String userName) {
		if (conn == null) {
			System.out.println("database is not connected");
			return "";
		}
		PreparedStatement prep;
		String email = null;
		try {
			prep = conn.prepareStatement("SELECT email FROM player WHERE userName = ?;");
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
	/**
	 *updates the record with wins and losses based on the userNames 
	 * @param winner userName
	 * @param loser loserName
	 */
	protected synchronized void updateRecord(String winner, String loser) {
		PreparedStatement prep;
		int wins = 0;
		int losses = 0;
		try {
			//gets wins based on the userName of the winner
			prep = conn.prepareStatement("SELECT wins FROM player WHERE userName = ?;");
			prep.setString(1, winner);
			ResultSet rs = prep.executeQuery();
			while (rs.next()) {
				wins = rs.getInt(1);

			}
			prep.close();
			//increments wins by 1
			prep = conn.prepareStatement("UPDATE player SET wins =? WHERE userName = ?;");
			prep.setInt(1, wins + 1);
			prep.setString(2, winner);
			prep.executeUpdate();
			prep.close();
			//gets losses based on the userName of the loser
			prep = conn.prepareStatement("SELECT losses FROM player WHERE userName = ?;");
			prep.setString(1, loser);
			rs = prep.executeQuery();
			while (rs.next()) {
				losses = rs.getInt(1);

			}
			prep.close();
			//increments losses by 1
			prep = conn.prepareStatement("UPDATE player SET losses =? WHERE userName = ?;");
			prep.setInt(1, losses + 1);
			prep.setString(2, loser);
			prep.executeUpdate();
			prep.close();
		} catch (SQLException e) {
			System.out.println("Entry not found");
		}
	}

}
