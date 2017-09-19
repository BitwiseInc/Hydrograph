/********************************************************************************
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
 ******************************************************************************/
package hydrograph.ui.datastructure.property;

import hydrograph.ui.common.cloneableinterface.IDataStructure;

/**
 * The Class FTPProtocolDetails persists FTP protocol details
 * @author Bitwise
 *
 */
public class FTPProtocolDetails implements IDataStructure{
	private String protocol = "FTP";
	private String host;
	private String port;

	public FTPProtocolDetails(String protocol, String host, String port) {
		this.protocol = protocol;
		this.host = host;
		this.port = port;
	}

	/**
	 * @return protocol
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * @param protocol
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * @return host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * @param port
	 */
	public void setPort(String port) {
		this.port = port;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((port == null) ? 0 : port.hashCode());
		result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
		return result;
	}
	
	@Override
	public FTPProtocolDetails clone() {
		FTPProtocolDetails ftpProtocolDetails = new FTPProtocolDetails(getProtocol(), 
				getHost(), getPort());
		return ftpProtocolDetails;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FTPProtocolDetails other = (FTPProtocolDetails) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (port == null) {
			if (other.port != null)
				return false;
		} else if (!port.equals(other.port))
			return false;
		if (protocol == null) {
			if (other.protocol != null)
				return false;
		} else if (!protocol.equals(other.protocol))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FTPProtocolDetails [protocol=" + protocol + ", host=" + host + ", port=" + port + "]";
	}
	
}
