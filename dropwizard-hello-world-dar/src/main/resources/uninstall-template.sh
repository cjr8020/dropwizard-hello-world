#!/usr/bin/bash
# This script is only meant to be run by xl-deploy at application un-installation time
# The script must be unique from all other versions so it will not be "optimzed" from the deployment
# Timestamp to make the file unique: ${maven.build.timestamp}
apppath="$1"
appname="$(basename "$apppath")"
parentpath="$(dirname "$apppath")"
if [[ "$parentpath" != "/opt/apps/pojoapps" ]]; then
        echo Parent directory of the application must be /opt/apps/pojoapps but was "$parentpath" 1>&2
        exit 1
fi
scriptpath="${apppath}/${appname}.sh"

set -e

echo chmod -Rf ug=rwX,o= "$apppath"
chmod -Rf ug=rwX,o= "$apppath"

echo "$scriptpath" stop
"$scriptpath" stop

echo rm -f "$apppath/application.yml"
rm -f "$apppath/application.yml"

echo rm -rf "$apppath/var"
rm -rf "$apppath/var"
