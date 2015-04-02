#!/usr/bin/perl

use strict;
use warnings;

system("ps aux | grep ccnchat > /tmp/output.txt 2>&1");

open FH,"</tmp/output.txt";

read_text(*FH);

sub read_text
{
	local *FH = shift;
	my @lines;
	@lines = <FH>;
#	print $lines[0];
	for my $line (@lines) {
		if ($line =~ /ccnx:.+\n$/ and $line =~ /bin\/sh/) {
			#print $line;
			if ($line =~ /ccnx:\/(\w+)\n$/) {
				print"$1\n";
			}
		}
	}

}
#system("rm /tmp/output.txt");
