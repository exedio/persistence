/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope.instrument;

import bsh.NameSpace;

/**
 * Override default imports of standard NameSpace,
 * to make it conform to a javac compiler run.
 *
 * @author Ralf Wiebicke
 */
class CopeNameSpace extends NameSpace
{
	private static final long serialVersionUID = 1l;
	
	CopeNameSpace(final CopeNameSpace parent)
	{
		super(parent, "zack");
	}
	
	/**
	 * Override default imports of standard NameSpace,
	 * to make it conform to a javac compiler run.
	 * Implements Java Language Specification 7.5.3. "Automatic Imports".
	 */
	@Override
	public final void loadDefaultImports()
	{
		importPackage("java.lang");
	}

	/*public Object get(final String name, final Interpreter interpreter) throws UtilEvalError
	{
		System.out.println("++++++++++++++++get(\""+name+"\",Interpreter)");
		return super.get(name, interpreter);
	}
	
	public Class getClass(final String name) throws UtilEvalError
	{
		System.out.println("++++++++++++++++getClass(\""+name+"\")");
		return super.getClass(name);
	}
	
	public Object getVariable(final String name) throws UtilEvalError
	{
		System.out.println("++++++++++++++++getVariable(\""+name+"\")");
		return super.getVariable(name);
	}
	
	public Object getVariable(final String name, final boolean recurse) throws UtilEvalError
	{
		System.out.println("++++++++++++++++getVariable(\""+name+"\","+recurse+")");
		return super.getVariable(name, recurse);
	}
	
	protected Variable getVariableImpl(final String name, final boolean recurse) throws UtilEvalError
	{
		System.out.println("++++++++++++++++getVariableImpl(\""+name+"\","+recurse+")");
		return super.getVariableImpl(name, recurse);
	}
	
	protected Variable getImportedVar(final String name) throws bsh.UtilEvalError
	{
		System.out.println("++++++++++++++++getImportedVar(\""+name+"\")");
		return super.getImportedVar(name);
	}
	
	public String[] getVariableNames()
	{
		System.out.println("++++++++++++++++getVariableNames()");
		return super.getVariableNames();
	}
	
	public Variable[] getDeclaredVariables()
	{
		System.out.println("++++++++++++++++getDeclaredVariables()");
		return super.getDeclaredVariables();
	}*/
}
