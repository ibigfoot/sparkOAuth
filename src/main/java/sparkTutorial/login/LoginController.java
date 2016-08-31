package sparkTutorial.login;

import java.net.URLEncoder;

import org.json.JSONObject;

import spark.Filter;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.handlebars.HandlebarsTemplateEngine;
import sparkTutorial.APIUtils;
import sparkTutorial.BaseController;

public class LoginController extends BaseController {

	public static final String AUTH_SESSION = "authSession";

	/*
	 * troy@documenter.org - prod callback
	 * troy@documenter.dev - localhost callback
	 */
	public static Route loginPage = (Request request, Response response) -> {
		
		init();
		HandlebarsTemplateEngine ht = new HandlebarsTemplateEngine();
		ModelAndView mAndV = new ModelAndView(model, "login/login.hbs");
		return ht.render(mAndV);

	};

	public static Route oauth = (Request request, Response response) -> {
		
		LOG.info("We are processing an OAUTH call [{}]", request.url());
		APIUtils utils = new APIUtils();
		
		// if we are being sent here without a callback or an authenticated session
		if(request.session().attribute(AUTH_SESSION) == null && !request.url().contains("_callback")) {
			String redirectURL = "";
			String state = "";
			if(request.url().contains("production")) {
				redirectURL += "https://login.salesforce.com";
				state = "production";
			} else if (request.url().contains("sandbox")) {
				redirectURL += "https://test.salesforce.com";
				state = "sandbox";
			}
			redirectURL += "/services/oauth2/authorize?";
			redirectURL += "response_type=code&client_id=";
			
			redirectURL += URLEncoder.encode(utils.getClientId(),"UTF-8") +"&redirect_uri="+ URLEncoder.encode(utils.getRedirectURI(), "UTF-8") + "&state="+state;
			LOG.info("Redirecting the user to [{}]", redirectURL);
			response.redirect(redirectURL); // redirect user to the SF login service

		}  

		if(request.url().contains("_callback") && request.queryParams("code") != null) {
			LOG.info("We have detected a callback!");
			
			String authToken = request.queryParams("code");
			String state = request.queryParams("state");
			LOG.info("We have an auth token [{}] and state [{}]", authToken, state);

			String postURL = (state.equals("production") ? "https://login.salesforce.com" : "https://test.salesforce.com") + "/services/oauth2/token";
			
			JSONObject authSession = utils.requestAccessToken(postURL, authToken);
			LOG.info("We have result from POST [{}]", authSession.toString());
			request.session().attribute(AUTH_SESSION, authSession);
			
			JSONObject identity = utils.postToUrl((String)authSession.get("id"), (String)authSession.get("access_token"), null);
			LOG.info("We have a result from Identity POST [{}]", identity.toString());
			request.session().attribute("identity", identity);
			
			response.redirect("/home");
		}
		// if we have ended up at the OAuth URL and we already have a session. 
		if (request.session().attribute(AUTH_SESSION) != null) {
			LOG.info("We have an authenticated session.. ");
			response.redirect("/home");
		}
		return null;
		
	};
	
	public static Filter isUserAuthenticated = (Request request, Response response) -> {
		LOG.info("We are checking if user is authenticated for [{}]", request.url());
		if (request.session().attribute(AUTH_SESSION) == null && 
				!request.url().contains("login") && 
				!request.url().contains("oauth")) {
			response.redirect("/login");
		}
	};
	

}
