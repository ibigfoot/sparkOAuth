package sparkTutorial;

import static spark.Spark.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HelloWorld {

	private static Logger LOG;
	public static void main(String[] args ){
		
		int port = getHerokuAssignedPort();
		
		LOG = LoggerFactory.getLogger(HelloWorld.class);
		LOG.info("We are logging using Port [{}]", port);
		port(port);
		before((request, response) -> {
			LOG.info("We are attempting some before filter");
        });
		
		get("/hello", (request, response) -> {
			return "Hello World";
		});
		get("/hello/:name", (request, response) -> {
			return "Hello " + request.params("name");
		});
		get("/say/*/to/*", (request, response) -> {
			String say = request.splat()[0];
			String to = request.splat()[1];
			return say + " " + to;
		});
		
		after((request, response) -> {
			LOG.info("we are attempting some after filter");
		});
	}
	
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }	
}
