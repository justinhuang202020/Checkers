package checkers.repl;

import java.io.*;
import java.util.Map;

import com.google.gson.Gson;
import freemarker.template.Configuration;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * Created by Justin on 6/7/2017.
 */
public final class Main {

    private static final int DEFAULT_PORT = 4567;
    // private static StartProgram p;
    private static final Gson GSON = new Gson();


    /**
     * The initial method called when execution begins.
     *
     * @param args An array of command line arguments
     */
    public static void main(String[] args) {
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
        parser.accepts("port").withRequiredArg().ofType(Integer.class)
                .defaultsTo(DEFAULT_PORT);
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
            System.out.printf("ERROR: Unable use %s for template loading.%n",
                    templates);
            System.exit(1);
        }
        return new FreeMarkerEngine(config);
    }

    private void runSparkServer(int port) {
        Spark.port(port);
        Spark.externalStaticFileLocation("src/main/resources/static");
        Spark.exception(Exception.class, new ExceptionPrinter());
        FreeMarkerEngine freeMarker = createEngine();
//        Spark.get("/maps", new MapHandler(), freeMarker);
//        Spark.post("/tiles", new TileLoader());
//        Spark.post("/route", new PathHandler());
//        Spark.post("/traffic", new FrontTrafficHandler());
//        Spark.post("/suggestions", new AutoCorrectHandler());
    }




//    /**
//     * Handle requests to the front page of our Stars website.
//     *
//     * @author jj
//     */
//    private static class MapHandler implements TemplateViewRoute {
//        @Override
//        public ModelAndView handle(Request req, Response res) {
//            Map<String, Object> variables =
//                    ImmutableMap.of("title", "Welcome to Maps!");
//            return new ModelAndView(variables, "home.ftl");
//        }
//    }



    /**
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
