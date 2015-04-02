# ICN-over-SDN
Intern project to implement content-based routing over software defined network
Responses to: Anu.Mercian@asu.edu

Information-Centric Networking (ICN) uses content names to locate data and routing is based on content name. In this 
software build, we use CCNx-0.8.0 to enable content-based routing over an IP network, and design host managers to 
communicate with SDN-based Controller such as Floodlight Controller version 0.85. This is part of my Intern Project at 
Futurewei Technologies. The overall structure is included here to understand the design although all working parts are 
not included to protect copyright. 

Major Project aspects:
1. Controller modules added to Floodlight that work as Extensions or software upgrades describing the usefulness of SDN.
2. Host Modules for communication between CCNx and Floodlight modules. These host modules are easy to use and do not 
require any major software change. It uses existing modules to communicate. 

