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
package org.eclipse.wst.server.ui.editor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wst.server.core.IServerWorkingCopy;
/**
 * 
 * 
 * @since 1.0
 */
public abstract class ServerEditorSection implements IServerEditorSection {
	private String errorMessage = null;

	/**
	 * The server working copy.
	 */
	public IServerWorkingCopy server;

	/**
	 * The command manager.
	 */
	public ICommandManager commandManager;

	protected boolean readOnly;
	protected Composite parentComp;
	protected ServerEditorPart editor;

	/**
	 * @see org.eclipse.wst.server.ui.editor.IServerEditorSection#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input) {
		if (input instanceof IServerEditorPartInput) {
			IServerEditorPartInput sepi = (IServerEditorPartInput) input;
			server = sepi.getServer();
			commandManager = sepi.getServerCommandManager();
			readOnly = sepi.isServerReadOnly();
		}
	}

	/**
	 * Create the section.
	 * 
	 * @param parent the parent composite
	 */
	public void createSection(Composite parent) {
		this.parentComp = parent;
	}

	/**
	 * Return the shell of the section.
	 * 
	 * @return the shell
	 */
	public Shell getShell() {
		return parentComp.getShell();
	}

	/**
	 * Return the error message for this page.
	 * 
	 * @return the error message
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Returns error or status messages that will be displayed when the
	 * server resource is saved. If there are any error messages, the
	 * user will be unable to save the editor.
	 * 
	 * @return org.eclipse.core.runtime.IStatus
	 */
	public IStatus[] getSaveStatus() {
		return null;
	}

	/**
	 * Set the editor part that this section belongs to.
	 * 
	 * @param editor the editor
	 */
	public void setServerEditorPart(ServerEditorPart editor) {
		this.editor = editor;
	}

	/**
	 * Set an error message for this page.
	 * 
	 * @param error java.lang.String
	 */
	public void setErrorMessage(String error) {
		if (error == null && errorMessage == null)
			return;
		
		if (error != null && error.equals(errorMessage))
			return;
		
		errorMessage = error;
		if (editor != null)
			editor.updateErrorMessage();
	}

	/**
	 * Get a form toolkit to create widgets. It will automatically be disposed
	 * when the editor is disposed.
	 * 
	 * @param display
	 * @return FormToolkit
	 */
	public FormToolkit getFormToolkit(Display display) {
		return editor.getFormToolkit(display);
	}

	/**
	 * @see IServerEditorSection#dispose()
	 */
	public void dispose() {
		// ignore
	}
}