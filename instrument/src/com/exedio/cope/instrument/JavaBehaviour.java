
package com.exedio.cope.instrument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a behavioral feature of a class parsed by the java parser.
 * May be either a method or a constructor.
 * @see Injector
 */
abstract class JavaBehaviour extends JavaFeature
{
	
	/**
	 * Contains subsequently parameter types and names.
	 */
	protected final ArrayList parameters=new ArrayList();
	
	protected final List unmodifiableParameters=Collections.unmodifiableList(parameters);
	
	/**
	 * Contains all names given in the &quot;throws&quot; clause.
	 */
	private final ArrayList throwables=new ArrayList();
	
	JavaBehaviour(
						final JavaClass parent,
						final int modifiers,
						final String type,
						final String name)
	throws InjectorParseException
	{
		// parent must not be null
		super(parent.file, parent, modifiers, type, name);
	}
	
	void addParameter(String paramtype, String paramname)
	{
		parameters.add(paramtype);
		parameters.add(paramname);
	}
	
	final List getParameters()
	{
		return unmodifiableParameters;
	}
	
	final void addThrowable(String throwable)
	{
		throwables.add(throwable);
	}
	
}
