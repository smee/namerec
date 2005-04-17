#! /usr/lcal/bin/perl -w
use Time::localtime;
$day = localtime(time())->yday;  
system("rm *-$day.txt");

system("java Recognizer -ik wissenAkt.txt -rl pats2.txt -rp patPers.txt -pt 0.09 -oi items-$day.txt -om maybes-$day.txt -og NEs-$day.txt -or contexts-$day.txt > run-$day.log");

exit(1);