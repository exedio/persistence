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

package com.exedio.dsmf;

import static java.lang.Math.toIntExact;

public final class Sequence extends Node
{
	final String name;
	private final Field<Type> type;
	private final Field<Long> start;

	/**
	 * @deprecated Use {@link Schema#newSequence(String,Sequence.Type,long)} instead
	 */
	@Deprecated
	@SuppressWarnings("UnnecessarilyQualifiedInnerClassAccess") // otherwise javadoc issues warnings
	public Sequence(final Schema schema, final String name, final Type type, final long start)
	{
		this(schema, name, type, start, true);
	}

	Sequence(final Schema schema, final String name, final Type type, final long start, final boolean required)
	{
		super(schema.dialect, schema.connectionProvider, required);

		if(name==null)
			throw new RuntimeException();

		this.name = name;
		this.type = new Field<>(type, required);
		this.start = new Field<>(start, required);

		//noinspection ThisEscapedInObjectConstruction
		schema.register(this);
	}

	public String getName()
	{
		return name;
	}

	public Type getType()
	{
		return type.get();
	}

	/**
	 * @deprecated Use {@link #getStartL()} instead
	 */
	@Deprecated
	public int getStart()
	{
		return toIntExact(getStartL());
	}

	public long getStartL()
	{
		return start.get();
	}

	void notifyExists(final Type existingType)
	{
		notifyExistsNode();
		type.notifyExists(existingType);
	}

	void notifyExists(final Type existingType, final long existingStart)
	{
		notifyExists(existingType);
		start.notifyExists(existingStart);
	}

	@Override
	Result computeResult()
	{
		if(!exists())
			return Result.missing;

		if(!required())
			return Result.unusedWarning;

		if(type.mismatches())
			return Result.error(
					"unexpected type " + type.getExisting());

		if(start.mismatches())
			return Result.error(
					"unexpected start " + start.getExisting());

		return Result.ok;
	}

	public void create()
	{
		create((StatementListener)null);
	}

	public void create(final StatementListener listener)
	{
		final StringBuilder bf = new StringBuilder();
		create(bf);
		executeSQL(bf.toString(), listener);
	}

	void create(final StringBuilder bf)
	{
		dialect.createSequence(bf, quoteName(name), type.get(), start.get());
	}

	public void drop()
	{
		drop((StatementListener)null);
	}

	public void drop(final StatementListener listener)
	{
		final StringBuilder bf = new StringBuilder();
		drop(bf);
		executeSQL(bf.toString(), listener);
	}

	void drop(final StringBuilder bf)
	{
		dialect.dropSequence(bf, quoteName(name));
	}

	@Override
	public String toString()
	{
		return name;
	}

	public enum Type
	{
		bit31(Integer.MAX_VALUE),
		bit63(Long   .MAX_VALUE);

		public final long MAX_VALUE;

		Type(final long MAX_VALUE)
		{
			this.MAX_VALUE = MAX_VALUE;
		}

		@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
		static Type fromMaxValueExact(final long maxValue)
		{
			if(maxValue==Integer.MAX_VALUE)
				return bit31;
			else if(maxValue==Long.MAX_VALUE)
				return bit63;
			else
				throw new IllegalArgumentException("" + maxValue);
		}

		public static Type fromMaxValueLenient(final long maxValue)
		{
			if(maxValue<0)
				throw new IllegalArgumentException("" + maxValue);

			return (maxValue<=Integer.MAX_VALUE) ? bit31 : bit63;
		}
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #getStart()} instead
	 */
	@Deprecated
	public int getStartWith()
	{
		return getStart();
	}
}
