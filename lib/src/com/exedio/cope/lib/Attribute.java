package com.exedio.cope.lib;

import com.exedio.cope.lib.Database;
import java.util.Collections;
import java.util.List;

public abstract class Attribute
{
	public final AttributeMapping mapping;

	public Attribute()
	{
		this.mapping = null;
	}
	
	public Attribute(final AttributeMapping mapping)
	{
		this.mapping = mapping;
	}
	

	private boolean initialized = false;

	private Type type;
	private String name;
	private boolean readOnly;
	private boolean notNull;
	private List columns;
	private Column mainColumn;
	
	void setType(final Type type)
	{
		if(initialized)
			throw new RuntimeException();

		this.type = type;
	}
	
	public void initialize(final String name, final boolean readOnly, final boolean notNull)
	{
		if(initialized)
			throw new RuntimeException();

		this.name = name;
		this.readOnly = readOnly;
		this.notNull = notNull;
		this.columns =
			(mapping==null) ?
				Collections.unmodifiableList(createColumns(name, notNull)) :
				Collections.EMPTY_LIST;
		this.mainColumn = this.columns.isEmpty() ? null : (Column)columns.iterator().next();

		initialized = true;
	}
	
	public final Type getType()
	{
		if(type==null)
			throw new RuntimeException();

		return type;
	}
	
	public final String getName()
	{
		if(!initialized)
			throw new RuntimeException();

		return name;
	}
	
	public final boolean isReadOnly()
	{
		if(!initialized)
			throw new RuntimeException();

		return readOnly;
	}
	
	public final boolean isNotNull()
	{
		if(!initialized)
			throw new RuntimeException();

		return notNull;
	}
	
	final List getColumns()
	{
		if(!initialized)
			throw new RuntimeException();

		return columns;
	}
	
	final Column getMainColumn()
	{
		return mainColumn;
	}
	
	public final String toString()
	{
		// should be precomputed
		final StringBuffer buf = new StringBuffer();
		buf.append(name);
		buf.append('{');
		boolean first = true;
		if(readOnly)
		{
			if(first)
				first = false;
			else
				buf.append(',');
			buf.append("read-only");
		}
		if(notNull)
		{
			if(first)
				first = false;
			else
				buf.append(',');
			buf.append("not-null");
		}
		buf.append('}');
		if(mapping!=null)
		{
			buf.append('=');
			buf.append(mapping.toString());
		}
		return buf.toString();
	}
	
	protected abstract List createColumns(String name, boolean notNull);
	
	abstract Object cacheToSurface(Object cache);
	abstract Object surfaceToCache(Object surface);
	
}


