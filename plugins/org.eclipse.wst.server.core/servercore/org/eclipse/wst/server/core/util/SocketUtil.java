/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Random;

import org.eclipse.wst.server.core.internal.Trace;

import sun.net.spi.nameservice.dns.DNSNameService;
/**
 * A utility class for socket-related function. It's main purposes are to find
 * unused ports, check whether a port is in use, and check whether a given
 * address is a local(host) address.  
 */
public class SocketUtil {
	private static final Random rand = new Random(System.currentTimeMillis());
	
	private static String dnsHostname;

	/**
	 * Finds an unused port between the given from and to values.
	 * 
	 * @param host
	 * @param searchFrom
	 * @param searchTo
	 * @return
	 */
	public static int findUnusedPort(int searchFrom, int searchTo) {
		for (int i = 0; i < 10; i++) {
			int port = getRandomPort(searchFrom, searchTo);
			if (!isPortInUse(port))
				return port;
		}
		return -1;
	}

	/**
	 * Return a random port number in the given range.
	 * 
	 * @param low lowest possible port number
	 * @param high highest possible port number
	 * @return a random port number in the given range
	 */
	private static int getRandomPort(int low, int high) {
		return rand.nextInt(high - low) + low;
	}

	/**
	 * Checks to see if the given port number is being used. 
	 * Returns <code>true</code> if the given port is in use, and <code>false</code>
	 * otherwise. Retries every 500ms for "count" tries.
	 *
	 * @param port the port number to check
	 * @param count the number of times to retry
	 * @return boolean <code>true</code> if the port is in use, and
	 *    <code>false</code> otherwise
	 */
	public static boolean isPortInUse(int port, int count) {
		boolean inUse = isPortInUse(port);
		while (inUse && count > 0) {
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				// ignore
			}
			inUse = isPortInUse(port);
			count --;
		}
	
		return inUse;
	}

	/**
	 * Checks to see if the given port number is being used.
	 * Returns <code>true</code> if the given port is in use, and <code>false</code>
	 * otherwise.
	 *
	 * @param port the port number to check
	 * @return boolean <code>true</code> if the port is in use, and
	 *    <code>false</code> otherwise
	 */
	public static boolean isPortInUse(int port) {
		ServerSocket s = null;
		try {
			s = new ServerSocket(port);
		} catch (SocketException e) {
			return true;
		} catch (IOException e) {
			return true;
		} catch (Exception e) {
			return true;
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}

		return false;
	}

	/**
	 * Checks if the given host (name or IP address) is pointing to the local
	 * machine.
	 * Although this method is not foolproof (especially if the network
	 * configuration of the current machine is incorrect or failing), it will
	 * correctly identify just about all loopback adapters and the local hostname
	 * or IP address.
	 * <p>
	 * This method will not attempt to make an external network connection, so
	 * it returns quickly and is safe to use in UI interfaces.
	 * </p>
	 * 
	 * @param host a hostname or IP address
	 * @return <code>true</code> if the given host is localhost, and
	 *    <code>false</code> otherwise
	 */
	public static boolean isLocalhost(String host) {
		if (host == null)
			return false;
		try {
			if ("localhost".equals(host) || "127.0.0.1".equals(host))
				return true;
			
			InetAddress localHostaddr = InetAddress.getLocalHost();
			if (localHostaddr.getHostName().equals(host) || host.equals(localHostaddr.getCanonicalHostName()))
				return true;
			
			if (localHostaddr.getHostAddress().equals(host))
				return true;
			
			if (dnsHostname == null)
				try {
					DNSNameService dns = new DNSNameService();
					dnsHostname = dns.getHostByAddr(localHostaddr.getAddress());
				} catch (Throwable t) {
					dnsHostname = "*****************";
				}
			
			if (dnsHostname != null && dnsHostname.equals(host))
				return true;
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Error checking for localhost", e);
		}
		return false;
	}
}