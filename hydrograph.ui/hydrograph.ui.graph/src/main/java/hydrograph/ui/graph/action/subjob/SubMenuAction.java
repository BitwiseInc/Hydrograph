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

 
package hydrograph.ui.graph.action.subjob;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
public class SubMenuAction extends Action implements SelectionListener
  {
      private SelectionListener actionInstance;
   
      // the list of actions that are contained within this action
      private IAction[] actions;
   
      // should we hide the disabled ones (if not, they will appear as grayed out)
      private boolean hideDisabled;
   
      /***
       * Create a submenu.
       * 
       * @param subactions
       *            the actions that are contained within
       * @param text
       *            the container's textual label
       * @param toolTip
       *            the container's tooltip
       * @param descriptor
       *            the container's image descriptor
       * @param hideDisabledActions
       *            should we hide the disabled ones (if not, they will appear as
       *            grayed out)
       */
      public SubMenuAction(IAction[] subactions, String text, String toolTip, boolean hideDisabledActions)
      {
          super("", IAction.AS_DROP_DOWN_MENU);
   
          this.actionInstance = this;
          this.actions = subactions;
          this.hideDisabled = hideDisabledActions;
   
          setText(text);
          setToolTipText(toolTip);
          
   
          // the secondayr menu logic
          setMenuCreator(new IMenuCreator()
          {
              public Menu getMenu(Control parent)
              {
                  // this would be used outside of a menu. not useful for us.
                  return null;
              }
   
              public Menu getMenu(Menu parent)
              {
                  // create a submenu
                  Menu menu = new Menu(parent);
                  // fill it with our actions
                  for (int i = 0; i < actions.length; i++)
                  {
                    MenuItem item = new MenuItem(menu, SWT.NONE);
                    item.setText(actions[i].getText());
                    if (actions[i].getImageDescriptor() != null)
                        item.setImage(actions[i].getImageDescriptor().createImage());
                    
                    if (actions[i] == null || !actions[i].isEnabled() && hideDisabled)
                    {	                    
                    	item.setEnabled(false);
                       	continue;
               	 	}

                    item.setData(new Integer(i));
                    item.addSelectionListener(actionInstance);
                }
                return menu;
            }
 
            public void dispose()
             {
             }
         });
  
     }
  
     /**
      * Returns how many items are enabled in the menu. Useful to hide the
      * submenu when none are enabled.
      * 
      * @return the number of currently enabled menu items.
      */
     public int getActiveOperationCount()
     {
         int operationCount = 0;
         for (int i = 0; i < actions.length; i++)
             operationCount += actions[i] != null && actions[i].isEnabled() ? 1 : 0;
  
         return operationCount;
     }
  
     /**
      * Runs the default action
      */
     public void run()
     {
         actions[0].run();
     }
  
     /**
      * Runs the default action
      */
     public void widgetDefaultSelected(SelectionEvent e)
     {
         actions[0].run();
     }
  
     /**
      * Called when an item in the drop-down menu is selected. Runs the
      * associated run() method
      */
     public void widgetSelected(SelectionEvent e)
     {
         // get the index from the data and run that action.
         actions[((Integer) (((MenuItem) (e.getSource())).getData())).intValue()].run();
     }
 }