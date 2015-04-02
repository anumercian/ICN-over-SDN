#!/usr/bin/python

############################################################################
#Author	    : Anu Mercian
#Descrption : This module will detect the presence of CCND (CCN Daemon) from 
#	      syslog or environment and send message to controller
#Use	    : python vm_ccn.py
############################################################################

import socket 
import sys
import subprocess

#This can be modified according to the current system settings
HOST, PORT = "127.0.0.1", 6596

#FUNCTION TO DETECT THE PRESENCE OF CCND 
# Designed a perl script to see if ccnd process is running
#
# IMPORT the output of perl script

var = "ccnd" #The process name
#status = subprocess.call(["/usr/bin/perl", "./ps_running", var])
#subprocess.call returns only return code
#subprocess.check_output gives the exact output with enter (\n)
status = subprocess.check_output(["/usr/bin/perl", "/home/conalab/ps_running.pl", var])

if status == "Process is running.\n":
	data = "CCN is up"
else:
	data = "CCN not up"

print data
#creating socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

try:
	#connecting to server and send data
	sock.connect((HOST,PORT))
	sock.sendall(data + "\n")

	#In order to receive data from server and shut down
	received = sock.recv(1024)
	print received
finally:
	sock.close()
	
