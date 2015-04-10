package net.floodlightcontroller.ccntopology;

/*
 * This file handles the REST API Request
 * Here we send action and host IP address 
 * Based on that we add flows and print CCN Node
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;

import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFPacketOut;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.OFType;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionDataLayerDestination;
import org.openflow.protocol.action.OFActionNetworkLayerDestination;
import org.openflow.protocol.action.OFActionOutput;
import org.openflow.protocol.action.OFActionTransportLayerDestination;
import org.openflow.util.HexString;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CCNTopologyResource extends ServerResource {

	protected static Logger logger;
	//Include a CCN node array to hold the list of CCN Nodes
	/*
	private ArrayList<CCNnode> ccnNode = new ArrayList<CCNnode>() {
		{
			add(new CCNnode("ip_address", "mac_address", "port"))
		}
	};*/
	
	@Get("json")
	public String process() {
		String data = (String) getRequestAttributes().get("request");
		String[] tokens = data.split(","); // The incoming is action,ip
		
		try {
			if (tokens[0].equalsIgnoreCase("add"))
				return add(tokens[1]);
			else if (tokens[0].equalsIgnoreCase("del"))
				return del(tokens[1]);
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private String add(String host_ip) {
		/* Print that CCN Node is up 
		 * Add Flow 
		 * Modify the Web UI as the next goal
		 */
		logger.debug("***CCN Node is up ***" + "With IP: " + host_ip);
		//writeHostFlow(host_ip);
		
		return null;
	}
	
	private String del(String host_ip) {
		//Del the CCN Node which is up
		//Remove it from the Web UI
		
		return null;
	}
}
