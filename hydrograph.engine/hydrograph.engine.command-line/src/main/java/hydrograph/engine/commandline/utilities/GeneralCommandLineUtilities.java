/*******************************************************************************
 * Copyright 2017 Capital One Services, LLC and Bitwise, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 *******************************************************************************/
/**
 *
 */
package hydrograph.engine.commandline.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class GeneralCommandLineUtilities.
 *
 * @author Bitwise
 *
 */
public class GeneralCommandLineUtilities {

    private static final Logger LOG = LoggerFactory.getLogger(GeneralCommandLineUtilities.class);
    private GeneralCommandLineUtilities() {

    }

    public static Object loadAndInitClass(String className) throws IllegalAccessException, InstantiationException, ClassNotFoundException {

        @SuppressWarnings("rawtypes")
        Class loadedClass;
        loadedClass = Class.forName(className);
        return loadedClass.newInstance();
    }

    /**
     * To check if a option is provided in commandline arguments Please check
     * tests for usage.
     *
     * @param args
     * @param option
     * @return an string array containing all values found for given option
     */
    public static boolean IsArgOptionPresent(String[] args, String option) {
        String optionChar = "-";
        String optionString = optionChar + option;

        for (String arg : args) {
            if (arg.equalsIgnoreCase(optionString))
                return true;
        }
        return false;
    }

    public static void printUsage() {

        LOG.info("This utility can have following options:");
        LOG.info("  -xmlpath \t\t Required, single, to specify main/parent xml file to execute");
        LOG.info("  -libjars \t\t Optional, single, comma seperated paths of jar files to be included in classpath");
        LOG.info("  -debugxmlpath \t Optional, single, location of the debug file");
        LOG.info("  -loglevel \t Optional, single, set log level");
        LOG.info("  -jobid \t\t Optional (Required when -debugxmlpath option is specified), single, the job id");
        LOG.info(
                "  -basepath \t\t Optional (Required when -debugxmlpath option is specified), single, the base path where the debug files will be written");
        LOG.info("  -param \t\t Optional, multiple, command line parameters to be used for substitution.");
        LOG.info(
                "  -param <name>=<value>  for each parameter. Use double quotes if value has spaces. Please see example below.");
        LOG.info(
                "  -paramfiles \t\t Optional, multiple, comma seperated paths of parameter files having name vale pair for parameters to be substituted");
        LOG.info("  -help \t\t to print this usage and exit");
        LOG.info("\n");
        LOG.info("Example invocations could be:");
        LOG.info("\t $0 -xmlpath /user/jobs/myjob.xml -param param1=value1 -param param2=\"value2 valueafterspace\" ");
        LOG.info("or");
        LOG.info(
                "\t $0 -xmlpath /user/jobs/myjob.xml -libjars /mypath/my1.jar,/mypath/my2.jar \n\t\t -paramfiles /mypath/paramfile1,/mypath/paramfile2");
        LOG.info("or");
        LOG.info(
                "\t $0 -xmlpath /user/jobs/myjob.xml -debugxmlpath /user/jobs/debug/mydebug.xml -jobid myjob \n \t\t -basepath /tmp/debug/ -libjars /mypath/my1.jar,/mypath/my2.jar -paramfiles /mypath/paramfile1,/mypath/paramfile2");
    }
}
