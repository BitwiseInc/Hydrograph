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
package hydrograph.ui.dataviewer.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * The Class FilterConditions. 
 * This class holds the Filter Conditions that are currently applied in the watcher window.
 *  
 * @author Bitwise
 */
public class FilterConditions {
	
	private Map<Integer,List<List<Integer>>> localGroupSelectionMap = new TreeMap<>();
	private Map<Integer,List<List<Integer>>> remoteGroupSelectionMap = new TreeMap<>();
	private List<Condition> localConditions;
	private List<Condition> remoteConditions;
	private boolean retainLocal=false;
	private boolean retainRemote=false;
	private String localCondition;
	private String remoteCondition;
	private boolean isOverWritten = false;
	
	/**
	 * Checks if is over written.
	 * 
	 * @return true, if is over written
	 */
	public boolean isOverWritten() {
		return isOverWritten;
	}
	
	/**
	 * Sets the over written.
	 * 
	 * @param isOverWritten
	 *            the new over written
	 */
	public void setOverWritten(boolean isOverWritten) {
		this.isOverWritten = isOverWritten;
	}
	
	/**
	 * Instantiates a new filter conditions.
	 */
	public FilterConditions() {
		localConditions = new ArrayList<>();
		remoteConditions = new ArrayList<>();
	}
	
	/**
	 * Gets the local conditions.
	 * 
	 * @return the local conditions
	 */
	public List<Condition> getLocalConditions() {
		return localConditions;
	}
	
	/**
	 * Sets the local conditions.
	 * 
	 * @param localConditions
	 *            the new local conditions
	 */
	public void setLocalConditions(List<Condition> localConditions) {
		this.localConditions.clear();
		this.localConditions = localConditions;
	}
	
	/**
	 * Gets the remote conditions.
	 * 
	 * @return the remote conditions
	 */
	public List<Condition> getRemoteConditions() {
		return remoteConditions;
	}
	
	/**
	 * Sets the remote conditions.
	 * 
	 * @param remoteConditions
	 *            the new remote conditions
	 */
	public void setRemoteConditions(List<Condition> remoteConditions) {
		this.remoteConditions = remoteConditions;
	}
	
	/**
	 * Gets the retain local.
	 * 
	 * @return the retain local
	 */
	public boolean getRetainLocal() {
		return retainLocal;
	}
	
	/**
	 * Sets the retain local.
	 * 
	 * @param retainLocal
	 *            the new retain local
	 */
	public void setRetainLocal(boolean retainLocal) {
		this.retainLocal = retainLocal;
	}
	
	/**
	 * Gets the retain remote.
	 * 
	 * @return the retain remote
	 */
	public boolean getRetainRemote() {
		return retainRemote;
	}
	
	/**
	 * Sets the retain remote.
	 * 
	 * @param retainRemote
	 *            the new retain remote
	 */
	public void setRetainRemote(boolean retainRemote) {
		this.retainRemote = retainRemote;
	}
	
	/**
	 * Sets the local condition.
	 * 
	 * @param localCondition
	 *            the new local condition
	 */
	public void setLocalCondition(String localCondition){
		this.localCondition = localCondition;
	}

	/**
	 * Gets the remote condition.
	 * 
	 * @return the remote condition
	 */
	public String getRemoteCondition() {
		return remoteCondition;
	}
	
	/**
	 * Sets the remote condition.
	 * 
	 * @param remoteCondition
	 *            the new remote condition
	 */
	public void setRemoteCondition(String remoteCondition) {
		this.remoteCondition = remoteCondition;
	}
	
	/**
	 * Gets the local condition.
	 * 
	 * @return the local condition
	 */
	public String getLocalCondition() {
		return localCondition;
	}
	
	/**
	 * Gets the local group selection map.
	 * 
	 * @return the local group selection map
	 */
	public Map<Integer, List<List<Integer>>> getLocalGroupSelectionMap() {
		return localGroupSelectionMap;
	}
	
	/**
	 * Sets the local group selection map.
	 * 
	 * @param localGroupSelectionMap
	 *            the local group selection map
	 */
	public void setLocalGroupSelectionMap(
			Map<Integer, List<List<Integer>>> localGroupSelectionMap) {
		this.localGroupSelectionMap = localGroupSelectionMap;
	}
	
	/**
	 * Gets the remote group selection map.
	 * 
	 * @return the remote group selection map
	 */
	public Map<Integer, List<List<Integer>>> getRemoteGroupSelectionMap() {
		return remoteGroupSelectionMap;
	}
	
	/**
	 * Sets the remote group selection map.
	 * 
	 * @param remoteGroupSelectionMap
	 *            the remote group selection map
	 */
	public void setRemoteGroupSelectionMap(
			Map<Integer, List<List<Integer>>> remoteGroupSelectionMap) {
		this.remoteGroupSelectionMap = remoteGroupSelectionMap;
	}
}


