package com.exedio.cope.lib;

import java.util.Collections;
import java.util.List;

public abstract class Attribute
{
	private final boolean readOnly;
	private final boolean notNull;
	public final AttributeMapping mapping;

	protected Attribute(final Item.Option option)
	{
		this(option, null);
	}
	
	protected Attribute(final Item.Option option, final AttributeMapping mapping)
	{
		this.readOnly = option.readOnly;
		this.notNull = option.notNull;
		this.mapping = mapping;
	}
	
	public final boolean isReadOnly()
	{
		return readOnly;
	}
	
	public final boolean isNotNull()
	{
		return notNull;
	}
	
	// phase 2: setType ---------------------------------------------------

	private Type type;
	private String name;
	private List columns;
	private Column mainColumn;
	
	final void setType(final Type type, final String name)
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
	
	public final String getName()
	{
		if(this.type==null)
			throw new RuntimeException();

		return name;
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


