package edu.brown.cs.checkers.records;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class playerRecords {
	private static Connection conn;
	public playerRecords() {
	}
	public void setConnection(Connection connection) {
		if (connection!=null) {
			conn = connection;
		}
	}
	public int[] getRecord(String email) {
		int[] record = new int[2];
		if (conn == null) {
			System.out.println("database is not connected");
			return null;
		}
		PreparedStatement prep;
		try {
			prep = conn.prepareStatement(
					  "SELECT wins, losses FROM player WHERE email = ?;");
				System.out.println("prep passes");
				prep.setString(1, email);
				System.out.println("setString passes");
				ResultSet rs = prep.executeQuery();
				System.out.println("resultset passes");

				while (rs.next()) {
					System.out.println("loop");
				  int wins = rs.getInt(1);
				  int losses = rs.getInt(2);
				  record[0] = wins;
				  record[1] = losses;
				}
				System.out.println("outside of loop");
				rs.close();
		} catch (SQLException e) {
			System.out.println("exception handler");
			return null;
		}
		return record;
	}
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
			    + "PRIMARY KEY (email));");
			 prep.executeUpdate();
			 System.out.println("here");
			 prep = conn.prepareStatement( "SELECT wins FROM player WHERE userName = ?;");
			 prep.setString(1, userName);
			 ResultSet rs = prep.executeQuery();
			 System.out.println("there");
			 while (rs.next()) {
				 System.out.println("duplicate user name");
				 return false;
			 }
			 rs.close();
			 System.out.println("everywhere");
			 prep = conn.prepareStatement(
					  "INSERT INTO player VALUES (?, ?, ?, ?, ?, ?, ?);");
					prep.setString(1, email);
					System.out.println("blah");
					prep.setString(2, firstName);
					System.out.println("blahbede");
					prep.setString(3, lastName);
					System.out.println("blahblah");
					prep.setInt(4, 0);
					System.out.println("yeek");
					prep.setInt(5, 0);
					prep.setInt(6, 0);
					System.out.println("cry");
					prep.setString(7, userName);
					System.out.println("geez");
					prep.addBatch();
					prep.executeBatch();

		} catch (SQLException e) {
			System.out.println("Couldn't create new entry");
			return false;
		}
		return true;
	}
	private synchronized String getUserName(String email) {
		if (conn == null) {
			System.out.println("database is not connected");
			return "";
		}
		PreparedStatement prep;
		String userName = null;
		try {
			prep = conn.prepareStatement(
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
