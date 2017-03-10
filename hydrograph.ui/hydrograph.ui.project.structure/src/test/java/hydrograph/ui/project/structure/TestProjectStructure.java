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

package hydrograph.ui.project.structure;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.JavaCore;
import org.junit.Assert;
import org.junit.Test;

import hydrograph.ui.project.structure.natures.ProjectNature;
import hydrograph.ui.project.structure.wizard.ProjectStructureCreator;
import hydrograph.ui.project.structure.wizard.ProjectStructureCreator.InvalidProjectNameException;
 
 
/**
 * The Class TestProjectStructure.
 * 
 * @author Bitwise
 */
public class TestProjectStructure {
   
	/**
	 * Test create project with empty name arg.
	 */
	/*  
    @SuppressWarnings("nls")
    @Test(expected = ProjectStructureCreator.InvalidProjectNameException.class)
    public void testCreateProjectWithEmptyNameArg() {
        ProjectStructureCreator.INSTANCE.createProject("", null);
    }
  */
	/**
	 * Test create project with null name arg.
	 */
	 	/*  
    @Test(expected = ProjectStructureCreator.InvalidProjectNameException.class)
    public void testCreateProjectWithNullNameArg() {
    	ProjectStructureCreator.INSTANCE.createProject(null, null);
    }
   */   
	/**
	 * Test create project with good args.
	 * 
	 * @throws CoreException
	 *             the core exception
	 * @throws IOException 
	 */
    @SuppressWarnings("nls")
    @Test
    public void testCreateProjectWithGoodArgs() throws CoreException, IOException {
        // This is the default workspace for this plug-in
       /* String workspaceFilePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
        String projectName = "delete-me";
        String projectPath = workspaceFilePath + File.separator + projectName;
        createFilesForTest();
        assertProjectDotFileAndStructureAndNatureExist(projectPath, projectName, null);*/
    }
 
    
    @SuppressWarnings("nls")
    private void assertProjectDotFileAndStructureAndNatureExist(String projectPath, String name, URI location) 
    		throws CoreException {
        /*IProject project = ProjectStructureCreator.INSTANCE.createProject(name, location);
        String projectFilePath = projectPath + File.separator + ".project";
 
        Assert.assertNotNull(project);
        assertFileExists(projectFilePath);
        assertNatureIn(project);
        assertFolderStructureIn(projectPath);
        project.delete(true, null);*/
    }
 
    @SuppressWarnings("nls")
    private void assertFolderStructureIn(String projectPath) {
        for (String path : ProjectStructureCreator.paths) {
            File file = new File(projectPath + File.separator + path);
            if (!file.exists()) {
                Assert.fail("Folder structure " + path + " does not exist.");
            }
        }
    }
 
    private void assertNatureIn(IProject project) throws CoreException {
        IProjectDescription descriptions = project.getDescription();
        String[] natureIds = descriptions.getNatureIds();
        if (natureIds.length != 3) {
            Assert.fail("Not all natures found in project."); //$NON-NLS-1$
        }
 
        if (!natureIds[0].equals(ProjectNature.NATURE_ID) ||
        		!natureIds[1].equals(JavaCore.NATURE_ID) ||
        		!natureIds[2].endsWith("org.eclipse.m2e.core.maven2Nature")){
            Assert.fail("Required project natures not found in project."); //$NON-NLS-1$
        }
    }
 
    private void assertFileExists(String projectFilePath) {
        File file = new File(projectFilePath);
 
        if (!file.exists()) {
            Assert.fail("File " + projectFilePath + " does not exist."); //$NON-NLS-1$//$NON-NLS-2$
        }
    }
    
    private void createFilesForTest() throws IOException{
    	File configDir = new File(Platform.getInstallLocation().getURL().getPath() + "/config");
    	if(!configDir.exists()){
    		configDir.mkdir();
    	}
    	File mavenDir = new File(configDir.getPath() + "/maven");
    	if(!mavenDir.exists()){
    		mavenDir.mkdir();
    	}
    	File destinationFile = new File(mavenDir + "/pom.xml");
    	if(destinationFile.exists()){
    		return;
    	}
    	else{
    		destinationFile.createNewFile();
    	}
    }
}