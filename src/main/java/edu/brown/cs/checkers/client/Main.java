package edu.brown.cs.checkers.client;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.checkers.game.Matches;
import edu.brown.cs.checkers.records.playerRecords;
import freemarker.template.Configuration;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * Created by Justin on 6/7/2017.
 */
public final class Main {

	private static final int DEFAULT_PORT = 4567;
	private static final Gson GSON = new Gson();
	private static playerRecords database;
	private static Matches matches;

	/**
	 * The initial method called when execution begins.
	 *
	 * @param args
	 *            An array of command line arguments
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		database = new playerRecords();
		Class.forName("org.sqlite.JDBC");
		String urlToDB = "jdbc:sqlite:" + "db.sqlite3";
		Connection conn = DriverManager.getConnection(urlToDB);
		// these two lines tell the database to enforce foreign
		// keys during operations, and should be present
		Statement stat = conn.createStatement();
		stat.executeUpdate("PRAGMA foreign_keys = ON;");
		database.setConnection(conn);
		System.out.println("here");
		matches = new Matches();
		matches.setConnection(conn);
		System.out.println("there");
		new Main(args).run();
	}

	private String[] args;

	private Main(String[] args) {
		this.args = args;

	}

	private void run() {
		// Parse command line arguments
		OptionParser parser = new OptionParser();
		parser.accepts("gui");
		parser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(DEFAULT_PORT);
		OptionSet options = parser.parse(args);
		if (options.has("gui")) {
			runSparkServer((int) options.valueOf("port"));
		}

	}

	private static FreeMarkerEngine createEngine() {
		Configuration config = new Configuration();
		File templates = new File("src/main/resources/spark/template/freemarker");
		try {
			config.setDirectoryForTemplateLoading(templates);
		} catch (IOException ioe) {
			System.out.printf("ERROR: Unable use %s for template loading.%n", templates);
			System.exit(1);
		}
		return new FreeMarkerEngine(config);
	}

	private void runSparkServer(int port) {
		Spark.port(port);
		Spark.externalStaticFileLocation("src/main/resources/static");
		Spark.exception(Exception.class, new ExceptionPrinter());
		FreeMarkerEngine freeMarker = createEngine();
		Spark.webSocket("/matches", Matches.class);
		Spark.get("/game", new GamePageHandler(), freeMarker);
		Spark.get("/login", new LoginHandler(), freeMarker);
		Spark.get("/", new HomePageHandler(), freeMarker);
		Spark.post("/createPlayer", new CreateUserHandler());
		Spark.post("getRecord", new recordHandler());
		Spark.post("/checkDuplicate", new duplicateConnectionHandler());
		//
	}

	// /**
	// * Handle requests to the front page of our Stars website.
	// *
	// * @author jj
	// */
	private static class LoginHandler implements TemplateViewRoute {
		@Override
		public ModelAndView handle(Request req, Response res) {
			Map<String, Object> variables = ImmutableMap.of();
			return new ModelAndView(variables, "login.ftl");
		}
	}

	private static class HomePageHandler implements TemplateViewRoute {
		@Override
		public ModelAndView handle(Request req, Response res) {
			Map<String, Object> variables = ImmutableMap.of();
			return new ModelAndView(variables, "home.ftl");
		}
	}

	private static class CreateUserHandler implements Route {
		@Override
		public String handle(Request req, Response res) {
			QueryParamsMap qm = req.queryMap();
			String firstName = qm.value("firstName");
			String lastName = qm.value("lastName");
			String email = qm.value("email");
			String userName = qm.value("userName");
			System.out.println(userName);
			boolean success = database.addPlayer(email, firstName, lastName, userName);
			System.out.println("sucess " + success);
			Map<String, Object> variables = ImmutableMap.of("success", success);
			return GSON.toJson(variables);
		}
	}

	private static class recordHandler implements Route {
		@Override
		public String handle(Request req, Response res) {
			QueryParamsMap qm = req.queryMap();
			String email = qm.value("email");
			int[] record = database.getRecord(email);
			System.out.println(record);
			Map<String, Object> variables = ImmutableMap.of("record", record);
			return GSON.toJson(variables);
		}
	}
	private static class duplicateConnectionHandler implements Route {
		@Override
		public String handle(Request req, Response res) {
			QueryParamsMap qm = req.queryMap();
			String email = qm.value("email");
			boolean duplicate = matches.duplicateConnection(email);
			System.out.println("duplicate " + duplicate);
			Map<String, Object> variables = ImmutableMap.of("duplicate", duplicate);
			return GSON.toJson(variables);
		}
	}

	private static class GamePageHandler implements TemplateViewRoute {
		@Override
		public ModelAndView handle(Request req, Response res) {
			Map<String, Object> variables = ImmutableMap.of();
			return new ModelAndView(variables, "game.ftl");
		}

	}

	/*
	 * Display an error page when an exception occurs in the server.
	 *
	 * @author jj
	 */
	private static class ExceptionPrinter implements ExceptionHandler {
		@Override
		public void handle(Exception e, Request req, Response res) {
			res.status(500);
			StringWriter stacktrace = new StringWriter();
			try (PrintWriter pw = new PrintWriter(stacktrace)) {
				pw.println("<pre>");
				e.printStackTrace(pw);
				pw.println("</pre>");
			}
			res.body(stacktrace.toString());
		}
	}

}
