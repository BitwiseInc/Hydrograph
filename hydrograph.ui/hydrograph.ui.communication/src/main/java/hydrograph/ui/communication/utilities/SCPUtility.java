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

package hydrograph.ui.communication.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import hydrograph.ui.communication.messages.Message;
import hydrograph.ui.communication.messages.MessageType;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * The class SCPUtility
 * Provides utility methods to copy files using scp
 * 
 * @author Bitwise
 * 
 */
public class SCPUtility {
	private static final String STRICT_HOST_KEY_CHECKING_DEFAULT_VALUE = "no";
	private static final String STRICT_HOST_KEY_CHECKING = "StrictHostKeyChecking";
	private static final String PREFERRED_AUTHENTICATIONS_DEFAULT_VALUES = "publickey,keyboard-interactive,password";
	private static final String PREFERRED_AUTHENTICATIONS = "PreferredAuthentications";
	private static final int SSH_PORT = 22;
	private static final String GENERAL_SUCCESS_MESSAGE = "SUCCESS";
	private static final String GENERAL_ERROR_MESSAGE = "ERROR";
	private static final String AUTHENTICATION_FAILED_MESSAGE = "Invalid username or password";
	private static final String AUTH_FAIL_EXCEPTION = "Auth fail";
	private static final String UNKNOWN_HOST_MESSAGE = "Unknown host";
	private static final String UNKNOWN_HOST_EXCEPTION = "UnknownHostException";
	private static final Logger logger = LogFactory.INSTANCE.getLogger(SCPUtility.class);
	public static SCPUtility INSTANCE = new SCPUtility();
	
	private SCPUtility(){
		
	}
	
	public Message validateCredentials(String host, String user, String password) {

		JSch jsch = new JSch();

		Session session;
		try {
			session = jsch.getSession(user, host, SSH_PORT);

			session.setPassword(password);

			java.util.Properties config = new java.util.Properties();
			config.put(STRICT_HOST_KEY_CHECKING, STRICT_HOST_KEY_CHECKING_DEFAULT_VALUE);
			session.setConfig(config);

			session.setConfig(PREFERRED_AUTHENTICATIONS, PREFERRED_AUTHENTICATIONS_DEFAULT_VALUES);

			session.connect();
			session.disconnect();
		} catch (Exception e) {
			return getErrorMessage(e);
		}

		return new Message(MessageType.SUCCESS, GENERAL_SUCCESS_MESSAGE);
	}

	private Message getErrorMessage(Exception e) {
		if(StringUtils.contains(e.getMessage(), UNKNOWN_HOST_EXCEPTION)){
			return new Message(MessageType.UNKNOWN_HOST, UNKNOWN_HOST_MESSAGE); 
		}else if(StringUtils.contains(e.getMessage(), AUTH_FAIL_EXCEPTION)){
			return new Message(MessageType.INVALID_USERNAME_PASSWORD, AUTHENTICATION_FAILED_MESSAGE); 
		}else{
			return new Message(MessageType.ERROR, GENERAL_ERROR_MESSAGE);
		}
	}
	
	/**
	 * 
	 * Scp file from remote server
	 * 
	 * @param host
	 * @param user
	 * @param password
	 * @param remoteFile
	 * @param localFile
	 * @throws JSchException 
	 * @throws IOException 
	 */
	public void scpFileFromRemoteServer(String host, String user, String password, String remoteFile, String localFile) throws JSchException, IOException {
		
		String prefix = null;
		if (new File(localFile).isDirectory()) {
			prefix = localFile + File.separator;
		}

		JSch jsch = new JSch();
		Session session = jsch.getSession(user, host, 22);

		// username and password will be given via UserInfo interface.
		UserInfo userInfo = new UserInformation(password);
		session.setUserInfo(userInfo);
		session.connect();

		// exec 'scp -f remoteFile' remotely
		String command = "scp -f " + remoteFile;
		Channel channel = session.openChannel("exec");
		((ChannelExec) channel).setCommand(command);

		// get I/O streams for remote scp
		OutputStream out = channel.getOutputStream();
		InputStream in = channel.getInputStream();

		channel.connect();

		byte[] buf = new byte[1024];

		// send '\0'
		buf[0] = 0;
		out.write(buf, 0, 1);
		out.flush();
		
		readRemoteFileAndWriteToLocalFile(localFile, prefix, out, in, buf);

		session.disconnect();		
	}

	private void readRemoteFileAndWriteToLocalFile(String localFile, String prefix,
			OutputStream out, InputStream in, byte[] buf) throws IOException, FileNotFoundException {
		while (true) {
			int c = checkAck(in);
			if (c != 'C') {
				break;
			}

			// read '0644 '
			in.read(buf, 0, 5);

			long filesize = 0L;
			while (true) {
				if (in.read(buf, 0, 1) < 0) {
					// error
					break;
				}
				if (buf[0] == ' ')
					break;
				filesize = filesize * 10L + (long) (buf[0] - '0');
			}

			String file = null;
			for (int i = 0;; i++) {
				in.read(buf, i, 1);
				if (buf[i] == (byte) 0x0a) {
					file = new String(buf, 0, i);
					break;
				}
			}

			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();

			// read a content of local file
			try (FileOutputStream fos = new FileOutputStream(prefix == null ? localFile : prefix + file)){
				int foo;
				while (true) {
					if (buf.length < filesize)
						foo = buf.length;
					else
						foo = (int) filesize;
					foo = in.read(buf, 0, foo);
					if (foo < 0) {
						// error
						break;
					}
					fos.write(buf, 0, foo);
					filesize -= foo;
					if (filesize == 0L)
						break;
				}
			} catch (IOException e) {
				throw e;
			}

			if (checkAck(in) != 0) {
				//System.exit(0);
				return;
			}

			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();
		}
	}

	private int checkAck(InputStream in) throws IOException {
		int b = in.read();
		// b may be 0 for success,
		// 1 for error,
		// 2 for fatal error,
		// -1
		if (b == 0)
			return b;
		if (b == -1)
			return b;

		if (b == 1 || b == 2) {
			StringBuffer sb = new StringBuffer();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
			if (b == 1) { // error
				logger.debug(sb.toString());
			}
			if (b == 2) { // fatal error
				logger.debug(sb.toString());
			}
		}
		return b;
	}

	/**
	 * 
	 * This class store user information who participates in scp
	 * 
	 * @author Bitwise
	 * 
	 */
	private static class UserInformation implements UserInfo {

		String password;

		public UserInformation(String password) {
			this.password = password;
		}

		@Override
		public String getPassphrase() {
			return password;
		}

		@Override
		public String getPassword() {
			return this.password;
		}

		@Override
		public boolean promptPassphrase(String arg0) {
			return true;
		}

		@Override
		public boolean promptPassword(String arg0) {
			return true;
		}

		@Override
		public boolean promptYesNo(String arg0) {
			return true;
		}

		@Override
		public void showMessage(String arg0) {

		}

	}

}
