
package com.exedio.cope.lib;

import java.util.List;
import java.util.Collections;
import java.util.Arrays;

public abstract class ComputedFunction implements Function 
{
	private final Function[] sourceAttributes; // TODO rename field
	final Function mainSourceAttribute; // TODO rename field
	private final List sourceAttributeList; // TODO rename field
	private final String[] sqlFragments;
	private final String functionName;

	public ComputedFunction(final Function[] sourceAttributes, // TODO rename argument
									final Function mainSourceAttribute, // TODO rename argument
									final String[] sqlFragments,
									final String functionName)
	{
		this.sourceAttributes = sourceAttributes;
		this.mainSourceAttribute = mainSourceAttribute;
		this.sourceAttributeList = Collections.unmodifiableList(Arrays.asList(sourceAttributes));
		this.sqlFragments = sqlFragments;
		if(sourceAttributes.length+1!=sqlFragments.length)
			throw new RuntimeException("length "+sourceAttributes.length+" "+sqlFragments.length);
		this.functionName = functionName;
	}
	
	final List getSourceAttributes()
	{
		return sourceAttributeList;
	}

	public abstract Object mapJava(Object[] sourceValues);

	public final void append(final Statement bf)
	{
		for(int i = 0; i<sourceAttributes.length; i++)
		{
			bf.append(sqlFragments[i]).
				append(sourceAttributes[i]);
		}
		bf.append(sqlFragments[sqlFragments.length-1]);
	}

	public final String toString()
	{
		final StringBuffer buf = new StringBuffer(functionName);
		buf.append('(');
		for(int i = 0; i<sourceAttributes.length; i++)
		{
			if(i>0)
				buf.append(',');
			buf.append(sourceAttributes[i].getName());
		}
		buf.append(')');
		
		return buf.toString();
	}

	// second initialization phase ---------------------------------------------------

	private Type type;
	private String name;
	
	final void initialize(final Type type, final String name)
	{
		if(type==null)
			throw new RuntimeException();
		if(name==null)
			throw new RuntimeException();

		if(this.type!=null)
			throw new RuntimeException();
		if(this.name!=null)
			throw new RuntimeException();

		this.type = type;
		this.name = name;
	}
	
	public final Type getType()
	{
		if(this.type==null)
			throw new RuntimeException();

		return type;
	}
	
	public final String getName()
	{
		if(this.type==null)
			throw new RuntimeException();

		return name;
	}
	
}
