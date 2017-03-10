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

import org.eclipse.ui.IEditorInput;
import org.slf4j.Logger;


// TODO: Auto-generated Javadoc
/**
 * The Class GraphicalEditorContiner.
 */
public class GraphicalEditorContiner implements IGenrateContainerData {
	private static final Logger logger = LogFactory.INSTANCE.getLogger(GraphicalEditorContiner.class);
	private final ELTGraphicalEditorInput graphicaleditorInput;
	private final ELTGraphicalEditor eltGraphicalEditorInstance;
	
	/**
	 * Instantiates a new graphical editor continer.
	 * 
	 * @param editorInput
	 *            the editor input
	 * @param eltGraphicalEditorInstance
	 *            the elt graphical editor instance
	 */
	public GraphicalEditorContiner(IEditorInput editorInput, ELTGraphicalEditor eltGraphicalEditorInstance) {
		this.graphicaleditorInput = (ELTGraphicalEditorInput) editorInput;
		this.eltGraphicalEditorInstance=eltGraphicalEditorInstance;
	}

	@Override
	public Container getEditorInput() {
		logger.debug("getEditorInput - Setting GraphicalEditor Input");
		this.eltGraphicalEditorInstance.setPartName(this.graphicaleditorInput.getName());
		return new Container();
	}

	@Override
	public void storeEditorInput() {
		logger.debug("storeEditorInput - Calling doSaveAs method");
		eltGraphicalEditorInstance.doSaveAs();
		
	}
}

