/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
�*
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.IModule;
/**
 * Tomcat 41 handler.
 */
public class Tomcat41Handler implements ITomcatVersionHandler {
	/**
	 * @see ITomcatVersionHandler#verifyInstallPath(IPath)
	 */
	public boolean verifyInstallPath(IPath installPath) {
		if (installPath == null)
			return false;

		if (!TomcatPlugin.verifyTomcatVersionFromPath(installPath, TomcatPlugin.TOMCAT_41))
			return false;
		return TomcatPlugin.verifyInstallPath(installPath, TomcatPlugin.TOMCAT_41);
	}
	
	/**
	 * @see ITomcatVersionHandler#getRuntimeClass()
	 */
	public String getRuntimeClass() {
		return "org.apache.catalina.startup.Bootstrap";
	}
	
	/**
	 * @see ITomcatVersionHandler#getRuntimeClasspath(IPath)
	 */
	public List getRuntimeClasspath(IPath installPath) {
		List cp = new ArrayList();
		
		// 4.1 - add bootstrap.jar from the Tomcat bin directory
		IPath binPath = installPath.append("bin");
		if (binPath.toFile().exists()) {
			IPath path = binPath.append("bootstrap.jar");
			cp.add(JavaRuntime.newArchiveRuntimeClasspathEntry(path));
		}
		
		return cp;
	}

	/**
	 * @see ITomcatVersionHandler#getRuntimeProgramArguments(IPath, boolean, boolean)
	 */
	public String[] getRuntimeProgramArguments(IPath configPath, boolean debug, boolean starting) {
		List list = new ArrayList();
		
		if (debug)
			list.add("-debug");
		
		if (starting)
			list.add("start");
		else
			list.add("stop");
		
		String[] temp = new String[list.size()];
		list.toArray(temp);
		return temp;
	}

	/**
	 * @see ITomcatVersionHandler#getRuntimeVMArguments(IPath, IPath, boolean)
	 */
	public String[] getRuntimeVMArguments(IPath installPath, IPath configPath, boolean isTestEnv) {
		List list = new ArrayList();
		if (isTestEnv)
			list.add("-Dcatalina.base=\"" + configPath.toOSString() + "\"");
		else 
			list.add("-Dcatalina.base=\"" + installPath.toOSString() + "\"");
		list.add("-Dcatalina.home=\"" + installPath.toOSString() + "\"");
		list.add("-Djava.endorsed.dirs=\"" + installPath.append("common").append("endorsed").toOSString() + "\"");
		
		String[] s = new String[list.size()];
		list.toArray(s);
		return s;
	}

	public String getRuntimePolicyFile(IPath configPath) {
		return configPath.append("conf").append("catalina.policy").toOSString();
	}

	/**
	 * @see ITomcatVersionHandler#canAddModule(IModule)
	 */
	public IStatus canAddModule(IModule module) {
		String version = module.getModuleType().getVersion();
		if ("2.2".equals(version) || "2.3".equals(version))
			return Status.OK_STATUS;
		
		return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, Messages.errorSpec41, null);
	}

	/**
	 * @see ITomcatVersionHandler#getRuntimeBaseDirectory(TomcatServerBehaviour)
	 */
	public IPath getRuntimeBaseDirectory(TomcatServerBehaviour serverBehaviour) {
		if (serverBehaviour.getTomcatServer().isTestEnvironment())
			return serverBehaviour.getTempDirectory();
		return serverBehaviour.getServer().getRuntime().getLocation();
	}
}