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
import hydrograph.ui.graph.editor.ELTGraphicalEditorInput;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.logging.factory.LogFactory;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.slf4j.Logger;


// TODO: Auto-generated Javadoc
/**
 * The Class GenrateContainerData.
 */
public class GenrateContainerData {
	private static final Logger logger = LogFactory.INSTANCE.getLogger(GenrateContainerData.class);
	private IGenrateContainerData editorInput;
	
	/**
	 * Sets the editor input.
	 * 
	 * @param editorInput
	 *            the editor input
	 * @param eltGraphicalEditorInstance
	 *            the elt graphical editor instance
	 */
	public void setEditorInput(IEditorInput editorInput, ELTGraphicalEditor eltGraphicalEditorInstance) {
		logger.debug("setEditorInput - ");
		if((ELTGraphicalEditorInput.class).isAssignableFrom(editorInput.getClass()))
		{this.editorInput = new GraphicalEditorContiner(editorInput,eltGraphicalEditorInstance);}
		else{
			if((IFileEditorInput.class).isAssignableFrom(editorInput.getClass()))
				this.editorInput= new FileEditorContainer(editorInput,eltGraphicalEditorInstance);
			else
				if((FileStoreEditorInput.class).isAssignableFrom(editorInput.getClass()))
					this.editorInput=new FileStorageEditorContainer(editorInput,eltGraphicalEditorInstance);
		}
	}
	
	public Container getContainerData() throws CoreException, IOException
	{
		
		return editorInput.getEditorInput();
	}
	
	/**
	 * Store container data.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws CoreException
	 *             the core exception
	 */
	public void storeContainerData() throws IOException, CoreException
	{
		editorInput.storeEditorInput();
		
	}
	
}
