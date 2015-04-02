#!/usr/bin/python

#####################################################################
##################### HOST CCN Service MANAGER ######################
# This agent can be run without any argument			    #
# It is assumed that both CCND and CCN Chat are running before 	    #
#    this is agent is called, there identifying CCND process and    #
#    CCN Chat are included together in this agent 		    #
#####################################################################

import socket
import sys
import subprocess

#Controller Address
CONTROLLER, PORT = "192.168.1.140", 6633

#The local host
HOST, LOCAL_PORT = "192.168.1.149", 9696

#Application process name
var = "ccnd"

status = subprocess.check_output(["/usr/bin/perl", "/home/cona/ps_running.pl", var])


if status == "Process is running.\n":
        data = "CCN is up"
else:
        data = "CCN not up"

print "The message sent is: ",data

#creating UDP socket as 4-way handshake not required
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

try:
        #bind this agent to a particular port
        sock.bind((HOST,LOCAL_PORT))
        #connecting to server and send data
        sock.sendto(data, (CONTROLLER,PORT))

#Closes immediately, can also make this Ctrl+d to terminate
#Another option is to use "finally"
except:
        sock.close()

#Application process name
app_var = "ccnchat"

app_status = subprocess.check_output(["/usr/bin/perl", "/home/cona/ps_running.pl", var])

chat_name = subprocess.check_output(["/usr/bin/perl", "/home/cona/read_chat_name.pl"])

print "The Chat application name is: ", chat_name

if app_status == "Process is running.\n":
        app_data = "CCN Chat is up"
else:
        app_data = "CCN Chat not up"

print app_data

try:
        #bind this agent to a particular port
        sock.bind((HOST,LOCAL_PORT))
        #connecting to server and send data
        sock.sendto(app_data, (CONTROLLER,PORT))
	sock.sendto(chat_name, (CONTROLLER,PORT))
        #receive any data from the controller
        pkt_receive, addr = sock.recvfrom(1024)
        print "received message: ", pkt_receive
	print "from: ", addr

except:

	sock.close()

command_ccndc = ["ccndc add " + "ccnx:/" + chat_name + " udp " + pkt_receive]

print command_ccndc

subprocess.call(command_ccndc)
