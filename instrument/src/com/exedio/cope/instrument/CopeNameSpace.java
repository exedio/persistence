/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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
import bsh.UtilEvalError;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Override default imports of standard NameSpace,
 * to make it conform to a javac compiler run.
 *
 * @author Ralf Wiebicke
 */
class CopeNameSpace extends NameSpace
{
	private static final long serialVersionUID = 1l;

	private static final boolean debug = false;
	private static int depth = 0;

	CopeNameSpace(final CopeNameSpace parent, final String name)
	{
		super(parent, name);
	}

	@SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
	private void debugStart(final String message)
	{
		debug(message+" in "+toString());
		depth++;
	}

	@SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
	private void debugEnd(final String message)
	{
		depth--;
		debug(message);
	}

	@SuppressWarnings("static-method")
	private void debug(final String message)
	{
		for (int i=0; i<depth; i++) System.out.print("  ");
		System.out.println(message);
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

	@Override
	public final Object get(final String name, final bsh.Interpreter interpreter) throws UtilEvalError
	{
		if (debug) debugStart("+++ get(\""+name+"\",Interpreter)");
		return report(super.get(name, interpreter));
	}

	@Override
	public final Class<?> getClass(final String name) throws UtilEvalError
	{
		if (debug) debugStart("+++ getClass(\""+name+"\")");
		return report(getClassInternal(name));
	}

	Class<?> getClassInternal(final String name) throws UtilEvalError
	{
		return super.getClass(name);
	}

	@Override
	public final Object getVariable(final String name) throws UtilEvalError
	{
		if (debug) debugStart("+++ getVariable(\""+name+"\")");
		return report(getVariableInternal(name));
	}

	Object getVariableInternal(final String name) throws UtilEvalError
	{
		return super.getVariable(name);
	}

	@Override
	public final Object getVariable(final String name, final boolean recurse) throws UtilEvalError
	{
		if (debug) debugStart("+++ getVariable(\""+name+"\","+recurse+")");
		return report(super.getVariable(name, recurse));
	}

	@Override
	protected final bsh.Variable getVariableImpl(final String name, final boolean recurse) throws UtilEvalError
	{
		if (debug) debugStart("+++ getVariableImpl(\""+name+"\","+recurse+")");
		return report(super.getVariableImpl(name, recurse));
	}

	@Override
	protected final bsh.Variable getImportedVar(final String name) throws UtilEvalError
	{
		if (debug) debugStart("+++ getImportedVar(\""+name+"\")");
		return report(super.getImportedVar(name));
	}

	@Override
	public final String[] getVariableNames()
	{
		if (debug) debugStart("+++ getVariableNames()");
		return report(super.getVariableNames());
	}

	@Override
	public final bsh.Variable[] getDeclaredVariables()
	{
		if (debug) debugStart("+++ getDeclaredVariables()");
		return report(super.getDeclaredVariables());
	}

	private <X> X report(final X x)
	{
		if (debug) debugEnd("+++    result " + x);
		return x;
	}
}
