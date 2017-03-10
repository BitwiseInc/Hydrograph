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

 
package hydrograph.ui.graph.editorfactory;

import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.utility.CanvasUtils;
import hydrograph.ui.logging.factory.LogFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.slf4j.Logger;


/**
 * The Class FileStorageEditorContainer.
 */
public class FileStorageEditorContainer implements IGenrateContainerData {
	
	/** The Constant logger. */
	private static final Logger logger = LogFactory.INSTANCE.getLogger(FileStorageEditorContainer.class);
	
	/** The file storage editor input. */
	private final FileStoreEditorInput fileStorageEditorInput;
	
	/** The elt graphical editor instance. */
	private final ELTGraphicalEditor eltGraphicalEditorInstance;
	
	/**
	 * Instantiates a new file storage editor container.
	 * 
	 * @param editorInput
	 * @param eltGraphicalEditorInstance
	 */
	public FileStorageEditorContainer(IEditorInput editorInput,
			ELTGraphicalEditor eltGraphicalEditorInstance) {
		this.fileStorageEditorInput = (FileStoreEditorInput) editorInput;
		this.eltGraphicalEditorInstance = eltGraphicalEditorInstance;
	}

	/**
	 * Generate Container from xml,Setting FileStorageEditor Input into Ifile
	 * @return Container
	 * @throws IOException
	 */
	@Override
	public Container getEditorInput() throws IOException {
		logger.debug("storeEditorInput - Setting FileStorageEditor Input into Ifile");
		Container con = null;
		File file = new File(fileStorageEditorInput.getToolTipText());
		FileInputStream fs = new FileInputStream(file);
		con = (Container) CanvasUtils.INSTANCE.fromXMLToObject(fs);
		this.eltGraphicalEditorInstance.setPartName(file.getName());
		fs.close();
		return con;
	}

	/**
	 * Storing FileStorageEditor input into Ifile
	 * 
	 * Generate Container from xml
	 * @throws IOException
	 */	
	@Override
	public void storeEditorInput() throws IOException, CoreException {
		logger.debug("storeEditorInput - Storing FileStorageEditor input into Ifile");
		File file = new File(fileStorageEditorInput.getToolTipText());
		FileOutputStream fsout = new FileOutputStream(file);
		ByteArrayOutputStream arrayOutputStream=new ByteArrayOutputStream();
		CanvasUtils.INSTANCE.fromObjectToXML(
				eltGraphicalEditorInstance.getContainer(),arrayOutputStream);
		fsout.write(arrayOutputStream.toByteArray());
		arrayOutputStream.close();
		fsout.close();
		eltGraphicalEditorInstance.getCommandStack().markSaveLocation();
		eltGraphicalEditorInstance.setDirty(false);
		IFileStore fileStore = EFS.getLocalFileSystem().fromLocalFile(file);
		this.eltGraphicalEditorInstance.genrateTargetXml(null,fileStore,null);

	}

}
