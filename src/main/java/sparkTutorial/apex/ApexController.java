package sparkTutorial.apex;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.handlebars.HandlebarsTemplateEngine;
import sparkTutorial.APIUtils;
import sparkTutorial.BaseController;
import sparkTutorial.login.LoginController;

public class ApexController extends BaseController{

	public static Route getApex = (Request request, Response response) -> {
		
		init();
		
		APIUtils utils = new APIUtils();
		JSONObject session = request.session().attribute(LoginController.AUTH_SESSION);
		JSONObject identity = request.session().attribute("identity");

		JSONObject urls = identity.getJSONObject("urls");
		String queryURL = urls.getString("query");
		
		queryURL = queryURL.replace("{version}", "37.0");
		
		String query = "select ApiVersion,BodyCrc,CreatedById,CreatedDate,Id,IsValid,LastModifiedById,LastModifiedDate,LengthWithoutComments,Name,NamespacePrefix,Status,SystemModstamp FROM ApexClass";
		Map<String, String> params = new HashMap<String, String>();
		params.put("q", query);
		
		JSONObject apexClasses = utils.getFromUrl(queryURL, session.getString("access_token"), params);

		Map<String, Object> model = apexClasses.toMap();
		
		for(String s : model.keySet()) {
			LOG.info("We have key [{}] value[{}]", s, model.get(s));
		}
		
		HandlebarsTemplateEngine ht = new HandlebarsTemplateEngine();

		ModelAndView mAndV = new ModelAndView(model, "apex/apex.hbs");
		return ht.render(mAndV);		
	};
	
	
	public static Route getApexDetail = (Request request, Response response) -> {

		init();
		
		APIUtils utils = new APIUtils();
		JSONObject session = request.session().attribute(LoginController.AUTH_SESSION);
		JSONObject identity = request.session().attribute("identity");
		
		String apexClassId = request.params("id");
		
		JSONObject urls = identity.getJSONObject("urls");
		String apexDetailURL = urls.getString("sobjects");
		apexDetailURL = apexDetailURL.replace("{version}","37.0");
		apexDetailURL += "ApexClass/"+apexClassId;
		
		JSONObject apexDetail = utils.getFromUrl(apexDetailURL, session.getString("access_token"), null);
		
		Map<String, Object> model = apexDetail.toMap();
		
		for(String s : model.keySet()) {
			LOG.info("We have key [{}] value[{}]", s, model.get(s));
		}
		
		HandlebarsTemplateEngine ht = new HandlebarsTemplateEngine();

		ModelAndView mAndV = new ModelAndView(model, "apex/apex.hbs");
		return ht.render(mAndV);
	};
}
