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

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.utility.CanvasUtils;
import hydrograph.ui.logging.factory.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.slf4j.Logger;


// TODO: Auto-generated Javadoc
/**
 * The Class FileEditorContainer.
 */
public class FileEditorContainer implements IGenrateContainerData {
	private static final Logger logger = LogFactory.INSTANCE.getLogger(FileEditorContainer.class);
	private final IFileEditorInput ifileEditorInput;
	private final ELTGraphicalEditor eltGraphicalEditorInstance;
	
	/**
	 * Instantiates a new file editor continer.
	 * 
	 * @param editorInput
	 *            the editor input
	 * @param eltGraphicalEditorInstance
	 *            the elt graphical editor instance
	 */
	public FileEditorContainer(IEditorInput editorInput, ELTGraphicalEditor eltGraphicalEditorInstance) {
		this.ifileEditorInput=(IFileEditorInput)editorInput;
		this.eltGraphicalEditorInstance=eltGraphicalEditorInstance;
	}

	@Override
	public Container getEditorInput() throws CoreException {
		logger.debug("getEditorInput - Setting IFileEditor input");
		IFile Ifile = ifileEditorInput.getFile();
		this.eltGraphicalEditorInstance.setPartName(Ifile.getName());
		return (Container)CanvasUtils.INSTANCE.fromXMLToObject(Ifile.getContents());
	}

	@Override
	public void storeEditorInput() throws IOException, CoreException {
		logger.debug("storeEditorInput - Storing IFileEditor input into Ifile");
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			eltGraphicalEditorInstance.createOutputStream(out);
			IFile ifile = ifileEditorInput.getFile();
			ifile.setContents(new ByteArrayInputStream(out.toByteArray()),true, false, null);
			this.eltGraphicalEditorInstance.getCommandStack().markSaveLocation();
			this.eltGraphicalEditorInstance.genrateTargetXml(ifile,null,null);
			this.eltGraphicalEditorInstance.setDirty(false);
			
		
	}

	
	
}
