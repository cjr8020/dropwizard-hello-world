#!/usr/bin/bash
# This script is only meant to be run by xl-deploy at application installation time
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
if [[ -z "$scriptpath" || ! -e "$scriptpath" ]]; then
        echo The service startup script "$scriptpath" must exist 1>&2
        exit 1
fi
configpath="${apppath}/${appname}.yml"
if [[ -z "$configpath" || ! -e "$configpath" ]]; then
        echo The service config "$configpath" must exist 1>&2
        exit 1
fi
set -e

echo Create service startup script with machine specific configuration
cat "$scriptpath" | sed "s/!!RUN_NODE!!/$(uname -n)/g" > "${scriptpath}.tmp"
mv -f "${scriptpath}.tmp" "${scriptpath}"

echo Create configuration yml with machine specific configuration
cat "$configpath" | sed "s/!!RUN_NODE!!/$(uname -n)/g" > "${configpath}.tmp"
mv -f "${configpath}.tmp" "${configpath}"

echo chmod -Rf ug=rwX,o= "$apppath"
chmod -Rf ug=rwX,o= "$apppath"

echo chmod -f ug+x "$scriptpath"
chmod -f ug+x "$scriptpath"

echo "$scriptpath" start
"$scriptpath" start
