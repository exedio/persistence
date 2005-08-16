/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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
package com.exedio.cope;


public abstract class Attribute extends Feature
{
	private final boolean readOnly;
	private final boolean notNull;

	protected Attribute(final Option option)
	{
		this.readOnly = option.readOnly;
		this.notNull = option.notNull;
	}
	
	public final boolean isReadOnly()
	{
		return readOnly;
	}
	
	public final boolean isNotNull()
	{
		return notNull;
	}
	

	// second initialization phase ---------------------------------------------------

	private Column column;
	
	void initialize(final Type type, final String name)
	{
		super.initialize(type, name);
		type.registerInitialization(this);
	}
	
	final void materialize(final Table table)
	{
		if(table==null)
			throw new NullPointerException();
		if(this.column!=null)
			throw new RuntimeException();

		this.column = createColumn(table, getName(), notNull);
	}
	
	final Column getColumn()
	{
		if(this.column==null)
			throw new RuntimeException();

		return column;
	}
	
	public final String toString()
	{
		// should be precomputed
		final StringBuffer buf = new StringBuffer();
		buf.append(super.toString());
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

		return buf.toString();
	}
	
	protected abstract Column createColumn(Table table, String name, boolean notNull);
	
	public static class Option
	{
		public final boolean readOnly;
		public final boolean notNull;
		public final boolean unique;

		Option(final boolean readOnly, final boolean notNull, final boolean unique)
		{
			this.readOnly = readOnly;
			this.notNull = notNull;
			this.unique = unique;
		}
	}
	
}


