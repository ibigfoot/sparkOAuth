package sparkTutorial.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Filter;
import spark.Request;
import spark.Response;

public class BeforeFilters {

	private static Logger LOG = LoggerFactory.getLogger(BeforeFilters.class);
	
	public static Filter addTrailingSlashes = (Request request, Response response) -> {
		
		LOG.info("This is where our before filters would go");
		if(!request.pathInfo().endsWith("/")) {
			response.redirect(request.pathInfo() + "/");
		}
	};
}
