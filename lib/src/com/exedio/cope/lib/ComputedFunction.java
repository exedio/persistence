
package com.exedio.cope.lib;

import java.util.List;
import java.util.Collections;
import java.util.Arrays;

public abstract class ComputedFunction implements Function 
{
	private final Function[] sources;
	final Function mainSource;
	private final List sourceList;
	private final String[] sqlFragments;
	private final String functionName;

	public ComputedFunction(final Function[] sources,
									final Function mainSource,
									final String[] sqlFragments,
									final String functionName)
	{
		this.sources = sources;
		this.mainSource = mainSource;
		this.sourceList = Collections.unmodifiableList(Arrays.asList(sources));
		this.sqlFragments = sqlFragments;
		if(sources.length+1!=sqlFragments.length)
			throw new RuntimeException("length "+sources.length+" "+sqlFragments.length);
		this.functionName = functionName;
	}
	
	final List getSourceAttributes()
	{
		return sourceList;
	}

	public abstract Object mapJava(Object[] sourceValues);

	public final void append(final Statement bf)
	{
		for(int i = 0; i<sources.length; i++)
		{
			bf.append(sqlFragments[i]).
				append(sources[i]);
		}
		bf.append(sqlFragments[sqlFragments.length-1]);
	}

	public final String toString()
	{
		final StringBuffer buf = new StringBuffer(functionName);
		buf.append('(');
		for(int i = 0; i<sources.length; i++)
		{
			if(i>0)
				buf.append(',');
			buf.append(sources[i].getName());
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
