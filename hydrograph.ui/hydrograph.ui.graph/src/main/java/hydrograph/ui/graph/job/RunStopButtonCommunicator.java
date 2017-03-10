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

 
package hydrograph.ui.graph.job;

import org.eclipse.core.commands.AbstractHandler;
/**
 * 
 * The class is responsible for providing references to run and stop job button
 * 
 * @author Bitwise
 *
 */

public enum RunStopButtonCommunicator{
	RunJob {
		private AbstractHandler abstractHandler;
		
		AbstractHandler getAbstractHandler() {
			return abstractHandler;
		}
		void setAbstractHandler(AbstractHandler abstractHandler){
			this.abstractHandler = abstractHandler;
		}		
	},
	
	RunDebugJob {
		private AbstractHandler abstractHandler;
		
		AbstractHandler getAbstractHandler() {
			return abstractHandler;
		}
		void setAbstractHandler(AbstractHandler abstractHandler){
			this.abstractHandler = abstractHandler;
		}		
	},
	
	StopJob {
		private AbstractHandler abstractHandler;
		
		AbstractHandler getAbstractHandler() {
			return abstractHandler;
		}
		void setAbstractHandler(AbstractHandler abstractHandler){
			this.abstractHandler = abstractHandler;
		}		
	},
	
	Removewatcher {
		private AbstractHandler abstractHandler;
		
		AbstractHandler getAbstractHandler() {
			return abstractHandler;
		}
		void setAbstractHandler(AbstractHandler abstractHandler){
			this.abstractHandler = abstractHandler;
		}	
	},
	ExecutionTrackingConsole{

		private AbstractHandler abstractHandler;
		
		AbstractHandler getAbstractHandler() {
			return abstractHandler;
		}
		void setAbstractHandler(AbstractHandler abstractHandler){
			this.abstractHandler = abstractHandler;
		}	
		
	};
	
	
	abstract AbstractHandler getAbstractHandler();
	abstract void setAbstractHandler(AbstractHandler abstractHandler);
	
	/**
	 * returns button handler
	 * 
	 * @return
	 */
	public AbstractHandler getHandler() {
		return getAbstractHandler();
	}
	
	/**
	 * set button handler
	 * 
	 * @param abstractHandler
	 */
	public void setHandler(AbstractHandler abstractHandler){
		setAbstractHandler(abstractHandler);
	};
	
}

