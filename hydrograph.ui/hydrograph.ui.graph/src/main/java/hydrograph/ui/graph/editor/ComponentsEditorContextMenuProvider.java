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
package hydrograph.ui.graph.editor;



import org.apache.commons.lang.StringUtils;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.actions.ActionFactory;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.graph.action.debug.ViewDataActionMenu;
import hydrograph.ui.graph.action.debug.WatcherMenuAction;
import hydrograph.ui.graph.action.subjob.SubMenuAction;
import hydrograph.ui.graph.controller.ComponentEditPart;
import hydrograph.ui.graph.utility.SubJobUtility;



/**
 * The Class ComponentsEditorContextMenuProvider.
 */
public class ComponentsEditorContextMenuProvider extends ContextMenuProvider {
	/** The editor's action registry. */
	private ActionRegistry actionRegistry;
	private static final String team = "team.main";
	private static final String replaceWith = "replaceWithMenu";
	private static final String separator = "additions";
	private static final String runAs = "org.eclipse.debug.ui.contextualLaunch.debug.submenu";
	private static final String debugAs = "org.eclipse.debug.ui.contextualLaunch.run.submenu";
	private static final String compareWith = "compareWithMenu";
	private static final String validate = "ValidationAction";
	
	/**
	 * Instantiate a new menu context provider for the specified EditPartViewer
	 * and ActionRegistry.
	 * @param viewer the editor's graphical viewer
	 * @param registry the editor's action registry
	 * @throws IllegalArgumentException if registry is <tt>null</tt>.
	 */
	public ComponentsEditorContextMenuProvider(EditPartViewer viewer, ActionRegistry registry) {
		super(viewer);
		if (registry == null) {
			throw new IllegalArgumentException();
		}
		actionRegistry = registry;
		
	}
	
	/**
	 * Called when the context menu is about to show. Actions, whose state is
	 * enabled, will appear in the context menu.
	 */
	@Override
	public void buildContextMenu(IMenuManager menu) {
		// Add standard action groups to the menu
		GEFActionConstants.addStandardActionGroups(menu);
		 
        IAction[] actions = new IAction[4];
		actions[0] = getAction(Constants.SUBJOB_CREATE);
	    actions[1] = getAction(Constants.SUBJOB_OPEN);
	    actions[2] = getAction(Constants.SUBJOB_UPDATE);
	    actions[3] = getAction(Constants.SUBJOB_TRACKING);
	    
	    SubMenuAction subJobMenu=new SubMenuAction( actions, Constants.SUBJOB_ACTION, Constants.SUBJOB_ACTION_ToolTip,true);	// Add actions to the menu
		IAction[] watcherAction = new IAction[2];
        watcherAction[0] = getAction(Constants.ADD_WATCH_POINT_ID);
        watcherAction[1] = getAction(Constants.REMOVE_WATCH_POINT_ID);
        WatcherMenuAction watcherMenu = new WatcherMenuAction(watcherAction, Constants.WATCHER_ACTION,Constants.WATCHER_ACTION_TEXT, true); //action to add watch points
         
        
        IAction[] viewDataActions = new IAction[2];
        viewDataActions[0] = getAction(Constants.CURRENT_VIEW_DATA_ID);
        viewDataActions[1] = getAction(Constants.WATCH_RECORD_ID);
        ViewDataActionMenu viewDataActionMenu = new ViewDataActionMenu(viewDataActions, "View Data", "View Data", true);
        //WatcherMenuAction viewDataActionMenu = new WatcherMenuAction(viewDataActions, Constants.WATCHER_ACTION,Constants.WATCHER_ACTION_TEXT, true); //action to add watch points
        
        
        //IAction actionWatchRecords = getAction(Constants.WATCH_RECORD_ID);// action to view debug data
        IAction componentHelpAction=getAction(Constants.HELP_ID);
        IAction componentPropertiesAction=getAction(Constants.COMPONENT_PROPERTIES_ID);
        
        
		menu.appendToGroup(GEFActionConstants.GROUP_UNDO, // target group id
		getAction(ActionFactory.UNDO.getId())); // action to add
		menu.appendToGroup(GEFActionConstants.GROUP_UNDO, getAction(ActionFactory.REDO.getId()));
	    menu.appendToGroup(GEFActionConstants.GROUP_EDIT, getAction(ActionFactory.DELETE.getId()));
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, getAction(ActionFactory.SAVE.getId()));
		menu.appendToGroup(GEFActionConstants.GROUP_COPY, getAction(ActionFactory.CUT.getId()));
		menu.appendToGroup(GEFActionConstants.GROUP_COPY, getAction(ActionFactory.COPY.getId()));
		menu.appendToGroup(GEFActionConstants.GROUP_COPY, getAction(ActionFactory.PASTE.getId()));
	    menu.appendToGroup(GEFActionConstants.GROUP_REST, subJobMenu);
	    menu.appendToGroup(GEFActionConstants.GROUP_REST, getAction(Constants.PROPAGATE));
	    menu.appendToGroup(GEFActionConstants.GROUP_REST, getAction(Constants.COMMENT_BOX));
	    menu.appendToGroup(GEFActionConstants.GROUP_REST, getAction(Constants.GRAPH_PROPERTY));
		menu.appendToGroup(GEFActionConstants.GROUP_REST, watcherMenu);
		//menu.appendToGroup(GEFActionConstants.GROUP_REST, actionWatchRecords);
		menu.appendToGroup(GEFActionConstants.GROUP_REST, viewDataActionMenu);
		menu.appendToGroup(GEFActionConstants.GROUP_REST,componentPropertiesAction);
		menu.appendToGroup(GEFActionConstants.GROUP_REST, componentHelpAction);

		if(subJobMenu.getActiveOperationCount()== 0)
	    subJobMenu.setEnabled(false);
			 
		if(watcherMenu.getActiveOperationCount()== 0){
			watcherMenu.setEnabled(false);
		}
		
		if(viewDataActionMenu.getActiveOperationCount() == 0){
			viewDataActionMenu.setEnabled(false);
		}
		
	}
	
	private IAction getAction(String actionId) {
		return actionRegistry.getAction(actionId);
	}
	
	@Override
	protected void doItemFill(IContributionItem ci, int index) {
		
		StructuredSelection s=(StructuredSelection)SubJobUtility.getCurrentEditor().getViewer().getSelection();
			
			if (s.getFirstElement() instanceof ComponentEditPart && (StringUtils.equalsIgnoreCase(ci.getId(),team)||
				StringUtils.equalsIgnoreCase(ci.getId(),replaceWith)|| StringUtils.equalsIgnoreCase(ci.getId(),separator))){
					return;
			}
	
			if((StringUtils.equalsIgnoreCase(ci.getId(),runAs)||
				StringUtils.equalsIgnoreCase(ci.getId(),debugAs)||
				StringUtils.equalsIgnoreCase(ci.getId(),compareWith)||
				StringUtils.equalsIgnoreCase(ci.getId(),validate))){
					return ;
			}	
		super.doItemFill(ci, index);
	}
}
