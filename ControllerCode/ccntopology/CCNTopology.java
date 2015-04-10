package net.floodlightcontroller.ccntopology;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketIn;
import org.openflow.protocol.OFPacketOut;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.OFType;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionDataLayerDestination;
import org.openflow.protocol.action.OFActionNetworkLayerDestination;
import org.openflow.protocol.action.OFActionNetworkLayerSource;
import org.openflow.protocol.action.OFActionOutput;
import org.openflow.protocol.action.OFActionTransportLayerDestination;
import org.openflow.protocol.action.OFActionTransportLayerSource;
import org.openflow.util.HexString;
import org.openflow.util.U16;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.devicemanager.SwitchPort;
import net.floodlightcontroller.devicemanager.internal.Device;
import net.floodlightcontroller.packet.BasePacket;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.routing.Link;
import net.floodlightcontroller.routing.Route;
import net.floodlightcontroller.staticflowentry.IStaticFlowEntryPusherService;
//import net.floodlightcontroller.ccntopology.CCNNode;


public class CCNTopology implements IFloodlightModule, ICCNTopologyService, IOFMessageListener {

	protected IFloodlightProviderService floodlightProvider;
	protected IRestApiService restApi;
	protected IDeviceService deviceManager;
	protected IRoutingService routingService;
	protected IStaticFlowEntryPusherService staticFlowPusher; 
	protected static Logger logger;
	
	private String HOST_IP1 = "192.168.1.149";
	private String HOST_IP2 = "192.168.1.153";
	private String HOST_MAC = "";
	
	static int counter;
	
	public ArrayList<CCNNode> ccnnode = new ArrayList<CCNNode>();
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "CCNTopology";
	}

	// isCallbackOrderingPre/Postreq are set as false only to disable the order
	// in which the packets are received. Need to be modified only if necessary
	
	// The packets are first sent to device manager and then this module
	// To make sure all packets are received at this module
	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		if ((name.equalsIgnoreCase("devicemanager") && (type.equals(OFType.PACKET_IN))))
			return true;
		else
			return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	/*@Override
	public net.floodlightcontroller.core.IListener.Command receive(
			IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		// TODO Auto-generated method stub
		return null;
	}*/

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		// Use this if you want the module to be loaded as Floodlight and not independently
		//		l.add(IFloodlightProviderService.class);
		l.add(ICCNTopologyService.class);
		return l;
		//return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		Map<Class<? extends IFloodlightService>, IFloodlightService> m = new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
		m.put(ICCNTopologyService.class, this);
		return m;
		//return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		// TODO Auto-generated method stub
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		l.add(IRestApiService.class);
		l.add(IDeviceService.class);
		l.add(IRoutingService.class);
		l.add(IStaticFlowEntryPusherService.class);
		return l;
		//return null;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		// TODO Auto-generated method stub
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		logger = LoggerFactory.getLogger(CCNTopology.class);
		restApi = context.getServiceImpl(IRestApiService.class);
		deviceManager = context.getServiceImpl(IDeviceService.class);
		staticFlowPusher = context
				.getServiceImpl(IStaticFlowEntryPusherService.class);
		routingService = context.getServiceImpl(IRoutingService.class);
	}

	@Override
	public void startUp(FloodlightModuleContext context)
			throws FloodlightModuleException {
		// TODO Auto-generated method stub
		restApi.addRestletRoutable(new CCNTopologyWebRoutable());
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
	}
	
	@Override
	public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
			BasePacket bpkt = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
			PacketParser parsed = new PacketParser(bpkt);
			
			//For now I am supporting both TCP and UDP packets
			//The following step is only to support TCP
			
			if(parsed.isTCP() || parsed.isUDP()){
				logger.debug("**Received CCN packet for Topology Discovery**" + "Src " 
								+ parsed.getSourceIP() + ":" + parsed.getSourcePort()
								+ "Dst " + parsed.getDestinationIP()+ ":"
								+ parsed.getDestinationPort() + "From " + sw.getStringId());
				
				String src_ip = parsed.getSourceIP(); 
				short src_port = parsed.getSourcePort();
				String src_data = parsed.getInnerPacketData();
				
				logger.debug("The packet Data:");
				logger.debug(src_data);
				// the packet from host containing/not is CCN then write flows
				// isHost is the Host containing CCND
				// We are sending complete packetdata with Packet_In
				// Check for CCN is up and add CCN node and flow action=Controller
				
				if (src_data == "CCN is up" && src_port == 9696/*isHost(src_ip)*/) {
					IOFSwitch source = floodlightProvider.getSwitches().get(
							getSourceSwitch(Ethernet.toMACAddress(HOST_IP1)));
					
					//Add this node to CCN array list
					CCNNode node1 = new CCNNode(src_ip,"MAC_ADDRESS",src_port);
					ccnnode.add(node1);
					
					//This function adds flow to the Host for the packets
					//NEXT STEP
					//this.writeHostFlow(source, cntx, src_ip,
					//		parsed.getDestinationPort(), parsed.getDestinationIP());
					
					OFPacketIn pi = (OFPacketIn) msg;
					//Based on my incoming message, we send a packet out with action
					
					logger.debug("Writing Flow");
					OFPacketOut po = (OFPacketOut) floodlightProvider.
							getOFMessageFactory().getMessage(OFType.PACKET_OUT);
					
					po.setBufferId(pi.getBufferId()).setInPort(pi.getInPort());
					//Send packets to controllers of this type
					OFActionOutput action_flow = new OFActionOutput()
								.setPort((short) OFPort.OFPP_LOCAL.getValue());
					po.setActions(Collections.singletonList((OFAction) action_flow));
					po.setActionsLength((short) OFActionOutput.MINIMUM_LENGTH);
					
					//If the incoming packet comes with some data then send back the data
					if (pi.getBufferId() == 0xffffffff) {
						byte [] packetData = pi.getPacketData();
						po.setLength(U16.t(OFPacketOut.MINIMUM_LENGTH
								+ po.getActionsLength() + packetData.length));
						po.setPacketData(packetData);
					} else {
						po.setLength(U16.t(OFPacketOut.MINIMUM_LENGTH
								+ po.getActionsLength()));
					}
					//Or set your own packetData
					//po.setPacketData(packetData);
					
					try {
						sw.write(po, cntx);
						sw.flush();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return Command.CONTINUE;
	}
	
	//I want to get HOST_IP from the REST API URL call
	private boolean isHost(String ip) {
		if (ip.equalsIgnoreCase(HOST_IP1))
			return true;
		else
			return false;
	}
	
	/* Algorithm to add flow to the HOST for CCND packet
	 * Generally the interest packets are targeted to other 
	 * FIB entry CCN systems, add a flow for it.
	 * MODIFY IF NECESSARY
	 */
	private void writeHostFlow(IOFSwitch sw, FloodlightContext cntx, String host_ip, short transport_dst,
			String destHost_ip){
		assert (sw != null);
		logger.debug("Writing Flow to CCN node");
		
		short host_port = 9696; //The port for Host ICN Manager:standard
		OFMatch match1 = new OFMatch();
		match1.setNetworkProtocol(IPv4.PROTOCOL_TCP);
		match1.setDataLayerType(Ethernet.TYPE_IPv4);
		match1.setNetworkSource(IPv4.toIPv4Address(host_ip));
		match1.setTransportDestination(transport_dst);
		match1.setWildcards(~(OFMatch.OFPFW_NW_PROTO | OFMatch.OFPFW_DL_TYPE 
				| OFMatch.OFPFW_NW_SRC_MASK | OFMatch.OFPFW_TP_DST));
		
		List<OFAction> actions1 = new ArrayList<OFAction>();
		actions1.add(new OFActionNetworkLayerDestination(IPv4
				.toIPv4Address(HOST_IP1)));
		actions1.add(new OFActionDataLayerDestination(Ethernet
				.toMACAddress(HOST_MAC)));
		actions1.add(new OFActionTransportLayerDestination(host_port));
		//This is similar to output:2 (NEED TO BE CHANGED)
		//actions1.add(new OFActionOutput().setPort((short) 2));
		actions1.add(new OFActionOutput().setPort((short) OFPort.OFPP_LOCAL.getValue()));
		
		OFFlowMod mod1 = (OFFlowMod) floodlightProvider.getOFMessageFactory()
				.getMessage(OFType.FLOW_MOD);
		mod1.setMatch(match1)
						.setCookie(4321)
						.setCommand(OFFlowMod.OFPFC_ADD)
						.setIdleTimeout((short) 0)
						.setHardTimeout((short) 0)
						.setPriority((short) 32768)
						.setBufferId(OFPacketOut.BUFFER_ID_NONE)
						.setFlags((short) (1 << 0))
						.setLength(
								(short) (OFFlowMod.MINIMUM_LENGTH
											+ OFActionOutput.MINIMUM_LENGTH
											+ OFActionNetworkLayerDestination.MINIMUM_LENGTH
											+ OFActionTransportLayerDestination.MINIMUM_LENGTH));
		
		//If more than one flows are there in this then:
		List<OFMessage> msglist = new ArrayList<OFMessage>();
		msglist.add(mod1);
		//msglist.add(mod2);
		
		try {
			sw.write(msglist, cntx);
			sw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	//given MAC, gives DPID of switch it is connected to: 
	private long getSourceSwitch(byte[] mac) {
		Long ml = Ethernet.toLong(mac);
		//Enter the details of the switch, this is made null for now
		Device device = (Device) deviceManager.findDevice(ml, null, null, null, null);
		if (device != null) {
			SwitchPort[] attachmentPoints = device.getAttachmentPoints();
			return attachmentPoints[0].getSwitchDPID();
		} else 
			return 0;
	}

}
