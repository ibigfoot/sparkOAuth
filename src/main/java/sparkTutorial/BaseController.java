package sparkTutorial;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sparkTutorial.login.LoginController;

public abstract class BaseController {

	protected static Map<String, Object> model;
	protected static Logger LOG = LoggerFactory.getLogger(BaseController.class);
	
	protected static void init() {
		model = new HashMap<String, Object>();
		model.put("applicationName", "Org Documenter");
	}
}
