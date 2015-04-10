package net.floodlightcontroller.ccntopology;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.restserver.RestletRoutable;

public class CCNTopologyWebRoutable implements RestletRoutable {

	@Override
	public Restlet getRestlet(Context context) {
		Router router = new Router(context);
		router.attach("/{request}/json", CCNTopologyResource.class);
		return router;
	}

	@Override
	public String basePath() {
		return "/wm/ccntopology";
	}

}
