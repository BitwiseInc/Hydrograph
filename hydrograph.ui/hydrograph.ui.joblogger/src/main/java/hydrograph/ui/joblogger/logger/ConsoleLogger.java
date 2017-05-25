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

 
package hydrograph.ui.joblogger.logger;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.internal.console.ConsoleView;
import org.slf4j.Logger;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.logging.factory.LogFactory;


/**
 * The Class ConsoleLogger.
 * <p>
 * Class to create Console logger
 * 
 * @author Bitwise
 */
public class ConsoleLogger extends AbstractJobLogger{

	private static final int DEFAULT_CONSOLE_HIGH_WATER_MARK_OFFSET = 8000;

	private static final Logger logger = LogFactory.INSTANCE.getLogger(ConsoleLogger.class);
	
	private MessageConsoleStream messageConsoleStream;
	
	public ConsoleLogger(String projectName, String jobName) {
		super(projectName, jobName);
		initConsoleStream();
		logger.debug("Created console logger stream");
	}
	
	@Override
	public void log(String message) {
		if(messageConsoleStream!=null && !messageConsoleStream.isClosed())
			messageConsoleStream.println(message);
		logger.debug("logged message on console - message - {}" , message );
	}
	
	/**
	 * Find console using name if exist or create new.
	 *
	 * @param name            the console name
	 * @return console
	 */
	private void initConsoleStream() {
		MessageConsole messageConsole = getMessageConsole();
		messageConsole.clearConsole();
		messageConsoleStream=messageConsole.newMessageStream();
		logger.debug("Created message console stream");
		messageConsoleStream.getConsole().addPropertyChangeListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				((ConsoleView)event.getSource()).setScrollLock(true);
			}
		});
		
	}

	/**
	 * get message console object
	 * 
	 * @param messageConsole
	 * @return
	 */
	private MessageConsole getMessageConsole() {
		IConsoleManager conMan = getConsoleManager();	
		MessageConsole messageConsole = getConsoleFromExistingConsoles(conMan);		
		if(messageConsole == null){
			messageConsole = createNewMessageConsole(conMan);
			logger.debug("No existing console found, created new one");
		}
		
		int lowWaterMark = getConsoleBufferSize();
		messageConsole.setWaterMarks(lowWaterMark, lowWaterMark + DEFAULT_CONSOLE_HIGH_WATER_MARK_OFFSET);
		return messageConsole;
	}

	/**
	 * 
	 * returns gradle console if it is registered with console manager exist
	 * 
	 * @param conMan - console manager
	 * @return
	 */
	private MessageConsole getConsoleFromExistingConsoles(IConsoleManager conMan) {
		IConsole[] existing = getExistingConsoles(conMan);		
		MessageConsole messageConsole = getExistingMessageConsole(existing);
		return messageConsole;
	}

	
	/**
	 * 
	 * Creates new message console and register it with console manager
	 * 
	 * @param conMan
	 * @return
	 */
	private MessageConsole createNewMessageConsole(IConsoleManager conMan) {
		MessageConsole messageConsole;
		messageConsole = new MessageConsole(getFullJobName(), null);
		conMan.addConsoles(new IConsole[] { messageConsole });
		logger.debug("Created message console");
		
		return messageConsole;
	}

	private int getConsoleBufferSize(){
		String bufferSize = Platform.getPreferencesService().getString(
				Constants.GRAPH_PLUGIN_QUALIFIER,
				Constants.CONSOLE_BUFFER_SIZE_PREFERANCE_NAME,
				Constants.DEFUALT_CONSOLE_BUFFER_SIZE, null);

		return Integer.parseInt(bufferSize);
		
	}
	
	/**
	 * 
	 * returns Gradle console if it is registered with console manager 
	 * 
	 * @param existing
	 * @return - MessageConsole
	 */
	private MessageConsole getExistingMessageConsole(IConsole[] existing) {
		MessageConsole messageConsole=null;
		for (int i = 0; i < existing.length; i++) {
			if (getFullJobName().equals(existing[i].getName())){
				messageConsole=(MessageConsole) existing[i];
				logger.debug("We have a message console");
				break;
			}	
		}
		return messageConsole;
	}

	
	/**
	 * 
	 * get all consoles registered with default console manager
	 * 
	 * @param conMan
	 * @return - IConsole[]
	 */
	private IConsole[] getExistingConsoles(IConsoleManager conMan) {
		IConsole[] existing = conMan.getConsoles();
		logger.debug("Retrieved existing consoles. Number of console - {}" , existing.length);
		return existing;
	}

	/**
	 * 
	 *  get default console manager
	 * 
	 * @return ConsoleManager
	 */
	private IConsoleManager getConsoleManager() {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		logger.debug("Retrieved Console plugin object");
		IConsoleManager conMan = plugin.getConsoleManager();
		logger.debug("Retrieved Console manager");
		return conMan;
	}

	@Override
	public void close() {
		// TODO - need to re-implement once actual job kill process is in place
		// need input from engine
		/*try {
			messageConsoleStream.close();
		} catch (IOException e) {
			logger.debug("Unable to close message console stream " + e.getMessage());
		}*/
	}

	@Override
	public void logWithNoTimeStamp(String message) {
		messageConsoleStream.println(message);
		
	}	
}
