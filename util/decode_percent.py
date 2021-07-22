#!/usr/bin/python -O

import urllib
import sys

while True:
	line = sys.stdin.readline()
	if not line:
		break
	print urllib.unquote(line.rstrip())

