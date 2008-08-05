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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.exedio.cope.Condition;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.Function;
import com.exedio.cope.Item;
import com.exedio.cope.Join;
import com.exedio.cope.StringLengthViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.StringField;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.Field.Option;
import com.exedio.cope.instrument.Wrapper;

public abstract class Hash extends Pattern implements Settable<String>
{
	private final StringField storage;

	public Hash(final StringField storage)
	{
		if(storage==null)
			throw new NullPointerException("hash storage must not be null");

		registerSource(this.storage = storage, "Hash");
	}
	
	public Hash()
	{
		this(new StringField());
	}
	
	public final StringField getStorage()
	{
		return storage;
	}
	
	public final boolean isInitial()
	{
		return storage.isInitial();
	}
	
	public final boolean isFinal()
	{
		return storage.isFinal();
	}
	
	public final boolean isMandatory()
	{
		return storage.isMandatory();
	}
	
	public Class getInitialType()
	{
		return String.class;
	}
	
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		return storage.getInitialExceptions();
	}
	
	/**
	 * @param plainText the text to be hashed. Is never null.
	 * @return the hash of plainText. Must never return null.
	 */
	public abstract String hash(String plainText);
	
	public abstract Hash optional();
	
	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());
		
		result.add(
			new Wrapper("check").
			addComment("Returns whether the given value corresponds to the hash in {0}.").
			setReturn(boolean.class).
			addParameter(String.class));
		
		final Set<Class<? extends Throwable>> exceptions = getInitialExceptions();
		result.add(
			new Wrapper("set").
			addComment("Sets a new value for {0}.").
			addThrows(exceptions).
			addParameter(String.class));
		
		result.add(
			new Wrapper("getHash").
			addComment("Returns the encoded hash value for hash {0}.").
			setReturn(String.class));
		
		result.add(
			new Wrapper("setHash").
			addComment("Sets the encoded hash value for hash {0}.").
			addThrows(exceptions).
			addParameter(String.class));
		
		return Collections.unmodifiableList(result);
	}
	
	public final void set(final Item item, final String plainText)
		throws
			UniqueViolationException,
			MandatoryViolationException,
			StringLengthViolationException,
			FinalViolationException
	{
		storage.set(item, plainText!=null ? hash(plainText) : null);
	}
	
	public final boolean check(final Item item, final String actualPlainText)
	{
		final String expectedHash = storage.get(item);
		if(actualPlainText!=null)
			return hash(actualPlainText).equals(expectedHash); // hash(String) must not return null
		else
			return expectedHash==null;
	}
	
	public final SetValue<String> map(final String value)
	{
		return new SetValue<String>(this, value);
	}
	
	public final SetValue[] execute(final String value, final Item exceptionItem)
	{
		return new SetValue[]{ storage.map(value!=null ? hash(value) : null) };
	}
	
	public final String getHash(final Item item)
	{
		return storage.get(item);
	}
	
	public final void setHash(final Item item, final String hash)
	{
		storage.set(item, hash);
	}
	
	public final Condition equal(final String value)
	{
		return value!=null ? storage.equal(hash(value)) : storage.isNull();
	}
	
	public final Condition equal(final Join join, final String value)
	{
		final Function<String> boundStorage = storage.bind(join);
		return value!=null ? boundStorage.equal(hash(value)) : boundStorage.isNull();
	}

	public final Condition notEqual(final String value)
	{
		return value!=null ? storage.notEqual(hash(value)) : storage.isNotNull();
	}
	
	// ------------------- deprecated stuff -------------------
	
	/**
	 * @deprecated use {@link #optional()} instead.
	 */
	@Deprecated
	public Hash(final Option storageOption)
	{
		this(new StringField(storageOption));
	}
}
