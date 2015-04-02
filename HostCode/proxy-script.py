#!/usr/bin/python

controller = "http://12.133.183.82:8080"
path = "/wm/cachemanager/"

import re
re_get = re.compile("GET\s*(.*)\r\n")
re_host = re.compile("Host:\s*(.*)\r\n")

import httplib2
import socket
import urllib
import fcntl
import struct

def get_ip(ifname):
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    return socket.inet_ntoa(fcntl.ioctl(
        s.fileno(),
        0x8915,  # SIOCGIFADDR
        struct.pack('256s', ifname[:15])
    )[20:24])

def update_controller(serverip,filename):
    #filename = filename[1:]
    #filename = filename[:-9]
    print "update_controller sip {} file {}".format(serverip,filename)
    url = controller + path + 'save,' + serverip + ',' + filename + '/json'
    response,content = httplib2.Http().request(url)
    print url
    print content

def lookup_controller(filename,host_ip):
    my_ip = get_ip('eth0')
    print "lookup_controller file {}".format(filename)
    url = controller + path + 'retreive,' + filename + ',' + host_ip + ',' + my_ip + '/json'
    response, content = httplib2.Http().request(url)
    print url
    return content

def lookup(name,host_ip):
    print "lookup {}".format(name)
    ip = lookup_controller(name,host_ip)
    if ip == 'none':
        return ip + ":80"
    else:
        return ip + ":8080"

def proxy(data):
    print "Proxy called"
    filename = re_get.findall(data)[0]
    hostname = re_host.findall(data)[0]
    totalpath = hostname + filename[:len(filename) - 9]
    totalpath = urllib.quote_plus(totalpath)
    host_ip = socket.gethostbyname(hostname)
    host = lookup(totalpath,host_ip)
    if host != 'none:80':
        print "Controller returned {}".format(host)
        return {"remote": host}
    else:
        host_ip = socket.gethostbyname(hostname)
        update_controller(host_ip, totalpath)
        server_uri = str(host_ip)+":80"
        print "Going to " + server_uri
        return {"remote": server_uri}
