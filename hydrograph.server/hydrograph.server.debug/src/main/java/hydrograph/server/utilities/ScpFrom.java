/*******************************************************************************
 *  Copyright 2017 Capital One Services, LLC and Bitwise, Inc.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *******************************************************************************/
package hydrograph.server.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bitwise
 *
 */
public class ScpFrom {

	private static final Logger LOG = LoggerFactory.getLogger(ScpFrom.class);

	/**
	 * @param host
	 * @param user
	 * @param pwd
	 * @param remoteFile
	 */
	public void deleteFile(String host, String user, String pwd,
			String remoteFile) {
		try {
			JSch ssh = new JSch();
			Session session = ssh.getSession(user, host, 22);

			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(pwd);

			session.connect();
			Channel channel = session.openChannel("exec");
			channel.connect();

			String command = "rm -rf " + remoteFile;
			System.out.println("command: " + command);
			// ((ChannelExec) channel).setCommand(command);

			channel.disconnect();
			session.disconnect();
		} catch (JSchException e) {
			System.out.println(e.getMessage().toString());
			e.printStackTrace();
		}
	}

	/**
	 * @param host
	 * @param user
	 * @param pwd
	 * @param remoteFile
	 * @param localFile
	 * @return
	 */
	public String scpFileFromRemoteServer(String host, String user,
			String pwd, String remoteFile, String localFile) {
		FileOutputStream fos = null;
        OutputStream out = null;
        InputStream in = null;
		try {

			String prefix = null;
			if (new File(localFile).isDirectory()) {
				prefix = localFile + File.separator;
			}

			JSch jsch = new JSch();
			Session session = jsch.getSession(user, host, 22);

			// username and password will be given via UserInfo interface.
			UserInfo userInfo = new MyUserInfo(pwd);
			session.setUserInfo(userInfo);
			session.connect();

			// exec 'scp -f remoteFile' remotely
			String command = "scp -f " + remoteFile;
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			// get I/O streams for remote scp
			out = channel.getOutputStream();
			in = channel.getInputStream();

			channel.connect();

			byte[] buf = new byte[1024];

			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();

			fos = readRemoteFileAndWriteToLocalFile(localFile, fos, prefix,
					out, in, buf);

			channel.disconnect();
			session.disconnect();
		} catch (Exception e) {
		    LOG.error("Error in scpFileFromRemoteServer method "+e.getMessage());
			return "error";
		}
		finally {
            try {
                ServiceUtilities.safeOutputStreamClose(fos);
            } catch (IOException e) {
               LOG.error(e.getMessage());
            }
            try{
                ServiceUtilities.safeOutputStreamClose(out);
            }catch (IOException e) {
                LOG.error(e.getMessage());
            }
            try{
                ServiceUtilities.safeInputStreamClose(in);
            }catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }
        return "success";
	}

	/**
	 * @param localFile
	 * @param fos
	 * @param prefix
	 * @param out
	 * @param in
	 * @param buf
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private FileOutputStream readRemoteFileAndWriteToLocalFile(
			String localFile, FileOutputStream fos, String prefix,
			OutputStream out, InputStream in, byte[] buf) throws IOException,
			FileNotFoundException {
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

			// System.out.println("filesize="+filesize+", file="+file);

			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();

			// read a content of local file
			fos = new FileOutputStream(prefix == null ? localFile : prefix
					+ file);
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
			fos.close();
			fos = null;

			if (checkAck(in) != 0) {
				java.util.Date datex = new java.util.Date();
				System.out
						.println("+++ End: " + new Timestamp(datex.getTime()));
				System.exit(0);
			}

			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();
		}
		return fos;
	}

	/**
	 * @param in
	 * @return
	 * @throws IOException
	 */
	static int checkAck(InputStream in) throws IOException {
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
				System.out.print(sb.toString());
			}
			if (b == 2) { // fatal error
				System.out.print(sb.toString());
			}
		}
		return b;
	}

	/**
	 * @author bhaveshs
	 *
	 */
	public static class MyUserInfo implements UserInfo {

		String pwd;

		public MyUserInfo(String pwd) {
			this.pwd = pwd;
		}

		@Override
		public String getPassphrase() {
			return pwd;
		}

		@Override
		public String getPassword() {
			return this.pwd;
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
