#!/usr/bin/python

controller = "http://192.168.1.140:8080"
path = "/wm/ccntopology/"

import socket
import sys
import subprocess
import httplib2

#Getting IP address of host
def get_ip(ifname):
	s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
	return socket.inet_ntoa(fcntl.ioctl(s.fileno(),0x8915, 
		struct.pack('256s', ifname[:15]))[20:24])

def check_ccnd(proc_name):
	status = subprocess.check_output(["/usr/bin/perl", "./ps_running", proc_name])

	if status == "Process is running":
		flag = 1

	else:
		flag = 0

	return flag

def update_controller(ccnd_flag):
	host_ip = get_ip('br0') #This can also be eth0, depending on whether it in OVS or not

	if (ccnd_flag == 1):
		print "CCN node IP {} is up: Inform Controller".format(host_ip)
		url = controller + path + 'add,' + host_ip + '/json'
		response,content = httplib2.Http().request(url)
		print url
		print content
	else if (ccnd_flag == 0):
		print "CCN Node closed: Update Controller"
		url = controller + path + 'del,' + host_ip + '/json'
		response,content = httplib2.Http().request(url)
		print url
		print content


if __init__=="__main__":
	check_flag = check_ccnd(ccnd) 
	update_controller(check_flag)



