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
 * limitations under the License.
 *******************************************************************************/

package hydrograph.ui.perspective.dialog;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;

import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * Class for updating hydrograph.ini file at tool startup
 * 
 * @author Buitwise
 *
 */
public class PreStartActivity {
	private static final long REQUIRED_JDK_VERSION = 18000; // i.e. 1.8.0_00
	private static final String JAVAC_EXE = File.separator+"javac.exe";
	private static final String DOSGI_REQUIRED_JAVA_VERSION_PARAMETER_IN_INI_FILE = "-Dosgi.requiredJavaVersion";
	private static final String SLASH_BIN = File.separator+"bin";
	private static final String JAVA_HOME = "JAVA_HOME";
	private static final String HYDROGRAPH_EXE = "hydrograph.exe";
	private static final String HYDROGRAPH_INI = "hydrograph.ini";
	private static final Logger logger = LogFactory.INSTANCE.getLogger(PreStartActivity.class);
	private Shell shell;

	
	public PreStartActivity(Shell shell) {
		logger.debug("Initializing PreStartActivity");
		this.shell=shell;
	}

	/**
	 * Checks whether tool is launched in development mode.
	 * 
	 * @param argumentsMap
	 * @return true if tool is launched in development mode, else returns false
	 */
	@SuppressWarnings("unchecked")
	public static boolean isDevLaunchMode(Map argumentsMap) {
		 logger.debug("Checking whether tool is launched in development mode");
		 for(Entry< Object, String[]> entry:((Map<Object, String[]>)argumentsMap).entrySet()){
				for(String value:entry.getValue()){
					if(StringUtils.contains(value, DOSGI_REQUIRED_JAVA_VERSION_PARAMETER_IN_INI_FILE)){
						return false;
					}
				}
			}
			return true;
		}
	 
	/**
	 * Initiates startup activity for updating hydrograph.ini file  
	 * 
	 */
	public void performPreStartActivity() {
		logger.debug("Initiating pre-start activity");
			if (autoUpdateINIFile()) {
				restartHydrograph();
			} else {
				getUserInputAndUpdate(false);
			}
	}

	private void getUserInputAndUpdate(boolean replaceExistingJDKPath) {
		logger.debug("Launching dialog to get user input for getting JDK path");
		JdkPathDialog dlg = new JdkPathDialog(getShell());
		if (dlg.open() == Window.OK) {
			if(replaceExistingJDKPath){
			updateINIOnJDKUpgrade(dlg.getInputVlue());
			}else{
				updateINIFile(dlg.getInputVlue());
			}
			restartHydrograph();
		} else {
			System.exit(0);
		}

	}

	/**
	 * 
	 * Updates hydrograph.ini file whenever JAVA_HOME is updated.
	 * 
	 */
	public void updateINIOnJDkUpgrade() {
		logger.debug("Initiating auto-update process for updating INI file if JAVA_HOME variable is updated");
		String javaHome = getSystemJavaHomeValue();
		if(isValidJDKPath(javaHome,false) && updateINIOnJDKUpgrade(javaHome)){
			restartHydrograph();
		}
		
	}

	private boolean autoUpdateINIFile() {
		logger.debug("Initiating Auto-update process for updating INI file if JAVA_HOME is set");
		String javaHome = getSystemJavaHomeValue();
		if (isValidJDKPath(javaHome, false)) {
			return updateINIFile(javaHome);
		}
		
		return false;
	}

	private String getSystemJavaHomeValue() {
		String javaHome =System.getenv(JAVA_HOME);
		if (!StringUtils.endsWithIgnoreCase(javaHome, SLASH_BIN))
			javaHome = javaHome + SLASH_BIN;
		return javaHome;
	}

	/**
	 * Checks whether given JDK-PATH is valid or not.
	 * 
	 * @param javaHome
	 * 			input jdk path
	 * @param showPopUp
	 * 			true if user wants to show pop for invalid path.
	 * @return
	 * 		true if input string is valid JDK path.
	 */
	public static boolean isValidJDKPath(String javaHome,boolean showPopUp) {
		try{
			if(javaHome !=null && isValidDirectory(javaHome,showPopUp)){
				if (StringUtils.endsWith(javaHome, SLASH_BIN)) {
					javaHome = StringUtils.replace(javaHome, SLASH_BIN, "");
				}
				if (StringUtils.isNotBlank(javaHome)) {
					StringBuffer jdkPath = new StringBuffer(javaHome);
					jdkPath = jdkPath.delete(0, jdkPath.lastIndexOf("\\") + 1);
					return checkJDKVersion(jdkPath.toString(), showPopUp);
				}
			}
		}
		catch (Exception exception) {
			logger.warn("Exception occurred while validating javaHome path",exception);
		}
		return false;
	}

	private static boolean isValidDirectory(String javaHome, boolean showPopUp) {
		File fileName = new File(javaHome+JAVAC_EXE);
		if(fileName.exists()){
			return true;
		}
		if(showPopUp){
			showInvalidJavaHomeDialogAndExit();
		}
		return false;
	}

	private static boolean checkJDKVersion(String jdkVersion,boolean showPopupAndExit) {
		jdkVersion=StringUtils.removeStart(jdkVersion, "jdk");
		jdkVersion =StringUtils.remove(jdkVersion, ".");
		jdkVersion=StringUtils.remove(jdkVersion, "_");
		long version=Long.parseLong(jdkVersion);
		if(version>=REQUIRED_JDK_VERSION){
			return true; 
		}
		if(showPopupAndExit){
			showInvalidJavaHomeDialogAndExit();
		}
		return false;
	}

	private static void showInvalidJavaHomeDialogAndExit() {
		logger.debug("Showing dialog for invalid JAVA_HOME");
		MessageBox box=new MessageBox(new Shell(),SWT.ICON_ERROR|SWT.CLOSE);
		box.setText("Invalid JAVA_HOME entry");
		box.setMessage("Invalid JAVA_HOME ("+System.getenv(JAVA_HOME)+") entry. Please update JAVA_HOME environment variable and relaunch tool again");
		box.open();
		System.exit(0);
	}

	private void restartHydrograph() {
		logger.info("Starting New Hydrograph");
		String path = Platform.getInstallLocation().getURL().getPath();
		if ((StringUtils.isNotBlank(path)) && (StringUtils.startsWith(path, "/")) && (OSValidator.isWindows())) {
			path = StringUtils.substring(path, 1);
		}
		String command = path + HYDROGRAPH_EXE;
		Runtime runtime = Runtime.getRuntime();
		try {
			if (OSValidator.isWindows()) {
				String[] commandArray = { "cmd.exe", "/c", command };
				runtime.exec(commandArray);
			}
		} catch (IOException ioException) {
			logger.error("Exception occurred while starting hydrograph" + ioException);
		}
		System.exit(0);
	}

	private boolean updateINIFile(String javaHome) {
		logger.debug("Updating JAVA_HOME in ini file ::" + javaHome);
		javaHome = "-vm\n" + javaHome + "\n";
		RandomAccessFile file = null;
		boolean isUpdated = false;
		try {
			file = new RandomAccessFile(new File(HYDROGRAPH_INI), "rw");
			byte[] text = new byte[(int) file.length()];
			file.readFully(text);
			file.seek(0);
			file.writeBytes(javaHome);
			file.write(text);
			isUpdated = true;
		} catch (IOException ioException) {
			logger.error("IOException occurred while updating " + HYDROGRAPH_INI + " file", ioException);
		} finally {
			try {
				if (file != null) {
					file.close();
				}
			} catch (IOException ioException) {
				logger.error("IOException occurred while updating " + HYDROGRAPH_INI + " file", ioException);
			}
		}
		return isUpdated;
	}

	private Shell getShell() {
		if (shell== null) {
			return new Shell();
		}
		return shell;
	}

		
	
	
	private boolean updateINIOnJDKUpgrade(String javaHome) {
		logger.debug("Updating INI file if JDK path is updated");
		RandomAccessFile file = null;
		boolean isUpdated = false;
		try {
			file = new RandomAccessFile(new File(HYDROGRAPH_INI), "rw");
			byte[] text = new byte[(int) file.length()];
			
			while (file.getFilePointer() != file.length()) {
				if (StringUtils.equals(file.readLine(), "-vm")) {
					String currentLine=file.readLine();
					if (StringUtils.equals(currentLine, javaHome)) {
						logger.debug("JAVA_HOME and -vm in configuration file are same");
					} else {
						logger.debug("JAVA_HOME and -vm in configuration file are different. Updating configuration file with JAVA_HOME");
						file.seek(0);
						file.readFully(text);
						file.seek(0);
						updateData(text,javaHome,currentLine,file);
						isUpdated=true;
					}
					break;
				}
			}
		} catch (IOException ioException) {
			logger.error("IOException occurred while updating " + HYDROGRAPH_INI + " file", ioException);
		} finally {
			try {
				if (file != null) {
					file.close();
				}
			} catch (IOException ioException) {
				logger.error("IOException occurred while closing " + HYDROGRAPH_INI + " file", ioException);
			}
		}
		  return isUpdated;
	}

	private void updateData(byte[] text, String javaHome, String end,RandomAccessFile file) throws IOException {
		String data=new String(text);
		data=StringUtils.replace(data, end, javaHome);
		file.setLength(data.length());
		file.writeBytes(data);
	}

}
