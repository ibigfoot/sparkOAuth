package sparkTutorial;

import static spark.Spark.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HelloWorld {

	private static Logger LOG;
	public static void main(String[] args ){
		LOG = LoggerFactory.getLogger(HelloWorld.class);
		LOG.info("We are logging");
		
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
}
