package sparkTutorial;

import static spark.Spark.*;
import static spark.debug.DebugScreen.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sparkTutorial.apex.ApexController;
import sparkTutorial.filters.BeforeFilters;
import sparkTutorial.home.HomeController;
import sparkTutorial.login.LoginController;

public class OrgDocumenter {

	private static Logger LOG;

	public static void main(String[] args) {

		int port = getHerokuAssignedPort();

		LOG = LoggerFactory.getLogger(OrgDocumenter.class);
		port(port);
		enableDebugScreen();	
		staticFiles.location("/static");
		
		before("*", LoginController.isUserAuthenticated);
		
		get("/login", LoginController.loginPage);

		get("/oauth/*", LoginController.oauth);
		
		get("/home", HomeController.home);
		
		get("/apex", ApexController.getApex);
		
		get("/apexclass/:id", ApexController.getApexDetail);
		
/*
		after((request, response) -> {
			LOG.info("we are attempting some after filter");
		});
*/
	}

	static int getHerokuAssignedPort() {
		ProcessBuilder processBuilder = new ProcessBuilder();
		if (processBuilder.environment().get("PORT") != null) {
			return Integer.parseInt(processBuilder.environment().get("PORT"));
		}
		return 4567; // return default port if heroku-port isn't set (i.e. on
						// localhost)
	}
}
