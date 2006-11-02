/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import java.util.SortedSet;

import com.exedio.cope.Condition;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.Item;
import com.exedio.cope.Join;
import com.exedio.cope.LengthViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.StringField;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.Field.Option;

public abstract class Hash extends Pattern implements Settable<String>
{
	private final StringField storage;

	public Hash(final StringField storage)
	{
		if(storage==null)
			throw new NullPointerException("hash storage must not be null");

		registerSource(this.storage = storage);
	}
	
	public Hash(final Option storageOption)
	{
		this(new StringField(storageOption));
	}

	@Override
	public void initialize()
	{
		if(!storage.isInitialized())
			initialize(storage, getName()+"Hash");
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
	
	public SortedSet<Class> getSetterExceptions()
	{
		return storage.getSetterExceptions();
	}
	
	public abstract String hash(String plainText);
	
	public final void set(final Item item, final String plainText)
		throws
			UniqueViolationException,
			MandatoryViolationException,
			LengthViolationException,
			FinalViolationException
	{
		storage.set(item, hash(plainText));
	}
	
	public final boolean check(final Item item, final String actualPlainText)
	{
		final String expectedHash = storage.get(item);
		final String actualHash = hash(actualPlainText);
		if(expectedHash==null)
			return actualHash==null;
		else
			return expectedHash.equals(actualHash);
	}
	
	public final SetValue<String> map(final String value)
	{
		return new SetValue<String>(this, value);
	}
	
	public final SetValue[] execute(final String value, final Item exceptionItem)
	{
		return new SetValue[]{ storage.map(hash(value)) };
	}
	
	public final Condition equal(final String value)
	{
		return storage.equal(hash(value));
	}
	
	public final Condition equal(final Join join, final String value)
	{
		return storage.bind(join).equal(hash(value));
	}

	public final Condition notEqual(final String value)
	{
		return storage.notEqual(hash(value));
	}
	
}
