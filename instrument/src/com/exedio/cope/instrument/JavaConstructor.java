
package com.exedio.cope.instrument;

import java.io.PrintStream;

/**
 * Represents a constructor of a class parsed by the java parser.
 * @see Injector
 */
public final class JavaConstructor extends JavaBehaviour
{
	/**
	 * The index of the start of the last parameter of the
	 * parameter list in {@link #literal}.
	 * Is the index of the last comma, if there is more than one
	 * parameter or otherwise the index after the opening parent.
	 *
	 * Is initialized to -1.
	 */
	private int last_param_start=-1;
	
	/**
	 * The index of the end of the last parameter of the
	 * parameter list in {@link #literal}.
	 * Is the index of the closing bracket of the parameter list.
	 *
	 * Is initialized to -1.
	 */
	private int last_param_end=-1;
	
	public JavaConstructor(JavaClass parent,
	int modifiers,
	String name)
	throws InjectorParseException
	{
		super(parent, modifiers, null, name);
	}
	
	/**
	 * Sets {@link #last_param_start} to the given value.
	 * @throws RuntimeException if pos is negative.
	 */
	public final void setLastParameterStart(int pos)
	{
		if(pos<0)
			throw new RuntimeException();
		last_param_start=pos;
	}
	
	/**
	 * Sets {@link #last_param_end} to the given value.
	 * @throws RuntimeException if pos is negative.
	 * @throws RuntimeException if called more than once.
	 */
	public final void setLastParameterEnd(int pos)
	{
		if(pos<0)
			throw new RuntimeException();
		if(last_param_end>=0)
			throw new RuntimeException();
		last_param_end=pos;
	}
	
	/**
	 * See Java Language Specification 8.6.3
	 * &quot;Constructor Modifiers&quot;
	 */
	public final int getAllowedModifiers()
	{
		return
		java.lang.reflect.Modifier.PUBLIC |
		java.lang.reflect.Modifier.PROTECTED |
		java.lang.reflect.Modifier.PRIVATE;
	}
	
	public final void printMore(PrintStream o)
	{
		super.printMore(o);
		System.out.println("    ("+last_param_start+'|'+last_param_end+')');
	}
	
}
