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

package com.exedio.cope;

import com.exedio.cope.util.Day;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

final class SimpleSelectType<E> implements SelectType<E>
{
	final Class<E> javaClass;

	private SimpleSelectType(final Class<E> javaClass)
	{
		this.javaClass = javaClass;
		assert javaClass!=null;
	}

	@Override
	public Class<E> getJavaClass()
	{
		return javaClass;
	}

	@Override
	public String toString()
	{
		return javaClass.getName();
	}


	static final SimpleSelectType<String > STRING  = new SimpleSelectType<>(String .class);
	static final SimpleSelectType<Boolean> BOOLEAN = new SimpleSelectType<>(Boolean.class);
	static final SimpleSelectType<Integer> INTEGER = new SimpleSelectType<>(Integer.class);
	static final SimpleSelectType<Long   > LONG    = new SimpleSelectType<>(Long   .class);
	static final SimpleSelectType<Double > DOUBLE  = new SimpleSelectType<>(Double .class);
	static final SimpleSelectType<Date   > DATE    = new SimpleSelectType<>(Date   .class);
	static final SimpleSelectType<Day    > DAY     = new SimpleSelectType<>(Day    .class);

	private static final SimpleSelectType<?>[] instances = {STRING, BOOLEAN, INTEGER, LONG, DOUBLE, DATE, DAY};


	// serialization -------------

	@Serial
	private static final long serialVersionUID = 1l;

	/**
	 * <a href="https://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/input.html#5903">See Spec</a>
	 */
	@Serial
	private Object writeReplace()
	{
		return new Serialized(javaClass);
	}

	/**
	 * Block malicious data streams.
	 * @see #writeReplace()
	 */
	@Serial
	private void readObject(@SuppressWarnings("unused") final ObjectInputStream ois) throws InvalidObjectException
	{
		throw new InvalidObjectException("required " + Serialized.class);
	}

	/**
	 * Block malicious data streams.
	 * @see #writeReplace()
	 */
	@Serial
	private Object readResolve() throws InvalidObjectException
	{
		throw new InvalidObjectException("required " + Serialized.class);
	}

	private static final class Serialized implements Serializable
	{
		@Serial
		private static final long serialVersionUID = 1l;
		private final Class<?> javaClass;

		private Serialized(final Class<?> javaClass)
		{
			this.javaClass = javaClass;
		}

		/**
		 * <a href="https://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/input.html#5903">See Spec</a>
		 */
		@Serial
		private Object readResolve() throws InvalidObjectException
		{
			for(final SimpleSelectType<?> candidate : instances)
				if(javaClass.equals(candidate.javaClass))
					return candidate;

			throw new InvalidObjectException("no SimpleSelectType for " + javaClass);
		}
	}
}
