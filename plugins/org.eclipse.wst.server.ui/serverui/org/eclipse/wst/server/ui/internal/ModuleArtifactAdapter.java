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
package org.eclipse.wst.server.ui.internal;

import org.eclipse.core.expressions.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
/**
 * 
 */
public class ModuleArtifactAdapter {
	private IConfigurationElement element;
	private Expression fContextualLaunchExpr = null;

	/**
	 * ModuleArtifactAdapter constructor comment.
	 */
	public ModuleArtifactAdapter(IConfigurationElement element) {
		super();
		this.element = element;
	}

	/**
	 * Returns the id of this ModuleArtifactAdapter.
	 *
	 * @return java.lang.String
	 */
	public String getId() {
		return element.getAttribute("id");
	}

	/**
	 * Returns true if the plugin that loaded this class has been loaded.
	 *
	 * @return boolean
	 */
	public boolean isPluginActivated() {
		String pluginId = element.getDeclaringExtension().getNamespace();
		return Platform.getBundle(pluginId).getState() == Bundle.ACTIVE;
	}
	
	/**
	 * Returns an expression that represents the enablement logic for the
	 * contextual launch element of this launch shortcut description or
	 * <code>null</code> if none.
	 * @return an evaluatable expression or <code>null</code>
	 * @throws CoreException if the configuration element can't be
	 *  converted. Reasons include: (a) no handler is available to
	 *  cope with a certain configuration element or (b) the XML
	 *  expression tree is malformed.
	 */
	public Expression getContextualLaunchEnablementExpression() throws CoreException {
		if (fContextualLaunchExpr == null) {
			IConfigurationElement[] elements = element.getChildren(ExpressionTagNames.ENABLEMENT);
			IConfigurationElement enablement = elements.length > 0 ? elements[0] : null; 

			if (enablement != null)
				fContextualLaunchExpr = ExpressionConverter.getDefault().perform(enablement);
		}
		return fContextualLaunchExpr;
	}
	
	/**
	 * Evaluate the given expression within the given context and return
	 * the result. Returns <code>true</code> iff result is either TRUE or NOT_LOADED.
	 * This allows optimistic inclusion of shortcuts before plugins are loaded.
	 * Returns <code>false</code> if exp is <code>null</code>.
	 * 
	 * @param exp the enablement expression to evaluate or <code>null</code>
	 * @param context the context of the evaluation. Usually, the
	 *  user's selection.
	 * @return the result of evaluating the expression
	 * @throws CoreException
	 */
	protected boolean evalEnablementExpression(IEvaluationContext context, Expression exp) throws CoreException {
		return (exp != null) ? ((exp.evaluate(context)) != EvaluationResult.FALSE) : false;
	}
	
	/**
	 * @return an Evaluation context with default variable = selection
	 */
	/*protected IEvaluationContext createContext(Object obj) {
		List list = null;
		IWorkbenchWindow window = DebugUIPlugin.getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
			    IWorkbenchPart activePart = page.getActivePart();
			    if (activePart instanceof IEditorPart) {
			        list = new ArrayList();
			        list.add(((IEditorPart)activePart).getEditorInput());
			    } else if (activePart != null) {
			        IWorkbenchPartSite site = activePart.getSite();
			        if (site != null) {
	                    ISelectionProvider selectionProvider = site.getSelectionProvider();
	                    if (selectionProvider != null) {
	                        ISelection selection = selectionProvider.getSelection();
					        if (selection instanceof IStructuredSelection) {
					            list = ((IStructuredSelection)selection).toList();
					        }
	                    }
			        }
			    }
			}
		}
		// create a default evaluation context with default variable
		// of the user selection or editor input
		if (list == null) {
		    list = Collections.EMPTY_LIST;
		}
		IEvaluationContext context = new EvaluationContext(null, list);
		context.addVariable("selection", list); //$NON-NLS-1$
		
		return context;
	}*/
	
	public boolean isEnabled(Object obj) throws CoreException {
		IEvaluationContext context = new EvaluationContext(null, obj);
		context.addVariable("selection", obj);
		return evalEnablementExpression(context, getContextualLaunchEnablementExpression());
	}
	
	/**
	 * Return a string representation of this object.
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		return "ModuleArtifactAdapter[" + getId() + "]";
	}
}