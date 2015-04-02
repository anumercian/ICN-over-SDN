#!/bin/sh

ovs-ofctl add-flow br0 "in_port=LOCAL, table=1, idle_timeout=60, ip, hard_timeout=60, vlan_tci=0x0000, dl_src=78:e7:d1:7b:84:94, dl_dst=78:e7:d1:7b:84:94, nw_proto=6, nw_dst=192.168.1.149, nw_src=192.168.1.149, tp_src=9696, tp_dst=9698, actions=drop"
ovs-ofctl dump-flows br0
