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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.wst.server.core.*;
/**
 * An implementation of the org.eclipse.wst.server.core.ITaskModel interface
 * that provides a default implementation of the methods. 
 */
public class TaskModel implements ITaskModel {
	private Map map = new HashMap();

	/*
	 * @see org.eclipse.wst.server.core.ITaskModel.getObject(String)
	 */
	public Object getObject(String id) {
		try {
			return map.get(id);
		} catch (Exception e) {
			return null;
		}
	}

	/*
	 * @see org.eclipse.wst.server.core.ITaskModel.putObject(String, Object)
	 */
	public void putObject(String id, Object obj) {
		map.put(id, obj);
	}
}