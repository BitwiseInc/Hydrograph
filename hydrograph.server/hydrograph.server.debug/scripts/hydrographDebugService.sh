#!/bin/bash

#********************************************************************************
# * Copyright 2017 Capital One Services, LLC and Bitwise, Inc.
# * Licensed under the Apache License, Version 2.0 (the "License");
# * you may not use this file except in compliance with the License.
# * You may obtain a copy of the License at
# * http://www.apache.org/licenses/LICENSE-2.0
# * Unless required by applicable law or agreed to in writing, software
# * distributed under the License is distributed on an "AS IS" BASIS,
# * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# * See the License for the specific language governing permissions and
# * limitations under the License.
# ******************************************************************************


#IMPORTANT: Please update the following paths for your environment for successful
#           execution of the script.
SERVER_JAR_PATH=./bin
LIB_PATH=./libs
LOG_PATH=./log
LIBJARS=""
SERVER_JAR="elt-debug-0.1.1.jar"
SERVER_NAME=`uname -n`

export LIBPATHS=""
export LIBJARS=""
export MAINJAR=""

SERVICE_ID="NULL"
ts="`date +%Y%m%d%H%M%S`"
FILE_NAME="debugService$ts.log";
PID_FILE="debugService.pid";
PID=-1
SERVICE_START_IND=0



#*********function declarations************
exportMailConfigurations(){
    mailConfigFile="./config/mail.properties"
    if [ -f "$mailConfigFile" ]
    then
        . $mailConfigFile
    else
        echo "$mailConfigFile file not found"
    fi
}



function monitor_service() {
    while [ 1 ]
    do
        check_status "SUPRESS_LOGGING"
        if  [ $SERVICE_START_IND -eq 0 ];
        then
            echo "View Data service has stopped on $SERVER_NAME" | mail -v -s "View Data Service has stopped" \
				-S smtp=$mail_smtp_host \
				-S from="$mail_smtp_from" \
				"$mail_smtp_to"
            rc=$?
            if [ $rc -ne 0 ]; then
                echo "Failed while sending email with return code: "$rc
            fi
            break
        fi
        sleep 30s
    done
}

appendLibJars(){

  LIB_FOLDER=$1

  for JARFILE in `ls -1 ${LIB_FOLDER}/*.jar`
        do
                                if [ -n "$LIBJARS" ]; then
                                                LIBJARS=${LIBJARS},$JARFILE
                                else
                                                LIBJARS=$JARFILE
                                fi
                done
 }


setMainJar(){

MAINJAR=$SERVER_JAR_PATH/$SERVER_JAR

#if main jar is not found then raise error
if [ ! -f "$MAINJAR" ]; then
   echo "ERROR:Main executable jar is not found at path ${SERVER_JAR_PATH}. Exiting."
   exit 1
fi
}


check_status() {
SUPRESS=$1
SERVICE_START_IND=0
if [ -f ${LOG_PATH}/${PID_FILE} ]; then
        PID=`cat ${LOG_PATH}/${PID_FILE}`;
        if $(ps -ef | awk '{ print $2 }' | grep -w ${PID} > /dev/null)
        then
          if [ "$SUPRESS" == "" ]; then
          echo "Hydrograph debug service is running as PID=$PID"
          fi
          SERVICE_START_IND=1
          return 1
        else
          if [ "$SUPRESS" == "" ]; then
          echo "Hydrograph debug service not started"
          fi
        fi
fi
return 0
}

function print_help {
                echo "============================"
        echo "Hydrograph UI Debug Service"
                echo "============================"
        echo "Starts the Hydrograph Debug Service to enable debugging on development environments."
        echo "*Only one instance of the service should be present on the server"
        echo ""
        echo "Usage:"
        echo "hydrographDebugService.sh [status|start|stop|restart]"
        echo ""
        echo "To view the logs, please check out $LOG_PATH"
}

start_service () {

                setMainJar

                echo "Main jar has been set to $MAINJAR"

                #set jars automatically. User need not specify it
                appendLibJars $LIB_PATH

                if [ -n "$LIBPATHS" ]; then
                   echo "Lib paths are specified. Setting libsjars and hadoop classpath accordingly."

                   LIBJARS=${LIBJARS},$LIBPATHS


                fi
                   COLONSEPERATED="`echo "$LIBJARS"|tr ',' ':'`"

        SUPRESS=$1
        if [ ! "$SUPRESS" == "" ]; then
			check_status $SUPRESS
		fi

        if [ $SERVICE_START_IND -eq 0 ]; then
                echo "Starting Debug Service"
                java -cp config:${LIB_PATH}/*:"$MAINJAR" hydrograph.server.service.HydrographService > $LOG_PATH/$FILE_NAME &
                SERVICE_ID=$!
                echo $SERVICE_ID > ${LOG_PATH}/${PID_FILE}
                echo "Service $SERVICE_ID started successfully!"
        else
                echo "Service $PID already running!"
        fi
        }

stop_service() {
        SUPRESS=$1
        if ! check_status $SUPRESS
        then
                echo "Trying to stop service..."
                if [ $PID -eq -1 ]; then
                        echo "No previously running service was found!"
                else
                        kill $PID
                        if [ $? -eq 0 ]; then
                                echo "Service $PID stopped successfully!"
                                return 0
                        fi
                fi
        fi
        return 1
}


#*********Execution starts from here************




if [ $# -eq 0 ]; then
        print_help
        exit 1
else
		exportMailConfigurations
        case "$1" in
        stop) stop_service
        ;;
 		start)  start_service $1
				monitor_service
       ;;
        restart)
                if ! check_status "SUPRESS"
                then
                  stop_service
                fi
                start_service
				monitor_service
        ;;
        status) check_status
        ;;
        *) print_help
        exit 1;;
        esac
fi
