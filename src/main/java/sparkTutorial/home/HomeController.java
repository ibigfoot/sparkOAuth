package sparkTutorial.home;

import java.util.Map;

import org.json.JSONObject;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.handlebars.HandlebarsTemplateEngine;
import sparkTutorial.BaseController;
import sparkTutorial.login.LoginController;

public class HomeController extends BaseController{

	public static Route home = (Request request, Response response) -> {
		
		init();
		JSONObject obj =  (JSONObject)request.session().attribute(LoginController.AUTH_SESSION);
		Map<String, Object> model = obj.toMap();
		model.putAll(((JSONObject)request.session().attribute("identity")).toMap());
		
		for(String s : model.keySet()) {
			LOG.info("We have key [{}] value[{}]", s, model.get(s));
		}
		
		HandlebarsTemplateEngine ht = new HandlebarsTemplateEngine();

		ModelAndView mAndV = new ModelAndView(model, "home/home.hbs");
		return ht.render(mAndV);
	};
}
