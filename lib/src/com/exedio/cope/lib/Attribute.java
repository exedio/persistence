package com.exedio.cope.lib;

import com.exedio.cope.lib.Database;
import java.util.Collections;
import java.util.List;

public abstract class Attribute
{
	public final AttributeMapping mapping;

	protected Attribute()
	{
		this.mapping = null;
	}
	
	protected Attribute(final AttributeMapping mapping)
	{
		this.mapping = mapping;
	}
	

	// phase 1: initialize ---------------------------------------------
	private String name;
	private boolean readOnly;
	private boolean notNull;

	public final Attribute initialize(final String name, final boolean readOnly, final boolean notNull)
	{
		if(name==null)
			throw new RuntimeException();

		if(this.name!=null)
			throw new RuntimeException();
		if(this.type!=null)
			throw new RuntimeException();

		this.name = name;
		this.readOnly = readOnly;
		this.notNull = notNull;

		return this;
	}
	
	public final String getName()
	{
		if(this.name==null)
			throw new RuntimeException();

		return name;
	}
	
	public final boolean isReadOnly()
	{
		if(this.name==null)
			throw new RuntimeException();

		return readOnly;
	}
	
	public final boolean isNotNull()
	{
		if(this.name==null)
			throw new RuntimeException();

		return notNull;
	}
	
	// phase 2: setType ---------------------------------------------------

	private Type type;
	private List columns;
	private Column mainColumn;
	
	void setType(final Type type)
	{
		if(type==null)
			throw new RuntimeException();

		if(this.name==null)
			throw new RuntimeException();
		if(this.type!=null)
			throw new RuntimeException();

		this.type = type;
		this.columns =
			(mapping==null) ?
				Collections.unmodifiableList(createColumns(name, notNull)) :
				Collections.EMPTY_LIST;
		this.mainColumn = this.columns.isEmpty() ? null : (Column)columns.iterator().next();
	}
	
	public final Type getType()
	{
		if(this.type==null)
			throw new RuntimeException();

		return type;
	}
	
	final List getColumns()
	{
		if(this.type==null)
			throw new RuntimeException();

		return columns;
	}
	
	final Column getMainColumn()
	{
		if(this.type==null)
			throw new RuntimeException();

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


