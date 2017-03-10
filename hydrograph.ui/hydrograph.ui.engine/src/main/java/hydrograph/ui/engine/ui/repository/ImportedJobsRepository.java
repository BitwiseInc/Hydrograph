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

package hydrograph.ui.engine.ui.repository;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;

/**
 * The Class ImportedJobsRepository
 * Used to check duplicate Jobs.
 * 
 * @author Bitwise
 *
 */
public class ImportedJobsRepository {

	public static final ImportedJobsRepository INSTANCE=new ImportedJobsRepository();
	
	private List<IFile> importedJobsList = new ArrayList<IFile>();
	
	private ImportedJobsRepository() {	}

	/**
	 * Add imported job in list.
	 * @param importedJobFile
	 */
	public void addImportedJobName(IFile importedJobFile){
		importedJobsList.add(importedJobFile);
	}

	/**
	 * Return true, if importedJobsList contains file.
	 * @param file
	 * @return boolean
	 */
	public boolean contains(IFile file){
		return importedJobsList.contains(file);
	}
	
	/**
	 * clear all jobs from list.
	 */
	public void flush(){
		importedJobsList.clear();
	}
	
}
