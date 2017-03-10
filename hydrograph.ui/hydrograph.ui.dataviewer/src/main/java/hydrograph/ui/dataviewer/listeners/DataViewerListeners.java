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

package hydrograph.ui.dataviewer.listeners;

import hydrograph.ui.dataviewer.actions.SelectColumnAction;
import hydrograph.ui.dataviewer.adapters.DataViewerAdapter;
import hydrograph.ui.dataviewer.constants.Messages;
import hydrograph.ui.dataviewer.constants.StatusConstants;
import hydrograph.ui.dataviewer.constants.ControlConstants;
import hydrograph.ui.dataviewer.constants.Views;
import hydrograph.ui.dataviewer.datastructures.StatusMessage;
import hydrograph.ui.dataviewer.support.StatusManager;
import hydrograph.ui.dataviewer.utilities.DataViewerUtility;
import hydrograph.ui.dataviewer.viewloders.DataViewLoader;
import hydrograph.ui.dataviewer.window.DebugDataViewer;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

/**
 * The Class DataViewerListeners.
 * DataViewerListeners holds all listener for Data viewer Windows.
 * 
 * @author Bitwise
 * 
 */
public class DataViewerListeners {
	private DataViewLoader dataViewLoader;
	private StatusManager statusManager;
	private DataViewerAdapter dataViewerAdapter;
	private Map<String, Control> windowControls;
	private static final int ENTER_KEY_CODE=13;
	private DebugDataViewer debugDataViewer;
	/**
	 * 
	 * Set data view loader
	 * 
	 */
	public void setDataViewLoader(DataViewLoader dataViewLoader) {
		this.dataViewLoader = dataViewLoader;
	}

	/**
	 * 
	 * Set {@link DataViewerAdapter}
	 * 
	 */
	public void setDataViewerAdpater(DataViewerAdapter dataViewerAdapter) {
		this.dataViewerAdapter = dataViewerAdapter;
	}

	/**
	 * 
	 * set {@link StatusManager}
	 * 
	 * @param statusManager
	 */
	public void setStatusManager(StatusManager statusManager) {
		this.statusManager = statusManager;
	}

	/**
	 * 
	 * Set data viewer window control list
	 * 
	 * @param windowControls
	 */
	public void setWindowControls(Map<String, Control> windowControls) {
		this.windowControls = windowControls;
	}

	/**
	 * 
	 * Set debug data viewer
	 * 
	 * @param debugDataViewer
	 */
	public void setDebugDataViewer(DebugDataViewer debugDataViewer) {
		this.debugDataViewer = debugDataViewer;
	}
	
	/**
	 * 
	 * Attach data viewer tab folder listener. This listener loads data in selected view
	 * 
	 * @param tabFolder
	 */
	public void addTabFolderSelectionChangeListener(CTabFolder tabFolder) {
		tabFolder.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				dataViewLoader.reloadloadViews();
				disableColumnSelectionForUnformattedView(e); 
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// DO Nothing
			}
		});
	}

	private void disableColumnSelectionForUnformattedView(
			SelectionEvent e) {
		if(e.item.getData("VIEW_NAME").equals(Views.UNFORMATTED_VIEW_NAME)){
			debugDataViewer.getActionFactory().getAction(SelectColumnAction.class.getName()).setEnabled(false);
		}else{
			debugDataViewer.getActionFactory().getAction(SelectColumnAction.class.getName()).setEnabled(true);
		}
	}
	
	private void jumpPageListener() {
		final Text jumpPageTextBox = ((Text) windowControls.get(ControlConstants.JUMP_TEXT));

		if (((Text) windowControls.get(ControlConstants.JUMP_TEXT)).getText().isEmpty()) {
			statusManager.setStatus(new StatusMessage(StatusConstants.ERROR, Messages.JUMP_PAGE_TEXTBOX_CAN_NOTE_BE_EMPTY));
			return;
		}

		String statusMessage=Messages.FETCHING_PAGE + " " + jumpPageTextBox.getText();
		statusManager.setStatus(new StatusMessage(StatusConstants.PROGRESS, statusMessage));
		statusManager.setAllWindowControlsEnabled(false);
		
		Long pageNumberToJump = Long.valueOf(jumpPageTextBox.getText());
		
		executeJumpPageJob(statusMessage, pageNumberToJump);
	}

	private void executeJumpPageJob(String statusMessage, final Long pageNumberToJump) {
		Job job = new Job(statusMessage) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {

				final StatusMessage status = dataViewerAdapter.jumpToPage(pageNumberToJump);
				dataViewLoader.updateDataViewLists();

				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						refreshDataViewerWindow(status);
						DataViewerUtility.INSTANCE.resetSort(debugDataViewer);
						debugDataViewer.redrawTableCursor();
					}
				});
				return Status.OK_STATUS;
			}

		};
		job.schedule();
	}

	/**
	 * 
	 * Attach jump page listener. 
	 * The listener can be attached to only Button and Text box
	 * 
	 * @param widget
	 */
	public void attachJumpPageListener(final Widget widget) {
		if (widget instanceof Text) {
			((Text) widget).addKeyListener(new KeyListener() {
				@Override
				public void keyReleased(KeyEvent e) {
					if (e.keyCode == SWT.KEYPAD_CR || e.keyCode == ENTER_KEY_CODE) {
						jumpPageListener();
					}
				}

				@Override
				public void keyPressed(KeyEvent e) {
					// Nothing to do
				}
			});
		}

		if (widget instanceof Button) {
			((Button) widget).addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					jumpPageListener();
				}
			});
		}
	}

	/**
	 * 
	 * Attach Previous page listener.
	 * 
	 * @param button
	 */
	public void attachPreviousPageButtonListener(Button button) {
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {

				statusManager.setStatus(new StatusMessage(StatusConstants.PROGRESS, Messages.FETCHING_PREVIOUS_PAGE));
				statusManager.setAllWindowControlsEnabled(false);

				Job job = new Job(Messages.FETCHING_PREVIOUS_PAGE) {
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						final StatusMessage status = dataViewerAdapter.previous();

						dataViewLoader.updateDataViewLists();

						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								refreshDataViewerWindow(status);
								statusManager.clearJumpToPageText();
								DataViewerUtility.INSTANCE.resetSort(debugDataViewer);
								debugDataViewer.redrawTableCursor();
							}							
						});
						return Status.OK_STATUS;
					}
				};
				job.schedule();
			}
		});
	}
	
	/**
	 * 
	 * Attach next page listener
	 * 
	 * @param button
	 */
	public void attachNextPageButtonListener(Button button) {
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				statusManager.setStatus(new StatusMessage(StatusConstants.PROGRESS, Messages.FETCHING_NEXT_PAGE));
				statusManager.setAllWindowControlsEnabled(false);

				Job job = new Job(Messages.FETCHING_NEXT_PAGE) {
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						final StatusMessage status = dataViewerAdapter.next();
						dataViewLoader.updateDataViewLists();
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								refreshDataViewerWindow(status);
								statusManager.clearJumpToPageText();
								DataViewerUtility.INSTANCE.resetSort(debugDataViewer);
								debugDataViewer.redrawTableCursor();
							}
						});
						return Status.OK_STATUS;
					}
				};

				job.schedule();
			}
		});
	}
	
	private void refreshDataViewerWindow(final StatusMessage status) {
		dataViewLoader.reloadloadViews();
		statusManager.setAllWindowControlsEnabled(true);
		statusManager.setStatus(status);
	}
}
