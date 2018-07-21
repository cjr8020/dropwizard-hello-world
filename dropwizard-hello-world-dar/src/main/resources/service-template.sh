#!/bin/bash
#
# chkconfig: 2345 90 10
# description:  dropwizard-hello-world daemon
# Timestamp to make the file unique: ${maven.build.timestamp}

. /etc/init.d/functions

# You will probably want to change only the three lines.
PROG=$(basename "$0" .sh)
BASEDIR="/apps/pojoapps/${PROG}"

JVM_OPTS="{{JVM_OPTS}}"
CONFIG_OPTS="{{CONFIG_OPTS}}"
ADMIN_PORT="{{ADMIN_PORT}}"
HEALTHCHECK_URL="http://localhost:${ADMIN_PORT}{{HEALTHCHECK_URL}}"
EXPECTED_RESULT='{{EXPECTED_RESULT}}'

# If you are using jasypt encryption for master password
if [ "{{MASTER_PASSWORD_ENV_NAME}}" != "void" ]; then
    export {{MASTER_PASSWORD_ENV_NAME}}={{MASTER_PASSWORD_ENV_VALUE}}
fi

# If you are using encrypted-config-value.key
if [ "{{ENCRYPTED_CONFIG_VALUE_KEY}}" != "void" ]; then
    mkdir -p "${BASEDIR}/var/conf"
    echo -n "{{ENCRYPTED_CONFIG_VALUE_KEY}}" > "${BASEDIR}/var/conf/encrypted-config-value.key"
    chmod -Rf ug=rwX,o= "${BASEDIR}/var"
fi


run_user="pojouser"
run_cmd="java ${JVM_OPTS} -jar ${PROG}.jar ${CONFIG_OPTS}"
PIDFILE="${BASEDIR}/${PROG}.pid"
return_value=0

# Use this to adjust retry and timeout of http request
curl_args=(-k --connect-timeout 5 --max-time 10 --retry 5 --retry-delay 0 --retry-max-time 240 --silent)

# global vars used by status function
has_pidfile=0
stale_pidfile=0
listener_active=0

mismatched_process=0
health_check_failed=0
run_state="unknown"

function start () {
    echo "Checking status..."
    status
    if [[ "${run_state}" = "running" ]];then
      echo "${PROG} is already running."
      failure ; echo
      return_value=1
      return
    fi
    touch "${PIDFILE}"
    chown -f ${run_user} "${PIDFILE}"
    chmod -f ug=rwX,o= "${PIDFILE}"

    if [[ $EUID -ne 0 ]]; then
        cd "${BASEDIR}" || return
        echo "Starting ${PROG} ..."
        ${run_cmd} > /dev/null &
        echo $! > "${PIDFILE}"
    else
        runuser ${run_user} -s /bin/bash -c "cd ${BASEDIR}
        ${run_cmd} > /dev/null &
        echo \"Starting ${PROG} ...\"
        echo \$! > ${PIDFILE}"
    fi
    check_pidfile
    if [[ "${has_pidfile}" -eq 1 ]]; then
      check_stale_pidfile
      if [[ "${stale_pidfile}" -eq 1 ]]; then
        echo
        echo "Process did not start properly."
        failure; echo
        return_value=1
        return
      fi
    else
      echo
      echo "No pid file was created."
      failure; echo
      return_value=1
      return
    fi
    echo -n "Waiting for server to start ..."
    timeout 30 bash -c "until curl -k -output /dev/null --silent --head --fail ${HEALTHCHECK_URL}; do echo -n .; sleep 0.5; done"
    if [[ $? -ne 0 ]]; then
        return_value=$?
        echo "Timed out."
        failure
        return_value=1
        return
    else
      echo "Started."
      status
      if [[ "${run_state}" == "running" ]]
        then
           return_value=0
        else
           return_value=1
        fi
    fi
}

function stop () {
    echo "Checking status..."
    status
    if [[ "${run_state}" == "stopped" ]]; then
        echo "${PROG} is already stopped."
        return_value=0
    else
      if [[ "${run_state}" == "running" ]]; then
        echo $"Stopping ${PROG} ..."
        killproc -p "${PIDFILE}"
        return_value=$?
        echo
        if [[ "$return_value" -eq 0 ]] ; then
          rm -f "${PIDFILE}"
        fi
        status
        if [[ "${run_state}" == "stopped" ]]; then
          return_value=0
        else
          return_value=1
        fi
      else
        echo "${PROG} run state is ${run_state}. "
        return_value=1
      fi
    fi
}

function check_pidfile () {
  # Test 1: does the pid file exist?
  if [[ ! -f "${PIDFILE}" ]]; then
    has_pidfile=0
    return
  else
    has_pidfile=1
  fi
}

function check_stale_pidfile () {
  # Test 2: Is the process the pid file references active?
  pid=$(cat "${PIDFILE}")
  if ps -p"${pid}" -o "pid=" > /dev/null 2>&1; then
    stale_pidfile=0
  else
    stale_pidfile=1
  fi
}

function check_listener_active () {
  # Test 3: Does the admin port have a listener on it?
  lsof_pid=$(lsof -i :${ADMIN_PORT} -t)
  if [[ "${lsof_pid}" = "" ]]; then
    listener_active=0
  else
    listener_active=1
  fi
}

function check_mismatched_process () {
  # Test 4: Does the listening PID match the pid file reference?
  if [[ "${lsof_pid}" -eq "${pid}" ]]; then
    echo " Listening process ${lsof_pid} matches PID file process ${pid}."
    mismatched_process=0
  else
    echo " Listening process ${lsof_pid} is different from PID file process ${pid}."
    mismatched_process=1
  fi
}

function check_health () {
  # Test 5: Does the listener return the expected value?
  check_result=$(curl "${curl_args[@]}" "${HEALTHCHECK_URL}")
  curl_rc=$?
  if [[ ${curl_rc} -ne 0 ]]; then
    echo " WARNING: Health check read had a non-zero exit code: ${curl_rc}"
    health_check_failed=1
  else
    if [[ "${check_result//[$'( \t\r\n)+']/}" =~ ${EXPECTED_RESULT//[$'( \t\r\n)+']/} ]]; then

      health_check_failed=0
    else
        health_check_failed=1
    fi
  fi
}


function status () {
  check_pidfile
  if [[ "${has_pidfile}" -eq 0 ]]; then
    echo " No pid File found."
  else
    echo " PID file is present."
    check_stale_pidfile
    if [[ "${stale_pidfile}" -eq 0 ]]; then
      echo " Process ${pid} is running."
    else
      echo " Process ${pid} is not running."
    fi
  fi
  check_listener_active
  if [[ "${listener_active}" -eq 0 ]]; then
    echo " There is no process listening on ${ADMIN_PORT}."
  else
    echo " Process ${lsof_pid} is listening on ${ADMIN_PORT}."
    if [[ ${has_pidfile} -eq 1 ]] && [[ ${stale_pidfile} -eq 0 ]]; then
      check_mismatched_process
      if [[ "${mismatched_process}" -eq 0 ]]; then
        check_health
        if [[ "${health_check_failed}" -eq 0 ]]; then
          echo " Health check matched expected value."
        else
          echo " Health check does not match expected value."
        fi
      else
        echo " Listening process ${lsof_pid} is different from PID file process ${pid}."
      fi
    fi
  fi


  statemap="${has_pidfile}${stale_pidfile}${listener_active}${health_check_failed}${mismatched_process}"

  if [[ ${statemap} = "10100" ]]; then
    run_state="running"
    return_value=0
  else
    if [[ ${statemap} = "00000" ]]; then
      run_state="stopped"
      return_value=0
    else
      run_state="failed"
      return_value=1
    fi
  fi
  echo "${PROG} is now ${run_state}."
  return
}




restart () {
    stop
    start
}


# See how we were called.
case "$1" in
  start)
    start
    ;;
  stop)
    stop
    ;;
  status)
    status
    ;;
  restart)
    restart
    ;;
  *)
    echo $"Usage: $0 {start|stop}"
    return_value=2
    ;;
esac

if [[ ${return_value} == 0 ]]; then
  success; echo
else
  failure; echo
fi
exit $return_value
