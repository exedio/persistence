
package com.exedio.cope.instrument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a behavioral feature of a class parsed by the java parser.
 * May be either a method or a constructor.
 * @see Injector
 */
public abstract class JavaBehaviour extends JavaFeature
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
	
	public JavaBehaviour(JavaClass parent,
	int modifiers,
	String type,
	String name)
	throws InjectorParseException
	{
		// parent must not be null
		super(parent.file, parent, modifiers, type, name);
	}
	
	public void addParameter(String paramtype, String paramname)
	{
		parameters.add(paramtype);
		parameters.add(paramname);
	}
	
	public final List getParameters()
	{
		return unmodifiableParameters;
	}
	
	public final void addThrowable(String throwable)
	{
		throwables.add(throwable);
	}
	
	public final Iterator getThrowables()
	{
		return throwables.iterator();
	}
	
}
