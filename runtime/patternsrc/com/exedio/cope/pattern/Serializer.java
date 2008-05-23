/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.pattern;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.exedio.cope.Cope;
import com.exedio.cope.DataField;
import com.exedio.cope.DateField;
import com.exedio.cope.Field;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.Item;
import com.exedio.cope.LengthViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.StringField;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.instrument.Wrapper;

/**
 * Stores a java object by serialization - use with care!
 * <p>
 * Stores serializable objects into a backing
 * {@link DataField}.
 * BEWARE:
 * Generally this is not a good idea.
 * In contrast to normal fields, such as
 * {@link StringField}, {@link DateField},
 * etc. there is no searching, order by or caching.
 * The main purpose is to maintain database compatibility
 * to legacy systems.
 */
public final class Serializer<E> extends Pattern implements Settable<E>
{
	private final Class<E> valueClass;
	private final DataField source;

	private Serializer(final Class<E> valueClass, final DataField source)
	{
		if(valueClass==null)
			throw new NullPointerException("valueClass must not be null");
		
		this.valueClass = valueClass;
		this.source = source;

		registerSource(source, "Data");
	}
	
	/**
	 * @deprecated use {@link #optional()} instead.
	 */
	@Deprecated
	private Serializer(final Class<E> valueClass, final Field.Option option)
	{
		this(valueClass, new DataField(option));
	}
	
	public static final <E> Serializer<E> newSerializer(final Class<E> valueClass, final DataField source)
	{
		return new Serializer<E>(valueClass, source);
	}
	
	public static final <E> Serializer<E> newSerializer(final Class<E> valueClass)
	{
		return new Serializer<E>(valueClass, new DataField());
	}
	
	/**
	 * @deprecated use {@link #optional()} instead.
	 */
	@Deprecated
	public static final <E> Serializer<E> newSerializer(final Class<E> valueClass, final Field.Option option)
	{
		return new Serializer<E>(valueClass, option);
	}
	
	public Serializer<E> optional()
	{
		return new Serializer<E>(valueClass, source.optional());
	}
	
	// TODO allow setting of length of DataField
	
	public DataField getSource()
	{
		return source;
	}
	
	public boolean isInitial()
	{
		return source.isInitial();
	}
	
	public boolean isFinal()
	{
		return source.isFinal();
	}
	
	public Class getInitialType()
	{
		return valueClass;
	}
	
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		return source.getInitialExceptions();
	}
	
	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());
		
		final Class initialType = getInitialType();
		
		result.add(
			new Wrapper("get").
			addComment("Returns the value of {0}.").
			setReturn(initialType));
		
		if(!isFinal())
		{
			result.add(
				new Wrapper("set").
				addComment("Sets a new value for {0}.").
				addThrows(getInitialExceptions()).
				addParameter(initialType));
		}
			
		return Collections.unmodifiableList(result);
	}
	
	public E get(final Item item)
	{
		final byte[] buf = source.getArray(item);
		
		if(buf==null)
			return null;

		final E result;
		ObjectInputStream ois = null;
		try
		{
			final ByteArrayInputStream bis = new ByteArrayInputStream(buf);
			ois = new ObjectInputStream(bis);
			result = Cope.verboseCast(valueClass, ois.readObject());
			ois.close();
			ois = null;
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		catch(ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			if(ois!=null)
			{
				try
				{
					ois.close();
				}
				catch(IOException e)
				{
					throw new RuntimeException(e);
				}
			}
		}
		
		return result;
	}
	
	public void set(final Item item, final E value)
		throws
			UniqueViolationException,
			MandatoryViolationException,
			LengthViolationException,
			FinalViolationException,
			ClassCastException
	{
		source.set(item, serialize(value));
	}
	
	public SetValue<E> map(final E value)
	{
		return new SetValue<E>(this, value);
	}
	
	public SetValue[] execute(final E value, final Item exceptionItem)
	{
		return new SetValue[]{ source.map(serialize(value)) };
	}
	
	private byte[] serialize(final E value)
	{
		if(value==null)
			return null;
		
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;
		try
		{
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(value);
			oos.close();
			oos = null;
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			if(oos!=null)
			{
				try
				{
					oos.close();
				}
				catch(IOException e)
				{
					throw new RuntimeException(e);
				}
			}
		}
		return bos.toByteArray();
	}
}
