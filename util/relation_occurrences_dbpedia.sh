#!/bin/bash

awk '{++c[$2]} END { for (k in c) print k " " c[k] }' "$1" | sort | tr -d '<>'
