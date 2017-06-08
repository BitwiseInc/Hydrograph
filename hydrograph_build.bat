@ECHO OFF

REM SET ENVIRONMENT VARIABLES
SET JAVA_VERSION=-1
SET GRADLE_VERSION=-1
SET MAVEN_VERSION=-1

REM HYDROGRAPH COMPONENT DIRECTORIES
SET ENGN_PRJ_DIR=hydrograph.engine
SET EXEC_PRJ_DIR=hydrograph.server\hydrograph.server.execution.tracking
SET DBG_PRJ_DIR=hydrograph.server\hydrograph.server.debug
SET UI_PRJ_DIR=hydrograph.ui

REM DEFAULT LOG FILE LOCATIONS FOR BUILD SCRIPTS
SET ERR_LOG=..\logs\error.log
SET ENGN_LOG=..\logs\engine.log
SET EXEC_LOG=..\..\logs\exec_track_service.log
SET DBG_LOG=..\..\logs\debug_service.log
SET UI_LOG=..\logs\ui.log
SET COPYLIBS_LOG=.\logs\copyLibs.log

mkdir .\logs

REM MINIMUM TOOL VERSIONS
REM VERSION NUMBERS SHOULD BE ONE LESS THAN REQURIED
SET MIN_JAVA_VER=7
SET MIN_GRADLE_VER=2
SET MIN_MVN_VER=2

SET ERROR_MSG=

CALL :javaVersionCheck
CALL :gradleVersionCheck
CALL :mavenVersionCheck
CALL :checkRequiredDirectories
CALL :resolveDeps
CALL :buildEngine
CALL :buildDebugService
CALL :buildExecutionTrackingService
CALL :buildUI

ECHO The Hydrograph application can be found in %UI_PRJ_DIR%\hydrograph.ui.product\target\products\hydrograph.ui.perspective.ID\win32\win32\x86_64
explorer %UI_PRJ_DIR%\hydrograph.ui.product\target\products\hydrograph.ui.perspective.ID\win32\win32\x86_64

echo Done!
goto :eof


REM CHECK JAVA VERSION
:javaVersionCheck
FOR /F "tokens=2 delims=." %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
		
        SET JAVA_VERSION=%%g
)


IF /I "%JAVA_VERSION%" LEQ "%MIN_JAVA_VER%" (
	SET ERROR_MSG=Java should be installed and Java version should be 1.8 or higher!
	CALL :printError 
)
EXIT /B %ERRORLEVEL%


REM CHECK GRADLE VERSION
:gradleVersionCheck
FOR /F "tokens=2 delims=. " %%a in ('gradle -version 2^>^&1 ^| findstr /i "^Gradle"') do (
        SET GRADLE_VERSION=%%a
)

IF /I "%GRADLE_VERSION%" LEQ "%MIN_GRADLE_VER%" (
	SET ERROR_MSG=Gradle should be installed and Gradle version should be 3.0 or higher! %GRADLE_VERSION%
	CALL :printError 
)
EXIT /B %ERRORLEVEL%

REM CHECK MAVEN VERSION
:mavenVersionCheck
FOR /F "tokens=3 delims=. " %%a in ('mvn -version 2^>^&1 ^| findstr "^Apache"') do (
		SET MAVEN_VERSION=%%a
)

IF /I "%MAVEN_VERSION%" LEQ "%MIN_MVN_VER%" (
	SET ERROR_MSG=Maven should be installed and Maven version should be 3.0 or higher!
	CALL :printError 
)
EXIT /B %ERRORLEVEL%

REM ALL CHECKS DONE. NOW, BUILD THE PROJECT

REM CHECK IF ALL THE PROJECT DIRECTORIES ARE PRESENT
:checkRequiredDirectories
IF NOT EXIST "%ENGN_PRJ_DIR%" (
	SET ERROR_MSG=%ENGN_PRJ_DIR% not found in the current directory. Please make sure you have cloned the Hydrograph project!
	CALL :printError 
)

IF NOT EXIST "%EXEC_PRJ_DIR%" (
	SET ERROR_MSG=%EXEC_PRJ_DIR% not found in the current directory. Please make sure you have cloned the Hydrograph project!
	CALL :printError 
)

IF NOT EXIST "%DBG_PRJ_DIR%" (
	SET ERROR_MSG=%DBG_PRJ_DIR% not found in the current directory. Please make sure you have cloned the Hydrograph project!
	CALL :printError 
)

IF NOT EXIST "%UI_PRJ_DIR%" (
	SET ERROR_MSG=%UI_PRJ_DIR% not found in the current directory. Please make sure you have cloned the Hydrograph project!
	CALL :printError 
)

EXIT /B %ERRORLEVEL%

REM RESOLVE ALL THE DEPENDENCIES & DOWNLOAD THEM
:resolveDeps
ECHO Resolving Hydrograph dependencies...
call gradle copyLibs 1> %COPYLIBS_LOG% 2>&1

IF %ERRORLEVEL% NEQ 0 (
SET ERROR_MSG=Error resolving dependencies. Please check %COPYLIBS_LOG% for more details.
CALL :printError 
)

ECHO Hydrograph dependencies resolved successfully!
EXIT /B %ERRORLEVEL%


REM BUILD THE HYDROGRAPH ENGINE FIRST
:buildEngine
ECHO Building Hydrograph Engine...
cd %ENGN_PRJ_DIR%
call gradle install 1> %ENGN_LOG%  2>&1

IF %ERRORLEVEL% NEQ 0 (
SET ERROR_MSG=Error building %ENGN_PRJ_DIR% please check %ENGN_LOG% for more details.
cd ..
CALL :printError 
)

ECHO Hydrograph Engine built successfully!
cd ..
EXIT /B %ERRORLEVEL%

REM BUILD THE HYDROGRAPH DEBUG SERVICE
:buildDebugService
ECHO Building Hydrograph Debug Service...
cd %DBG_PRJ_DIR%
call gradle install 1> %DBG_LOG%  2>&1

IF %ERRORLEVEL% NEQ 0 (
SET ERROR_MSG=Error building %DBG_PRJ_DIR% please check %DBG_LOG% for more details.
cd ..\..
CALL :printError 
)

ECHO Hydrograph Debug Service built successfully!
cd ..\..
EXIT /B %ERRORLEVEL%

REM BUILD THE HYDROGRAPH EXECUTION TRACKING SERVICE
:buildExecutionTrackingService
ECHO Building Hydrograph Execution Tracking Service...
cd %EXEC_PRJ_DIR%

call mvn clean install 1> %EXEC_LOG%  2>&1

IF %ERRORLEVEL% NEQ 0 (
SET ERROR_MSG=Error building %EXEC_PRJ_DIR% please check %EXEC_LOG% for more details.
cd ..\..
CALL :printError 
)

ECHO Hydrograph Execution Tracking Service built successfully!
cd ..\..
EXIT /B %ERRORLEVEL%


REM BUILD THE HYDROGRAPH UI
:buildUI
ECHO Building Hydrograph UI...
cd %UI_PRJ_DIR%

call mvn clean install 1> %UI_LOG% 2>&1

IF %ERRORLEVEL% NEQ 0 (
cd ..
SET ERROR_MSG=Error building %UI_PRJ_DIR% please check %UI_LOG% for more details.
CALL :printError 
)

ECHO Hydrograph UI built successfully!
cd ..
EXIT /B %ERRORLEVEL%

REM PRINT THE ERROR MESSAGE AND EXIT
:printError
ECHO %ERROR_MSG%
EXIT 1


