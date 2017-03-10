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

 
package hydrograph.ui.perspective;

import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.perspective.dialog.PreStartActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import javax.tools.ToolProvider;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.ide.ChooseWorkspaceData;
import org.eclipse.ui.internal.ide.ChooseWorkspaceDialog;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.StatusUtil;


/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

	/**
	 * The name of the folder containing metadata information for the workspace.
	 */
    public static final String METADATA_FOLDER = ".metadata"; //$NON-NLS-1$

    private static final String JDK_PATH_TEMPLATE="/path/jdk/bin";
    
    private static final String VERSION_FILENAME = "version.ini"; //$NON-NLS-1$
    
    private static final String WORKSPACE_VERSION_KEY = "org.eclipse.core.runtime"; //$NON-NLS-1$
    
    private static final String WORKSPACE_VERSION_VALUE = "1"; //$NON-NLS-1$
    
    /**
     * A special return code that will be recognized by the PDE launcher and used to
     * show an error dialog if the workspace is locked.
     */
    private static final Integer EXIT_WORKSPACE_LOCKED = new Integer(15);
    
	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object start(IApplicationContext context) throws Exception {
		Display display = PlatformUI.createDisplay();
		Shell shell = WorkbenchPlugin.getSplashShell(display);

		if (OSValidator.isWindows() && !PreStartActivity.isDevLaunchMode(context.getArguments())) {
			PreStartActivity activity = new PreStartActivity(shell);
			if (ToolProvider.getSystemJavaCompiler() == null) {
				activity.performPreStartActivity();
			} else {
				activity.updateINIOnJDkUpgrade();
			}
		}
		try {
			Object instanceLocationCheck = checkInstanceLocation(shell, context.getArguments());
			if (instanceLocationCheck != null) {
            	WorkbenchPlugin.unsetSplashShell(display);
                context.applicationRunning();
                return instanceLocationCheck;
            }
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART)
				return IApplication.EXIT_RESTART;
			else
				return IApplication.EXIT_OK;
		} finally {
			if (display != null) {
				display.dispose();
			}
			Location instanceLoc = Platform.getInstanceLocation();
			if (instanceLoc != null){
				instanceLoc.release();
			}
		}
		
	}
	
	 /**
     * Return <code>null</code> if a valid workspace path has been set and an exit code otherwise.
     * Prompt for and set the path if possible and required.
     * 
     * @param applicationArguments the command line arguments
     * @return <code>null</code> if a valid instance location has been set and an exit code
     *         otherwise
     */
    private Object checkInstanceLocation(Shell shell, Map applicationArguments) {
        // -data @none was specified but an ide requires workspace
        Location instanceLoc = Platform.getInstanceLocation();
        if (instanceLoc == null) {
            MessageDialog
                    .openError(
                            shell,
                            IDEWorkbenchMessages.IDEApplication_workspaceMandatoryTitle,
                            IDEWorkbenchMessages.IDEApplication_workspaceMandatoryMessage);
            return EXIT_OK;
        }

        // -data "/valid/path", workspace already set
        if (instanceLoc.isSet()) {
            // make sure the meta data version is compatible (or the user has
            // chosen to overwrite it).
            if (!checkValidWorkspace(shell, instanceLoc.getURL())) {
				return EXIT_OK;
			}

            // at this point its valid, so try to lock it and update the
            // metadata version information if successful
            try {
                if (instanceLoc.lock()) {
                    writeWorkspaceVersion();
                    return null;
                }
                
                // we failed to create the directory.  
                // Two possibilities:
                // 1. directory is already in use
                // 2. directory could not be created
                File workspaceDirectory = new File(instanceLoc.getURL().getFile());
                if (workspaceDirectory.exists()) {
                	if (isDevLaunchMode(applicationArguments)) {
                		return EXIT_WORKSPACE_LOCKED;
                	}
	                MessageDialog.openError(
	                        shell,
	                        IDEWorkbenchMessages.IDEApplication_workspaceCannotLockTitle,
	                        IDEWorkbenchMessages.IDEApplication_workspaceCannotLockMessage);
                } else {
                	MessageDialog.openError(
                			shell, 
                			IDEWorkbenchMessages.IDEApplication_workspaceCannotBeSetTitle,
                			IDEWorkbenchMessages.IDEApplication_workspaceCannotBeSetMessage);
                }
            } catch (IOException e) {
                IDEWorkbenchPlugin.log("Could not obtain lock for workspace location", //$NON-NLS-1$
                        e);            	
                MessageDialog
                .openError(
                        shell,
                        IDEWorkbenchMessages.InternalError,
                        e.getMessage());                
            }            
            return EXIT_OK;
        }

        // -data @noDefault or -data not specified, prompt and set
        ChooseWorkspaceData launchData = new ChooseWorkspaceData(instanceLoc
                .getDefault());

        boolean force = false;
        while (true) {
            URL workspaceUrl = promptForWorkspace(shell, launchData, force);
            if (workspaceUrl == null) {
				return EXIT_OK;
			}

            // if there is an error with the first selection, then force the
            // dialog to open to give the user a chance to correct
            force = true;

            try {
                // the operation will fail if the url is not a valid
                // instance data area, so other checking is unneeded
                if (instanceLoc.setURL(workspaceUrl, true)) {
                    launchData.writePersistedData();
                    writeWorkspaceVersion();
                    return null;
                }
            } catch (IllegalStateException e) {
                MessageDialog
                        .openError(
                                shell,
                                IDEWorkbenchMessages.IDEApplication_workspaceCannotBeSetTitle,
                                IDEWorkbenchMessages.IDEApplication_workspaceCannotBeSetMessage);
                return EXIT_OK;
            }

            // by this point it has been determined that the workspace is
            // already in use -- force the user to choose again
            MessageDialog.openError(shell, IDEWorkbenchMessages.IDEApplication_workspaceInUseTitle, 
                    IDEWorkbenchMessages.IDEApplication_workspaceInUseMessage);
        }
    }
    
    /**
     * Open a workspace selection dialog on the argument shell, populating the
     * argument data with the user's selection. Perform first level validation
     * on the selection by comparing the version information. This method does
     * not examine the runtime state (e.g., is the workspace already locked?).
     * 
     * @param shell
     * @param launchData
     * @param force
     *            setting to true makes the dialog open regardless of the
     *            showDialog value
     * @return An URL storing the selected workspace or null if the user has
     *         canceled the launch operation.
     */
    private URL promptForWorkspace(Shell shell, ChooseWorkspaceData launchData,
			boolean force) {
        URL url = null;
        do {
        	// okay to use the shell now - this is the splash shell
            new ChooseWorkspaceDialog(shell, launchData, false, true).prompt(force);
            String instancePath = launchData.getSelection();
            if (instancePath == null) {
				return null;
			}

            // the dialog is not forced on the first iteration, but is on every
            // subsequent one -- if there was an error then the user needs to be
            // allowed to fix it
            force = true;

            // 70576: don't accept empty input
            if (instancePath.length() <= 0) {
                MessageDialog
                .openError(
                        shell,
                        IDEWorkbenchMessages.IDEApplication_workspaceEmptyTitle,
                        IDEWorkbenchMessages.IDEApplication_workspaceEmptyMessage);
                continue;
            }

            // create the workspace if it does not already exist
            File workspace = new File(instancePath);
            if (!workspace.exists()) {
				workspace.mkdir();
			}

            try {
                // Don't use File.toURL() since it adds a leading slash that Platform does not
                // handle properly.  See bug 54081 for more details.  
                String path = workspace.getAbsolutePath().replace(
                        File.separatorChar, '/');
                url = new URL("file", null, path); //$NON-NLS-1$
            } catch (MalformedURLException e) {
                MessageDialog
                        .openError(
                                shell,
                                IDEWorkbenchMessages.IDEApplication_workspaceInvalidTitle,
                                IDEWorkbenchMessages.IDEApplication_workspaceInvalidMessage);
                continue;
            }
        } while (!checkValidWorkspace(shell, url));

        return url;
    }
    
    /**
     * Return true if the argument directory is ok to use as a workspace and
     * false otherwise. A version check will be performed, and a confirmation
     * box may be displayed on the argument shell if an older version is
     * detected.
     * 
     * @return true if the argument URL is ok to use as a workspace and false
     *         otherwise.
     */
    private boolean checkValidWorkspace(Shell shell, URL url) {
        // a null url is not a valid workspace
        if (url == null) {
			return false;
		}

        String version = readWorkspaceVersion(url);

        // if the version could not be read, then there is not any existing
        // workspace data to trample, e.g., perhaps its a new directory that
        // is just starting to be used as a workspace
        if (version == null) {
			return true;
		}

        final int ide_version = Integer.parseInt(WORKSPACE_VERSION_VALUE);
        int workspace_version = Integer.parseInt(version);

        // equality test is required since any version difference (newer
        // or older) may result in data being trampled
        if (workspace_version == ide_version) {
			return true;
		}

        // At this point workspace has been detected to be from a version
        // other than the current ide version -- find out if the user wants
        // to use it anyhow.
        String title = IDEWorkbenchMessages.IDEApplication_versionTitle_newerWorkspace;
        String message = NLS.bind(IDEWorkbenchMessages.IDEApplication_versionMessage_newerWorkspace, url.getFile());

        MessageBox mbox = new MessageBox(shell, SWT.OK | SWT.CANCEL
                | SWT.ICON_WARNING | SWT.APPLICATION_MODAL);
        mbox.setText(title);
        mbox.setMessage(message);
        return mbox.open() == SWT.OK;
    }
    
    /**
     * Look at the argument URL for the workspace's version information. Return
     * that version if found and null otherwise.
     */
    private static String readWorkspaceVersion(URL workspace) {
        File versionFile = getVersionFile(workspace, false);
        if (versionFile == null || !versionFile.exists()) {
			return null;
		}

        try {
            // Although the version file is not spec'ed to be a Java properties
            // file, it happens to follow the same format currently, so using
            // Properties to read it is convenient.
            Properties props = new Properties();
            FileInputStream is = new FileInputStream(versionFile);
            try {
                props.load(is);
            } finally {
                is.close();
            }

            return props.getProperty(WORKSPACE_VERSION_KEY);
        } catch (IOException e) {
            IDEWorkbenchPlugin.log("Could not read version file", new Status( //$NON-NLS-1$
                    IStatus.ERROR, IDEWorkbenchPlugin.IDE_WORKBENCH,
                    IStatus.ERROR,
                    e.getMessage() == null ? "" : e.getMessage(), //$NON-NLS-1$, 
                    e));
            return null;
        }
    }
    
    private static boolean isDevLaunchMode(Map args) {
		// see org.eclipse.pde.internal.core.PluginPathFinder.isDevLaunchMode()
		if (Boolean.getBoolean("eclipse.pde.launch")) //$NON-NLS-1$
			return true;
		return args.containsKey("-pdelaunch"); //$NON-NLS-1$
	}
    

    /**
     * Write the version of the metadata into a known file overwriting any
     * existing file contents. Writing the version file isn't really crucial,
     * so the function is silent about failure
     */
    private static void writeWorkspaceVersion() {
        Location instanceLoc = Platform.getInstanceLocation();
        if (instanceLoc == null || instanceLoc.isReadOnly()) {
			return;
		}

        File versionFile = getVersionFile(instanceLoc.getURL(), true);
        if (versionFile == null) {
			return;
		}

        OutputStream output = null;
        try {
            String versionLine = WORKSPACE_VERSION_KEY + '='
                    + WORKSPACE_VERSION_VALUE;

            output = new FileOutputStream(versionFile);
            output.write(versionLine.getBytes("UTF-8")); //$NON-NLS-1$
        } catch (IOException e) {
            IDEWorkbenchPlugin.log("Could not write version file", //$NON-NLS-1$
                    StatusUtil.newStatus(IStatus.ERROR, e.getMessage(), e));
        } finally {
            try {
                if (output != null) {
					output.close();
				}
            } catch (IOException e) {
                // do nothing
            }
        }
    }

    /**
     * The version file is stored in the metadata area of the workspace. This
     * method returns an URL to the file or null if the directory or file does
     * not exist (and the create parameter is false).
     * 
     * @param create
     *            If the directory and file does not exist this parameter
     *            controls whether it will be created.
     * @return An url to the file or null if the version file does not exist or
     *         could not be created.
     */
    private static File getVersionFile(URL workspaceUrl, boolean create) {
        if (workspaceUrl == null) {
			return null;
		}

        try {
            // make sure the directory exists
            File metaDir = new File(workspaceUrl.getPath(), METADATA_FOLDER);
            if (!metaDir.exists() && (!create || !metaDir.mkdir())) {
				return null;
			}

            // make sure the file exists
            File versionFile = new File(metaDir, VERSION_FILENAME);
            if (!versionFile.exists()
                    && (!create || !versionFile.createNewFile())) {
				return null;
			}

            return versionFile;
        } catch (IOException e) {
            // cannot log because instance area has not been set
            return null;
        }
    }
    
	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		if (!PlatformUI.isWorkbenchRunning())
			return;
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}
	
}
