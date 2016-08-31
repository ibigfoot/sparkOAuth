package sparkTutorial.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Filter;
import spark.Request;
import spark.Response;

public class BeforeFilters {

	private static Logger LOG = LoggerFactory.getLogger(BeforeFilters.class);
	
	public static Filter addTrailingSlashes = (Request request, Response response) -> {
		
		LOG.info("We are adding trailing slashes to the request if they don't exist");
		if(!request.pathInfo().endsWith("/")) {
			response.redirect(request.pathInfo() + "/");
		}
	};
}
